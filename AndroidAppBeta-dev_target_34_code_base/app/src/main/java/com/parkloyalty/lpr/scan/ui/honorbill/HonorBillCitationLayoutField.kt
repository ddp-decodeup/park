package com.parkloyalty.lpr.scan.ui.honorbill

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class HonorBillCitationLayoutField(
    @field:JsonProperty("aligned")
    @get:JsonProperty("aligned")
    var aligned: String? = null,

    @field:JsonProperty("clickable")
    @get:JsonProperty("clickable")
    var clickable: String? = null,

    @field:JsonProperty("collection_name")
    @get:JsonProperty("collection_name")
    var collectionName: String? = null,

    @field:JsonProperty("field_name")
    @get:JsonProperty("field_name")
    var fieldName: String? = null,

    @field:JsonProperty("is_editable")
    @get:JsonProperty("is_editable")
    var isEditable: Boolean? = null,

    @field:JsonProperty("is_required")
    @get:JsonProperty("is_required")
    var isRequired: Boolean? = null,

    @field:JsonProperty("name")
    @get:JsonProperty("name")
    var name: String? = null,

    @field:JsonProperty("print")
    @get:JsonProperty("print")
    var print: Boolean? = null,

    @field:JsonProperty("repr")
    @get:JsonProperty("repr")
    var repr: String? = null,

    @field:JsonProperty("tag")
    @get:JsonProperty("tag")
    var tag: String? = null,

    @field:JsonProperty("type")
    @get:JsonProperty("type")
    var type: String? = null,

    @field:JsonProperty("default_value")
    @get:JsonProperty("default_value")
    var mDefaultValue: String? = null,

    @field:JsonProperty("dependant_field")
    @get:JsonProperty("dependant_field")
    var mDependantField: Boolean? = null,

    @field:JsonProperty("calculated_field")
    @get:JsonProperty("calculated_field")
    var mCalculatedField: String? = null,

    @field:JsonProperty("data_type_validation")
    @get:JsonProperty("data_type_validation")
    var mDataTypeValidation: String? = null,

    @field:JsonProperty("delete_enable")
    @get:JsonProperty("delete_enable")
    var mDeleteEnable: String? = null,

    @field:JsonProperty("dependent_field")
    @get:JsonProperty("dependent_field")
    var mDependentField: String? = null,

    @field:JsonProperty("max_length")
    @get:JsonProperty("max_length")
    var mMaxLength: String? = null,

    @field:JsonProperty("min_length")
    @get:JsonProperty("min_length")
    var mMinLength: String? = null,

    @field:JsonProperty("print_layout_order")
    @get:JsonProperty("print_layout_order")
    var mPrintLayoutOrder: String? = null,

    @field:JsonProperty("form_layout_order")
    @get:JsonProperty("form_layout_order")
    var mFormLayoutOrder: String? = null,

    @field:JsonProperty("position_x_y_font")
    @get:JsonProperty("position_x_y_font")
    var mPositionXYFont: String? = null,

    @field:JsonProperty("display_column")
    @get:JsonProperty("display_column")
    var mDisplayColumn: String? = null,

    @field:JsonProperty("options")
    @get:JsonProperty("options")
    var optionsCheckBox: List<HonorBillCitationOptionsCheckBox>? = null
) : Parcelable
