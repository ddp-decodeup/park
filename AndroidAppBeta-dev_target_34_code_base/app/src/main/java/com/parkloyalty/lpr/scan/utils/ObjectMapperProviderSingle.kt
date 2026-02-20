//package com.parkloyalty.lpr.scan.utils
//
//import com.fasterxml.jackson.core.type.TypeReference
//import com.fasterxml.jackson.databind.ObjectMapper
//import javax.inject.Inject
//
//class ObjectMapperProviderSingle @Inject constructor(
//    private val objectMapper: ObjectMapper
//) {
//    // Turns Json string to Object of given class
//    fun <T> fromJson(json: String, clazz: Class<T>): T {
//        return objectMapper.readValue(json, clazz)
//    }
//
//    // Turns Json string to Object using a TypeReference (useful for List<T>, Map<K,V>, etc.)
//    fun <T> fromJson(json: String, typeRef: TypeReference<T>): T {
//        return objectMapper.readValue(json, typeRef)
//    }
//
//    // Safe (nullable) variant that returns null on parse error
//    fun <T> fromJsonOrNull(json: String, typeRef: TypeReference<T>): T? {
//        return try {
//            objectMapper.readValue(json, typeRef)
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    // Kotlin-friendly inline reified helper so you can call: fromJson<List<MyModel>>(json)
//    inline fun <reified T> fromJson(json: String): T {
//        return objectMapper.readValue(json, object : TypeReference<T>() {})
//    }
//
//    // Nullable reified helper
//    inline fun <reified T> fromJsonOrNull(json: String): T? {
//        return try {
//            objectMapper.readValue(json, object : TypeReference<T>() {})
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    // Convenience: serialize object to JSON string
//    fun toJson(value: Any): String = objectMapper.writeValueAsString(value)
//
//    /**
//     * Usage examples:
//     *
//     * // For concrete class
//     * val model: MyModel = ObjectMapperProvider.fromJson(jsonString, MyModel::class.java)
//     *
//     * // For List<T> using TypeReference
//     * val list: List<DatasetResponse> = ObjectMapperProvider.fromJson(
//     *     jsonString,
//     *     object : TypeReference<List<DatasetResponse>>() {}
//     * )
//     *
//     * // Or using the Kotlin reified helper
//     * val list2: List<DatasetResponse> = ObjectMapperProvider.fromJson<List<DatasetResponse>>(jsonString)
//     *
//     * // Safe (nullable) usage - returns null if parsing fails
//     * val maybeList: List<DatasetResponse>? = ObjectMapperProvider.fromJsonOrNull<List<DatasetResponse>>(jsonString)
//     */
//}
