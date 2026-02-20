package com.parkloyalty.lpr.scan.ui.check_setup.model.timing

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class TimingMarkBulkRequest(
    @field:JsonProperty("mark_ids")
    @get:JsonProperty("mark_ids")
    var markIds: List<String?>? = null,
    @field:JsonProperty("arrival_status")
    @get:JsonProperty("arrival_status")
    var arrivalStatus: String? = null
) : Parcelable
