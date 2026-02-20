package com.parkloyalty.lpr.scan.network.response_handler
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeResponse
import kotlinx.coroutines.withContext


// kotlin
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.cfg.CoercionAction
import com.fasterxml.jackson.databind.cfg.CoercionInputShape
import com.fasterxml.jackson.databind.type.LogicalType
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

//val instance: ObjectMapper = ObjectMapper()
//    .registerKotlinModule()
//    .setDefaultPropertyInclusion(
//        JsonInclude.Value.construct(
//            JsonInclude.Include.NON_NULL,
//            JsonInclude.Include.NON_NULL
//        )
//    )
//    // Strict deserialization: fail on unknown props and other mapping problems
//    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
//    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false)
//    .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
//    .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
//    .configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true)
//    .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true)
//    .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true)
//    .also { mapper ->
//        // Disable permissive coercion: force failure when types don't match (booleans/numbers/strings)
//        mapper.coercionConfigFor(LogicalType.Boolean)
//            .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
//            .setCoercion(CoercionInputShape.String, CoercionAction.Fail)
//            .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
//
//        mapper.coercionConfigFor(LogicalType.Integer)
//            .setCoercion(CoercionInputShape.String, CoercionAction.Fail)
//            .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
//
//        mapper.coercionConfigFor(LogicalType.Float)
//            .setCoercion(CoercionInputShape.String, CoercionAction.Fail)
//            .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
//    }
//
//suspend fun <T> safeApiCallJson(
//    dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
//    apiNameTag: String,
//    call: suspend () -> Response<T>
//): NewApiResponse<JsonNode?> {
//    return try {
//        val response = withContext(dispatcher) { call() }
//
//        if (response.isSuccessful) {
//            // try to get raw string (peek raw, then response.body, then serialize parsed body)
//            val rawString: String? = try {
//                response.raw().peekBody(Long.MAX_VALUE).string()
//            } catch (_: Throwable) {
//                null
//            } ?: run {
//                // fallback: if body is String or ResponseBody use it; otherwise serialize body object
//                try {
//                    when (val b = try { response.body() } catch (_: Throwable) { null }) {
//                        is String -> b
//                        is ResponseBody -> try { b.string() } catch (_: Throwable) { null }
//                        null -> null
//                        else -> try { instance.writeValueAsString(b) } catch (_: Throwable) { null }
//                    }
//                } catch (_: Throwable) {
//                    null
//                }
//            }
//
//            if (rawString.isNullOrBlank()) {
//                // no content -> success with null JsonNode
//                NewApiResponse.Success(null, response.code(), apiNameTag)
//            } else {
//                // parse into JsonNode
//                try {
//                    val node: JsonNode = instance.readTree(rawString)
//                    NewApiResponse.Success(node, response.code(), apiNameTag)
//                } catch (parseEx: Throwable) {
//                    // 2xx but couldn't parse into expected JSON shape
//                    NewApiResponse.NotParsed(rawString, response.code(), apiNameTag, parseEx)
//                }
//            }
//        } else {
//            val code = response.code()
//            val error = parseErrorBody(response)
//            NewApiResponse.ApiError(code, error, apiNameTag)
//        }
//    } catch (ioEx: IOException) {
//        NewApiResponse.NetworkError(ioEx, apiNameTag)
//    } catch (t: Throwable) {
//        NewApiResponse.UnknownError(t, apiNameTag)
//    }
//}


suspend fun <T> safeApiCall(
    dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
    apiNameTag: String,
    call: suspend () -> Response<T>
): NewApiResponse<T> {
    return try {
        val response = withContext(dispatcher) { call() }
        if (response.isSuccessful) {
            // body may be null if response had no content - still treat as success with nullable T
            NewApiResponse.Success(response.body(), response.code(), apiNameTag)
        } else {
            val code = response.code()
            val error = parseErrorBody(response)
            NewApiResponse.ApiError(code, error, apiNameTag)
        }
    } catch (ioEx: IOException) {
        // network failures, timeouts, no connectivity, etc.
        NewApiResponse.NetworkError(ioEx, apiNameTag)
    } catch (t: Throwable) {
        NewApiResponse.UnknownError(t, apiNameTag)
    }
}

/**
 * Variant of safeApiCall that accepts a Response<T> directly and infers the expected type from
 * the generic parameter T (use `inline`+`reified` to allow callers to omit the expectedClass).
 *
 * Behavior notes:
 * - If the response contains a raw String body (T == String), that raw payload is used for
 *   parsing/validation.
 * - If the response body is already parsed into T by Retrofit's converter, the helper will
 *   serialize that object back to JSON using `ObjectMapperProvider.toJson(...)` and use the
 *   result as the "raw" payload for optional validators. This allows callers to provide a
 *   validator that checks the original payload shape when possible. Note: serializing the
 *   parsed object may not preserve certain differences (for example, numeric-vs-boolean) if the
 *   converter already coerced the value; to fully control raw parsing prefer calling an endpoint
 *   that returns Response<String> or Response<ResponseBody>.
 *
 * - `parser` is optional: if provided, it will be invoked with the raw JSON (when available)
 *   to produce the T instance; otherwise the already-parsed `response.body()` is used as-is.
 */
//suspend inline fun <reified T> safeApiCallParsed(
//    dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
//    apiNameTag: String,
//    noinline call: suspend () -> Response<T>,
//    noinline parser: ((String) -> T?)? = null,
//    noinline validator: ((raw: String, parsed: T) -> Throwable?)? = null
//): NewApiResponse<T> {
//    return try {
//        val response = withContext(dispatcher) { call() }
//        if (response.isSuccessful) {
//            val body = try {
//                response.body()
//            } catch (_: Throwable) {
//                null
//            }
//
//            if (body == null) {
//                // empty body - treat as success with null data
//                NewApiResponse.Success(null, response.code(), apiNameTag)
//            } else {
//                // Determine raw payload: if body is String, use it; otherwise try to serialize
//                val rawPayload: String? = when (body) {
//                    is String -> body
//                    else -> try {
//                        instance.writeValueAsString(body)
//                    } catch (_: Throwable) {
//                        null
//                    }
//                }
//
//                // Determine parsed object: prefer parser(raw) if provided and raw is available,
//                // otherwise use the body directly (it's already T)
//                val (parsed, parseError) = if (parser != null && rawPayload != null) {
//                    try {
//                        Pair(parser.invoke(rawPayload), null as Throwable?)
//                    } catch (t: Throwable) {
//                        Pair(null as T?, t)
//                    }
//                } else {
//                    Pair(body as T, null as Throwable?)
//                }
//
//                if (parsed != null) {
//                    // run optional semantic/type validator
//                    val validationError = try {
//                        if (rawPayload != null) validator?.invoke(rawPayload, parsed) else validator?.invoke("", parsed)
//                    } catch (t: Throwable) {
//                        t
//                    }
//
//                    return if (validationError == null) {
//                        NewApiResponse.Success(parsed, response.code(), apiNameTag)
//                    } else {
//                        NewApiResponse.NotParsed(rawPayload ?: "", response.code(), apiNameTag, validationError)
//                    }
//                } else {
//                    NewApiResponse.NotParsed(rawPayload ?: "", response.code(), apiNameTag, parseError)
//                }
//            }
//        } else {
//            val code = response.code()
//            val error = parseErrorBody(response)
//            NewApiResponse.ApiError(code, error, apiNameTag)
//        }
//    } catch (ioEx: IOException) {
//        NewApiResponse.NetworkError(ioEx, apiNameTag)
//    } catch (t: Throwable) {
//        NewApiResponse.UnknownError(t, apiNameTag)
//    }
//}

/**
 * Try to parse Retrofit errorBody using Jackson. Fall back to raw string if parsing fails.
 */
fun <T> parseErrorBody(response: Response<T>): ErrorBody? {
    val eb = try {
        response.errorBody()?.string()
    } catch (_: Throwable) {
        null
    } ?: return null

    return try {
        // Attempt to parse into ErrorBody (JSON)
        val parsed = ObjectMapperProvider.fromJson(eb, ErrorBody::class.java)
        // Fill raw if empty
        if (parsed.message == null && parsed.errors == null) parsed.copy(raw = eb) else parsed.copy(
            raw = eb
        )
    } catch (_: Throwable) {
        // Not JSON — return raw as ErrorBody.raw
        ErrorBody(message = null, errors = null, raw = eb)
    }
}


//suspend inline fun <reified T> safeApiCallPar(
//    dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
//    apiNameTag: String,
//    crossinline call: suspend () -> Response<T>
//): NewApiResponse<T> {
//    return try {
//        val response = withContext(dispatcher) { call() }
//
//        if (response.isSuccessful) {
//            // ✅ Step 1: Read raw body as string
//            val rawString = try {
//                // peekBody lets us re-read safely
//                response.raw().peekBody(Long.MAX_VALUE).string()
//            } catch (_: Throwable) {
//                try {
//                    response.errorBody()?.string() ?: response.raw().body?.string()
//                } catch (_: Throwable) {
//                    null
//                }
//            }
//
//            // ✅ Step 2: If no body, treat as success with null
//            if (rawString.isNullOrBlank()) {
//                return NewApiResponse.Success(response.body(), response.code(), apiNameTag)
//            }
//
//            // ✅ Step 3: Try parsing manually to T using ObjectMapper
//            try {
//                val parsedBody: T = instance.readValue(rawString, WelcomeResponse::class.java) as T
//                NewApiResponse.Success(parsedBody, response.code(), apiNameTag)
//            } catch (parseEx: Throwable) {
//                // ✅ Parsing failed — mismatched model
//                NewApiResponse.NotParsed(rawString ?: "", response.code(), apiNameTag, parseEx)
//            }
//
//        } else {
//            // non-2xx responses
//            val code = response.code()
//            val error = parseErrorBody(response)
//            NewApiResponse.ApiError(code, error, apiNameTag)
//        }
//
//    } catch (ioEx: IOException) {
//        NewApiResponse.NetworkError(ioEx, apiNameTag)
//    } catch (t: Throwable) {
//        NewApiResponse.UnknownError(t, apiNameTag)
//    }
//}

