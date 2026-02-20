package com.parkloyalty.lpr.scan.network

import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.database.NewSingletonDataSet
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.interceptor.ApiLoggingInterceptor
import com.parkloyalty.lpr.scan.network.services.ActivityService
import com.parkloyalty.lpr.scan.network.services.AuthService
import com.parkloyalty.lpr.scan.network.services.CitationService
import com.parkloyalty.lpr.scan.network.services.DatasetService
import com.parkloyalty.lpr.scan.network.services.EventService
import com.parkloyalty.lpr.scan.network.services.GuideEnforcementService
import com.parkloyalty.lpr.scan.network.services.InventoryService
import com.parkloyalty.lpr.scan.network.services.LocationService
import com.parkloyalty.lpr.scan.network.services.MediaService
import com.parkloyalty.lpr.scan.network.services.MunicipalCitationService
import com.parkloyalty.lpr.scan.network.services.RecaptchaApiService
import com.parkloyalty.lpr.scan.network.services.ReportService
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.NewConstructLayoutBuilder
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.SettingsUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/* this class is responsible for network calls */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    private val mUserAuthToken: String? = null
    private var mHostName = ""

    @Provides
    @Singleton
    @Named("AppRetrofit")
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

        val builder = Retrofit.Builder()
        builder.baseUrl(BuildConfig.BASE_API_URL)
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        builder.client(okHttpClient)
        builder.addConverterFactory(JacksonConverterFactory.create(objectMapper))
        return builder.build()
    }

//    @Provides
//    @Singleton
//    fun provideObjectMapper(): ObjectMapper {
//        return ObjectMapper().registerKotlinModule()
//
////        return ObjectMapper()
////            .registerKotlinModule()
////            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
////            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//    }
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(
//        objectMapper: ObjectMapper,
//        okHttpClient: OkHttpClient
//    ): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BuildConfig.BASE_API_URL)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .client(okHttpClient)
//            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
//            .build()
//    }

    /**
     * set ssl certificate at dynamic from real time database (fire-base)
     * there is two way to set ssl pinning in dynamic
     * 1. when app is lunch at that time, call fire-base method for getting update ssl pin
     * 2. when getting error in any api at that time also update the ssl pin
     */
    @Provides
    @Singleton
    fun getApiCallInterface(@Named("AppRetrofit") retrofit: Retrofit): NetworkAPIServices {
        return retrofit.create(NetworkAPIServices::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        sharedPref: SharedPref
    ): OkHttpClient {
        try {
            val uri = URI(BuildConfig.BASE_API_URL)
            mHostName = uri.host
            LogUtil.printLog("Host name", mHostName)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val httpClient = OkHttpClient.Builder()

        //Adding this interceptor for retrying the api call if fails by any case
//        httpClient.addInterceptor(RetryingInterceptor())
//        httpClient.addInterceptor(NetworkLatencyInterceptor())

        try {
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                var request: Request? = null
                var response: Response? = null
                val url = original.url.toString()
                val requestBuilder = original.newBuilder()
                requestBuilder.addHeader("Content-Type", CONTENT_TYPE)
                requestBuilder.addHeader(
                    "token",
                    sharedPref.read(SharedPrefKey.ACCESS_TOKEN, "").nullSafety()
                )
                requestBuilder.method(original.method, original.body)
                request = requestBuilder.build()
                response = chain.proceed(request)
                LogUtil.printLogHeader(
                    "Header ",
                    "token " + sharedPref.read(SharedPrefKey.ACCESS_TOKEN, "")
                )

                response
            }
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Adding logging interceptor at last so it will print all of the above
        if (LogUtil.isEnableAPILogs) {
            // Add custom comprehensive API logging interceptor
            httpClient.addInterceptor(ApiLoggingInterceptor())

            // Optionally add OkHttp's built-in logging interceptor as well for additional details
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(interceptor)
        }

        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideService(networkAPIServices: NetworkAPIServices): Service {
        return Service(networkAPIServices)
    }

    companion object {
        private val TAG = NetworkModule::class.java.simpleName
        private const val CONTENT_TYPE = "application/json"
        private const val CONTENT_TYPE_MULTIPART = "multipart/form-data"
        private const val ACCEPT = "application/json"
    }

    @Provides
    @Singleton
    fun provideActivityService(@Named("AppRetrofit") retrofit: Retrofit): ActivityService {
        return retrofit.create(ActivityService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(@Named("AppRetrofit") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideCitationService(@Named("AppRetrofit") retrofit: Retrofit): CitationService {
        return retrofit.create(CitationService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatasetService(@Named("AppRetrofit") retrofit: Retrofit): DatasetService {
        return retrofit.create(DatasetService::class.java)
    }

    @Provides
    @Singleton
    fun provideEventService(@Named("AppRetrofit") retrofit: Retrofit): EventService {
        return retrofit.create(EventService::class.java)
    }

    @Provides
    @Singleton
    fun provideGuideEnforcementService(@Named("AppRetrofit") retrofit: Retrofit): GuideEnforcementService {
        return retrofit.create(GuideEnforcementService::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryService(@Named("AppRetrofit") retrofit: Retrofit): InventoryService {
        return retrofit.create(InventoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationService(@Named("AppRetrofit") retrofit: Retrofit): LocationService {
        return retrofit.create(LocationService::class.java)
    }

    @Provides
    @Singleton
    fun provideMediaService(@Named("AppRetrofit") retrofit: Retrofit): MediaService {
        return retrofit.create(MediaService::class.java)
    }

    @Provides
    @Singleton
    fun provideMunicipalCitationService(@Named("AppRetrofit") retrofit: Retrofit): MunicipalCitationService {
        return retrofit.create(MunicipalCitationService::class.java)
    }

    @Provides
    @Singleton
    fun provideReportService(@Named("AppRetrofit") retrofit: Retrofit): ReportService {
        return retrofit.create(ReportService::class.java)
    }

    @Provides
    @Singleton
    @Named("RecaptchaRetrofit")
    fun provideRecaptchaRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

        val builder = Retrofit.Builder()
        builder.baseUrl("https://www.google.com/")
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        builder.client(okHttpClient)
        builder.addConverterFactory(JacksonConverterFactory.create(objectMapper))
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRecaptchaApiService(@Named("RecaptchaRetrofit") recaptchaRetrofit: Retrofit): RecaptchaApiService {
        return recaptchaRetrofit.create(RecaptchaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewConstructLayoutBuilder(): NewConstructLayoutBuilder {
        return NewConstructLayoutBuilder()
    }

    @Provides
    @Singleton
    fun provideSettingsUtils(newSingletonDataSet: NewSingletonDataSet): SettingsUtils {
        return SettingsUtils(newSingletonDataSet)
    }
}