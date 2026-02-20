package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationVehicleModel(
    @field:JsonProperty("make")
    @get:JsonProperty("make")
    var make: String? = null,
    @field:JsonProperty("makeFullName")
    @get:JsonProperty("makeFullName")
    var makeFullName: String? = null,
    @field:JsonProperty("make_label")
    @get:JsonProperty("make_label")
    var makeLabel: String? = null,
    @field:JsonProperty("make_column")
    @get:JsonProperty("make_column")
    var makeColumn: Int? = 1,
    @field:JsonProperty("makeFullName_lable")
    @get:JsonProperty("makeFullName_lable")
    var makeFullNameLabel: String? = null,
    @field:JsonProperty("status_make")
    @get:JsonProperty("status_make")
    var isStatus_make: Boolean = false,
    @field:JsonProperty("print_order_make")
    @get:JsonProperty("print_order_make")
    var mPrintOrderMake: Double = 0.0,
    @field:JsonProperty("print_layout_order_make")
    @get:JsonProperty("print_layout_order_make")
    var mPrintLayoutOrderMake: String? = null,
    @field:JsonProperty("make_x")
    @get:JsonProperty("make_x")
    var mMakeX: Double = 0.0,
    @field:JsonProperty("make_y")
    @get:JsonProperty("make_y")
    var mMakeY: Double = 0.0,
    @field:JsonProperty("make_font")
    @get:JsonProperty("make_font")
    var mMakeFont: Int = 0,
    @field:JsonProperty("make_column_size")
    @get:JsonProperty("make_column_size")
    var mMakeColumnSize: Int = 0,
    @field:JsonProperty("model")
    @get:JsonProperty("model")
    var model: String? = null,
    @field:JsonProperty("model_lookup_code")
    @get:JsonProperty("model_lookup_code")
    var model_lookup_code: String? = null,
    @field:JsonProperty("model_label")
    @get:JsonProperty("model_label")
    var modelLabel: String? = null,
    @field:JsonProperty("model_column")
    @get:JsonProperty("model_column")
    var modelColumn: Int? = 1,
    @field:JsonProperty("status_model")
    @get:JsonProperty("status_model")
    var isStatus_model: Boolean = false,
    @field:JsonProperty("print_order_model")
    @get:JsonProperty("print_order_model")
    var mPrintOrderModel: Double = 0.0,
    @field:JsonProperty("print_layout_order_model")
    @get:JsonProperty("print_layout_order_model")
    var mPrintLayoutOrderModel: String? = null,
    @field:JsonProperty("model_x")
    @get:JsonProperty("model_x")
    var mModelX: Double = 0.0,
    @field:JsonProperty("model_y")
    @get:JsonProperty("model_y")
    var mModelY: Double = 0.0,
    @field:JsonProperty("model_font")
    @get:JsonProperty("model_font")
    var mModelFont: Int = 0,
    @field:JsonProperty("model_column_size")
    @get:JsonProperty("model_column_size")
    var mModelColumnSize: Int = 0,
    @field:JsonProperty("color")
    @get:JsonProperty("color")
    var color: String? = null,
    @field:JsonProperty("color_label")
    @get:JsonProperty("color_label")
    var colorLabel: String? = null,
    @field:JsonProperty("color_column")
    @get:JsonProperty("color_column")
    var colorColumn: Int? = 1,
    @field:JsonProperty("colorCodeFullName")
    @get:JsonProperty("colorCodeFullName")
    var colorCodeFullName: String? = null,
    @field:JsonProperty("status_color")
    @get:JsonProperty("status_color")
    var isStatus_color: Boolean = false,
    @field:JsonProperty("print_order_color")
    @get:JsonProperty("print_order_color")
    var mPrintOrderColor: Double = 0.0,
    @field:JsonProperty("print_layout_order_color")
    @get:JsonProperty("print_layout_order_color")
    var mPrintLayoutOrderColor: String? = null,
    @field:JsonProperty("color_x")
    @get:JsonProperty("color_x")
    var mColorX: Double = 0.0,
    @field:JsonProperty("color_Y")
    @get:JsonProperty("color_Y")
    var mColorY: Double = 0.0,
    @field:JsonProperty("color_font")
    @get:JsonProperty("color_font")
    var mColorFont: Int = 0,
    @field:JsonProperty("color_column_size")
    @get:JsonProperty("color_column_size")
    var mColorColumnSize: Int = 0,
    @field:JsonProperty("expiration")
    @get:JsonProperty("expiration")
    var expiration: String? = null,
    @field:JsonProperty("expiration_label")
    @get:JsonProperty("expiration_label")
    var expirationLabel: String? = null,
    @field:JsonProperty("expiration_column")
    @get:JsonProperty("expiration_column")
    var expirationColumn: Int? = 1,
    @field:JsonProperty("status_expiration")
    @get:JsonProperty("status_expiration")
    var isStatus_expiration: Boolean = false,
    @field:JsonProperty("print_order_expiration")
    @get:JsonProperty("print_order_expiration")
    var mPrintOrderExpiration: Double = 0.0,
    @field:JsonProperty("print_layout_order_expiration")
    @get:JsonProperty("print_layout_order_expiration")
    var mPrintLayoutOrderExpiration: String? = null,
    @field:JsonProperty("expiration_x")
    @get:JsonProperty("expiration_x")
    var mExpirationX: Double = 0.0,
    @field:JsonProperty("expiration_y")
    @get:JsonProperty("expiration_y")
    var mExpirationY: Double = 0.0,
    @field:JsonProperty("expiration_font")
    @get:JsonProperty("expiration_font")
    var mExpirationFont: Int = 0,
    @field:JsonProperty("expiration_column_size")
    @get:JsonProperty("expiration_column_size")
    var mExpirationColumnSize: Int = 0,
    @field:JsonProperty("state")
    @get:JsonProperty("state")
    var state: String? = null,
    @field:JsonProperty("state_label")
    @get:JsonProperty("state_label")
    var stateLabel: String? = null,
    @field:JsonProperty("state_column")
    @get:JsonProperty("state_column")
    var stateColumn: Int? = 1,
    @field:JsonProperty("status_state")
    @get:JsonProperty("status_state")
    var isStatus_state: Boolean = false,
    @field:JsonProperty("print_order_state")
    @get:JsonProperty("print_order_state")
    var mPrintOrderState: Double = 0.0,
    @field:JsonProperty("print_layout_order_state")
    @get:JsonProperty("print_layout_order_state")
    var mPrintLayoutOrderState: String? = null,
    @field:JsonProperty("state_x")
    @get:JsonProperty("state_x")
    var mStateX: Double = 0.0,
    @field:JsonProperty("state_y")
    @get:JsonProperty("state_y")
    var mStateY: Double = 0.0,
    @field:JsonProperty("state_font")
    @get:JsonProperty("state_font")
    var mStateFont: Int = 0,
    @field:JsonProperty("state_column_size")
    @get:JsonProperty("state_column_size")
    var mStateColumnSize: Int = 0,
    @field:JsonProperty("body_style")
    @get:JsonProperty("body_style")
    var bodyStyle: String? = null,
    @field:JsonProperty("body_style_lookup_code")
    @get:JsonProperty("body_style_lookup_code")
    var body_style_lookup_code: String? = null,
    @field:JsonProperty("body_style_label")
    @get:JsonProperty("body_style_label")
    var bodyStyleLabel: String? = null,
    @field:JsonProperty("body_style_column")
    @get:JsonProperty("body_style_column")
    var bodyStyleColumn: Int? = 1,
    @field:JsonProperty("body_style_des")
    @get:JsonProperty("body_style_des")
    var bodyStyleDescription: String? = null,
    @field:JsonProperty("status_body_style")
    @get:JsonProperty("status_body_style")
    var isStatus_body_style: Boolean = false,
    @field:JsonProperty("print_order_body_style")
    @get:JsonProperty("print_order_body_style")
    var mPrintOrderBodyStyle: Double = 0.0,
    @field:JsonProperty("print_layout_order_body_style")
    @get:JsonProperty("print_layout_order_body_style")
    var mPrintLayoutOrderBodyStyle: String? = null,
    @field:JsonProperty("body_style_x")
    @get:JsonProperty("body_style_x")
    var mBodyStyleX: Double = 0.0,
    @field:JsonProperty("body_style_y")
    @get:JsonProperty("body_style_y")
    var mBodyStyleY: Double = 0.0,
    @field:JsonProperty("body_font")
    @get:JsonProperty("body_font")
    var mBodyFont: Int = 0,
    @field:JsonProperty("body_column_size")
    @get:JsonProperty("body_column_size")
    var mBodyColumnSize: Int = 0,
    @field:JsonProperty("decal_year")
    @get:JsonProperty("decal_year")
    var decalYear: String? = null,
    @field:JsonProperty("decal_year_label")
    @get:JsonProperty("decal_year_label")
    var decalYearLabel: String? = null,
    @field:JsonProperty("decal_year_column")
    @get:JsonProperty("decal_year_column")
    var decalYearColumn: Int? = 1,
    @field:JsonProperty("status_decal_year")
    @get:JsonProperty("status_decal_year")
    var isStatus_decal_year: Boolean = false,
    @field:JsonProperty("print_order_decal_year")
    @get:JsonProperty("print_order_decal_year")
    var mPrintOrderDecalYear: Double = 0.0,
    @field:JsonProperty("print_layout_order_decal_year")
    @get:JsonProperty("print_layout_order_decal_year")
    var mPrintLayoutOrderDecalYear: String? = null,
    @field:JsonProperty("decal_year_x")
    @get:JsonProperty("decal_year_x")
    var mDecalYearX: Double = 0.0,
    @field:JsonProperty("decal_year_y")
    @get:JsonProperty("decal_year_y")
    var mDecalYearY: Double = 0.0,
    @field:JsonProperty("decal_year_font")
    @get:JsonProperty("decal_year_font")
    var mDecalYearFont: Int = 0,
    @field:JsonProperty("decal_year_column_size")
    @get:JsonProperty("decal_year_column_size")
    var mDecalYearColumnSize: Int = 0,
    @field:JsonProperty("decal_number")
    @get:JsonProperty("decal_number")
    var decalNumber: String? = null,
    @field:JsonProperty("decal_number_column")
    @get:JsonProperty("decal_number_column")
    var decalNumberColumn: Int? = 1,
    @field:JsonProperty("decal_number_label")
    @get:JsonProperty("decal_number_label")
    var decalNumberLabel: String? = null,
    @field:JsonProperty("status_decal_number")
    @get:JsonProperty("status_decal_number")
    var isStatus_decal_number: Boolean = false,
    @field:JsonProperty("print_order_decal_number")
    @get:JsonProperty("print_order_decal_number")
    var mPrintOrderDecalNumber: Double = 0.0,
    @field:JsonProperty("print_layout_order_decal_number")
    @get:JsonProperty("print_layout_order_decal_number")
    var mPrintLayoutOrderDecalNumber: String? = null,
    @field:JsonProperty("decal_number_x")
    @get:JsonProperty("decal_number_x")
    var mDecalNumberX: Double = 0.0,
    @field:JsonProperty("decal_number_y")
    @get:JsonProperty("decal_number_y")
    var mDecalNumberY: Double = 0.0,
    @field:JsonProperty("decal_number_font")
    @get:JsonProperty("decal_number_font")
    var mDecalNumberFont: Int = 0,
    @field:JsonProperty("decal_number_column_size")
    @get:JsonProperty("decal_number_column_size")
    var mDecalNumberColumnSize: Int = 0,
    @field:JsonProperty("vin_number")
    @get:JsonProperty("vin_number")
    var vinNumber: String? = null,
    @field:JsonProperty("vin_number_label")
    @get:JsonProperty("vin_number_label")
    var vinNumberLabel: String? = null,
    @field:JsonProperty("vin_number_column")
    @get:JsonProperty("vin_number_column")
    var vinNumberColumn: Int? = 1,
    @field:JsonProperty("status_vin_number")
    @get:JsonProperty("status_vin_number")
    var isStatus_vin_number: Boolean = false,
    @field:JsonProperty("print_order_vin_number")
    @get:JsonProperty("print_order_vin_number")
    var mPrintOrderVinNumber: Double = 0.0,
    @field:JsonProperty("print_layout_order_vin_number")
    @get:JsonProperty("print_layout_order_vin_number")
    var mPrintLayoutOrderVinNumber: String? = null,
    @field:JsonProperty("vin_number_x")
    @get:JsonProperty("vin_number_x")
    var mVinNumberX: Double = 0.0,
    @field:JsonProperty("vin_number_y")
    @get:JsonProperty("vin_number_y")
    var mVinNumberY: Double = 0.0,
    @field:JsonProperty("vin_number_font")
    @get:JsonProperty("vin_number_font")
    var mVinNumberFont: Int = 0,
    @field:JsonProperty("vin_number_column_size")
    @get:JsonProperty("vin_number_column_size")
    var mVinNumberColumnSize: Int = 0,
    @field:JsonProperty("license_plate")
    @get:JsonProperty("license_plate")
    var licensePlate: String? = null,
    @field:JsonProperty("license_plate_column")
    @get:JsonProperty("license_plate_column")
    var licensePlateColumn: Int? = 1,
    @field:JsonProperty("license_plate_label")
    @get:JsonProperty("license_plate_label")
    var licensePlateLabel: String? = null,
    @field:JsonProperty("status_license_plate")
    @get:JsonProperty("status_license_plate")
    var isStatus_license_plate: Boolean = false,
    @field:JsonProperty("print_order_license_plate")
    @get:JsonProperty("print_order_license_plate")
    var mPrintOrderLicensePlate: Double = 0.0,
    @field:JsonProperty("print_layout_order_license_plate")
    @get:JsonProperty("print_layout_order_license_plate")
    var mPrintLayoutOrderLicensePlate: String? = null,
    @field:JsonProperty("license_plate_x")
    @get:JsonProperty("license_plate_x")
    var mLicensePlateX: Double = 0.0,
    @field:JsonProperty("license_plate_y")
    @get:JsonProperty("license_plate_y")
    var mLicensePlateY: Double = 0.0,
    @field:JsonProperty("license_font")
    @get:JsonProperty("license_font")
    var mLicenseFont: Int = 0,
    @field:JsonProperty("license_column_size")
    @get:JsonProperty("license_column_size")
    var mLicenseColumnSize: Int = 0,
    @field:JsonProperty("form_layout_order")
    @get:JsonProperty("form_layout_order")
    var mFormLayoutOrder: String? = null
) : Parcelable
