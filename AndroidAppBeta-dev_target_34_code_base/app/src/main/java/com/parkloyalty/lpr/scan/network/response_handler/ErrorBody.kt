package com.parkloyalty.lpr.scan.network.response_handler

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Common shape for error responses. Adapt fields to your backend structure.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class ErrorBody(
    @JsonProperty("message") val message: String? = null,
    @JsonProperty("errors") val errors: Map<String, List<String>>? = null,
    // raw body fallback
    val raw: String? = null
)
