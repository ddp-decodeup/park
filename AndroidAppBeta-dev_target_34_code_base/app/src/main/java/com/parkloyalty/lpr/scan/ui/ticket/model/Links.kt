package com.parkloyalty.lpr.scan.ui.ticket.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class Links(
    @field:JsonProperty("img_1")
    @get:JsonProperty("img_1")
    var img1: String? = null,
    @field:JsonProperty("img_2")
    @get:JsonProperty("img_2")
    var img2: String? = null,
    @field:JsonProperty("img_3")
    @get:JsonProperty("img_3")
    var img3: String? = null,
    @field:JsonProperty("img_4")
    @get:JsonProperty("img_4")
    var img4: String? = null,
    @field:JsonProperty("img_5")
    @get:JsonProperty("img_5")
    var img5: String? = null,
    @field:JsonProperty("img_6")
    @get:JsonProperty("img_6")
    var img6: String? = null,
    @field:JsonProperty("img_7")
    @get:JsonProperty("img_7")
    var img7: String? = null,
    @field:JsonProperty("img_8")
    @get:JsonProperty("img_8")
    var img8: String? = null,
    @field:JsonProperty("img_9")
    @get:JsonProperty("img_9")
    var img9: String? = null,
    @field:JsonProperty("img_10")
    @get:JsonProperty("img_10")
    var img10: String? = null,
    @field:JsonProperty("img_11")
    @get:JsonProperty("img_11")
    var img11: String? = null,
    @field:JsonProperty("img_12")
    @get:JsonProperty("img_12")
    var img12: String? = null
) : Parcelable
