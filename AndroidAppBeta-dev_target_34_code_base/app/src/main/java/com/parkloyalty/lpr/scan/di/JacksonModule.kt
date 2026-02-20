//package com.parkloyalty.lpr.scan.di
//
//import com.fasterxml.jackson.annotation.JsonInclude
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.module.kotlin.registerKotlinModule
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//// JacksonModule.kt
//@Module
//@InstallIn(SingletonComponent::class)
//object JacksonModule {
//
//    @Provides
//    @Singleton
//    fun provideObjectMapper(): ObjectMapper {
//        val mapper = ObjectMapper()
//            .registerKotlinModule()
//            .setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
//            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
//
//
//        // Lightweight warm-up here to initialize internal caches/reflection.
//        // Keep it tiny to avoid heavy cost during cold start, but enough to trigger introspection.
//        try {
//            mapper.writeValueAsString(listOf(mapOf("a" to "b")))
//            mapper.readValue("""[{"a":"b"}]""", mapper.typeFactory.constructCollectionType(List::class.java, Map::class.java))
//        } catch (e: Exception) {
//            // ignore
//        }
//
//        return mapper
//    }
//}
