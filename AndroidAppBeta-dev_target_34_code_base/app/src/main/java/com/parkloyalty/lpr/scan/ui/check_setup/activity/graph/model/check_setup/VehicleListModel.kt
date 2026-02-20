package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class VehicleListModel(
    @field:JsonProperty("off_name_first")
    @get:JsonProperty("off_name_first")
    var offNameFirst: String? = null,

    @field:JsonProperty("off_type_first")
    @get:JsonProperty("off_type_first")
    var offTypeFirst: String? = null,

    @field:JsonProperty("print_order")
    @get:JsonProperty("print_order")
    var mPrintOrder: Double = 0.0,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: Int = 0,

    @field:JsonProperty("check_box_value")
    @get:JsonProperty("check_box_value")
    var checkBoxValue: String? = null,

    @field:JsonProperty("fontSize")
    @get:JsonProperty("fontSize")
    var mFontSize: String? = "",

    @field:JsonProperty("fontSizeInt")
    @get:JsonProperty("fontSizeInt")
    var mFontSizeInt: Int? = null,

    @field:JsonProperty("textAlignment")
    @get:JsonProperty("textAlignment")
    var mTextAlignment: String? = null,

    @field:JsonProperty("axis_x")
    @get:JsonProperty("axis_x")
    var mAxisX: Double = 0.0,

    @field:JsonProperty("axis_y")
    @get:JsonProperty("axis_y")
    var mAxisY: Double = 0.0,

    @field:JsonProperty("section_header")
    @get:JsonProperty("section_header")
    var mSectionHeader: String = "",

    @field:JsonProperty("horizontal_colon")
    @get:JsonProperty("horizontal_colon")
    var mHorizontalColon: Int = 0,

    @field:JsonProperty("no_box")
    @get:JsonProperty("no_box")
    var mNoBox: Int = 0,

    @field:JsonProperty("section_type")
    @get:JsonProperty("section_type")
    var sectionType: String? = null,

    @field:JsonProperty("font_size_for_name_xf_printer")
    @get:JsonProperty("font_size_for_name_xf_printer")
    var fontSizeForNameXFPrinter: Int? = null,

    @field:JsonProperty("font_size_for_xf_type_printer")
    @get:JsonProperty("font_size_for_xf_type_printer")
    var fontSizeForTypeXFPrinter: Int? = null,

    @field:JsonProperty("font_type_for_xf_printer")
    @get:JsonProperty("font_type_for_xf_printer")
    var fontTypeForXFPrinter: Int? = null
) : Parcelable
