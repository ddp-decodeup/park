package com.parkloyalty.lpr.scan.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.cfg.CoercionAction
import com.fasterxml.jackson.databind.cfg.CoercionInputShape
import com.fasterxml.jackson.databind.type.LogicalType
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.util.LogUtil

/**
 * Singleton provider for ObjectMapper with optimal configuration
 */
object ObjectMapperProvider {
    val instance: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true).also { mapper ->
            if (LogUtil.isNewJacksonInstance.nullSafety()) {
                // Disable permissive coercion: force failure when types don't match (booleans/numbers/strings)
                mapper.coercionConfigFor(LogicalType.Boolean)
                    .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.String, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)

                mapper.coercionConfigFor(LogicalType.Integer)
                    .setCoercion(CoercionInputShape.String, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.Float, CoercionAction.TryConvert)

                mapper.coercionConfigFor(LogicalType.Float)
                    .setCoercion(CoercionInputShape.String, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.Integer, CoercionAction.TryConvert)
            }
        }

//    val instance: ObjectMapper = ObjectMapper()
//        .registerKotlinModule()
//        .setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
//        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

    // Local helper to convert responseModel into typed response safely
    fun <T> parseAs(value: Any,clazz: Class<T>): T? {
        return try {
            fromJson(toJson(value), clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun <T> parseFromStringAs(value: String,clazz: Class<T>): T? {
        return try {
            fromJson(value, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Turns Json string to Object of given class
    fun <T> fromJson(json: String, clazz: Class<T>): T {
        return instance.readValue(json, clazz)
    }

    // Turns Json string to Object using a TypeReference (useful for List<T>, Map<K,V>, etc.)
    fun <T> fromJson(json: String, typeRef: TypeReference<T>): T {
        return instance.readValue(json, typeRef)
    }

    // Safe (nullable) variant that returns null on parse error
    fun <T> fromJsonOrNull(json: String, typeRef: TypeReference<T>): T? {
        return try {
            instance.readValue(json, typeRef)
        } catch (e: Exception) {
            null
        }
    }

    // Kotlin-friendly inline reified helper so you can call: fromJson<List<MyModel>>(json)
    inline fun <reified T> fromJson(json: String): T {
        return instance.readValue(json, object : TypeReference<T>() {})
    }

    // Nullable reified helper
    inline fun <reified T> fromJsonOrNull(json: String): T? {
        return try {
            instance.readValue(json, object : TypeReference<T>() {})
        } catch (e: Exception) {
            null
        }
    }

    // Convenience: serialize object to JSON string
    fun toJson(value: Any): String = instance.writeValueAsString(value)

    /**
     * Usage examples:
     *
     * // For concrete class
     * val model: MyModel = ObjectMapperProvider.fromJson(jsonString, MyModel::class.java)
     *
     * // For List<T> using TypeReference
     * val list: List<DatasetResponse> = ObjectMapperProvider.fromJson(
     *     jsonString,
     *     object : TypeReference<List<DatasetResponse>>() {}
     * )
     *
     * // Or using the Kotlin reified helper
     * val list2: List<DatasetResponse> = ObjectMapperProvider.fromJson<List<DatasetResponse>>(jsonString)
     *
     * // Safe (nullable) usage - returns null if parsing fails
     * val maybeList: List<DatasetResponse>? = ObjectMapperProvider.fromJsonOrNull<List<DatasetResponse>>(jsonString)
     */
}
