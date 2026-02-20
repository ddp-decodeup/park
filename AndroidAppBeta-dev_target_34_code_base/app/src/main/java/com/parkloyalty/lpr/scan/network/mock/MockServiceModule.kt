package com.parkloyalty.lpr.scan.network.mock

import android.content.Context
import com.parkloyalty.lpr.scan.network.mock.MockService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MockServiceModule {
    @Provides
    @Singleton
    fun provideMockService(@ApplicationContext context: Context): MockService {
        return MockService(context)
    }
}
