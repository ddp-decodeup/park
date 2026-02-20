package com.parkloyalty.lpr.scan.ui.login.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class RadioMetadataDeserializer  : JsonDeserializer<RadioMetadata>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): RadioMetadata {
        return when (p.currentToken) {
            JsonToken.START_OBJECT -> {
                val node = p.codec.readTree<JsonNode>(p)
                RadioMetadata(
                    name = node.get("name")?.asText(),
                    screen_name = node.get("screen_name")?.asText()
                )
            }
            JsonToken.VALUE_NUMBER_INT -> {
                RadioMetadata(id = p.intValue)
            }
            else -> RadioMetadata()
        }
    }
}