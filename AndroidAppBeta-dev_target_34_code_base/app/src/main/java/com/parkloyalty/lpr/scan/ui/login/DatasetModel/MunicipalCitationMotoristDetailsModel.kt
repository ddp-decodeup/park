package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class MunicipalCitationMotoristDetailsModel(
    @field:JsonProperty("first_name")
    @get:JsonProperty("first_name")
    var motoristFirstName: String? = null,
    @field:JsonProperty("is_status_motorist_first_name")
    @get:JsonProperty("is_status_motorist_first_name")
    var isStatusMotoristFirstName: Boolean? = null,
    @field:JsonProperty("print_order_motorist_first_name")
    @get:JsonProperty("print_order_motorist_first_name")
    var mPrintOrderMotoristFirstName: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_first_name")
    @get:JsonProperty("print_layout_order_motorist_first_name")
    var mPrintLayoutOrderMotoristFirstName: String? = null,
    @field:JsonProperty("motorist_first_name_label")
    @get:JsonProperty("motorist_first_name_label")
    var motoristFirstNameLabel: String? = null,
    @field:JsonProperty("motorist_first_name_column")
    @get:JsonProperty("motorist_first_name_column")
    var motoristFirstNameColumn: Int? = 1,
    @field:JsonProperty("motorist_first_name_font")
    @get:JsonProperty("motorist_first_name_font")
    var motoristFirstNameFont: Int = 0,
    @field:JsonProperty("motorist_first_name_x")
    @get:JsonProperty("motorist_first_name_x")
    var motoristFirstNameX: Double = 0.0,
    @field:JsonProperty("motorist_first_name_y")
    @get:JsonProperty("motorist_first_name_y")
    var motoristFirstNameY: Double = 0.0,
    // Middle Name
    @field:JsonProperty("middle_name")
    @get:JsonProperty("middle_name")
    var motoristMiddleName: String? = null,
    @field:JsonProperty("is_status_motorist_middle_name")
    @get:JsonProperty("is_status_motorist_middle_name")
    var isStatusMotoristMiddleName: Boolean? = null,
    @field:JsonProperty("print_order_motorist_middle_name")
    @get:JsonProperty("print_order_motorist_middle_name")
    var mPrintOrderMotoristMiddleName: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_middle_name")
    @get:JsonProperty("print_layout_order_motorist_middle_name")
    var mPrintLayoutOrderMotoristMiddleName: String? = null,
    @field:JsonProperty("motorist_middle_name_label")
    @get:JsonProperty("motorist_middle_name_label")
    var motoristMiddleNameLabel: String? = null,
    @field:JsonProperty("motorist_middle_name_column")
    @get:JsonProperty("motorist_middle_name_column")
    var motoristMiddleNameColumn: Int? = 1,
    @field:JsonProperty("motorist_middle_name_font")
    @get:JsonProperty("motorist_middle_name_font")
    var motoristMiddleNameFont: Int = 0,
    @field:JsonProperty("motorist_middle_name_x")
    @get:JsonProperty("motorist_middle_name_x")
    var motoristMiddleNameX: Double = 0.0,
    @field:JsonProperty("motorist_middle_name_y")
    @get:JsonProperty("motorist_middle_name_y")
    var motoristMiddleNameY: Double = 0.0,
    // Last Name
    @field:JsonProperty("last_name")
    @get:JsonProperty("last_name")
    var motoristLastName: String? = null,
    @field:JsonProperty("is_status_motorist_last_name")
    @get:JsonProperty("is_status_motorist_last_name")
    var isStatusMotoristLastName: Boolean? = null,
    @field:JsonProperty("print_order_motorist_last_name")
    @get:JsonProperty("print_order_motorist_last_name")
    var mPrintOrderMotoristLastName: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_last_name")
    @get:JsonProperty("print_layout_order_motorist_last_name")
    var mPrintLayoutOrderMotoristLastName: String? = null,
    @field:JsonProperty("motorist_last_name_label")
    @get:JsonProperty("motorist_last_name_label")
    var motoristLastNameLabel: String? = null,
    @field:JsonProperty("motorist_last_name_column")
    @get:JsonProperty("motorist_last_name_column")
    var motoristLastNameColumn: Int? = 1,
    @field:JsonProperty("motorist_last_name_font")
    @get:JsonProperty("motorist_last_name_font")
    var motoristLastNameFont: Int = 0,
    @field:JsonProperty("motorist_last_name_x")
    @get:JsonProperty("motorist_last_name_x")
    var motoristLastNameX: Double = 0.0,
    @field:JsonProperty("motorist_last_name_y")
    @get:JsonProperty("motorist_last_name_y")
    var motoristLastNameY: Double = 0.0,
    // DOB
    @field:JsonProperty("date_of_birth")
    @get:JsonProperty("date_of_birth")
    var motoristDateOfBirth: String? = null,
    @field:JsonProperty("is_status_motorist_date_of_birth")
    @get:JsonProperty("is_status_motorist_date_of_birth")
    var isStatusMotoristDateOfBirth: Boolean? = null,
    @field:JsonProperty("print_order_motorist_date_of_birth")
    @get:JsonProperty("print_order_motorist_date_of_birth")
    var mPrintOrderMotoristDateOfBirth: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_date_of_birth")
    @get:JsonProperty("print_layout_order_motorist_date_of_birth")
    var mPrintLayoutOrderMotoristDateOfBirth: String? = null,
    @field:JsonProperty("motorist_date_of_birth_label")
    @get:JsonProperty("motorist_date_of_birth_label")
    var motoristDateOfBirthLabel: String? = null,
    @field:JsonProperty("motorist_date_of_birth_column")
    @get:JsonProperty("motorist_date_of_birth_column")
    var motoristDateOfBirthColumn: Int? = 1,
    @field:JsonProperty("motorist_date_of_birth_font")
    @get:JsonProperty("motorist_date_of_birth_font")
    var motoristDateOfBirthFont: Int = 0,
    @field:JsonProperty("motorist_date_of_birth_x")
    @get:JsonProperty("motorist_date_of_birth_x")
    var motoristDateOfBirthX: Double = 0.0,
    @field:JsonProperty("motorist_date_of_birth_y")
    @get:JsonProperty("motorist_date_of_birth_y")
    var motoristDateOfBirthY: Double = 0.0,
    // DL Number
    @field:JsonProperty("dl_number")
    @get:JsonProperty("dl_number")
    var motoristDlNumber: String? = null,
    @field:JsonProperty("is_status_motorist_dl_number")
    @get:JsonProperty("is_status_motorist_dl_number")
    var isStatusMotoristDlNumber: Boolean? = null,
    @field:JsonProperty("print_order_motorist_dl_number")
    @get:JsonProperty("print_order_motorist_dl_number")
    var mPrintOrderMotoristDlNumber: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_dl_number")
    @get:JsonProperty("print_layout_order_motorist_dl_number")
    var mPrintLayoutOrderMotoristDlNumber: String? = null,
    @field:JsonProperty("motorist_dl_number_label")
    @get:JsonProperty("motorist_dl_number_label")
    var motoristDlNumberLabel: String? = null,
    @field:JsonProperty("motorist_dl_number_column")
    @get:JsonProperty("motorist_dl_number_column")
    var motoristDlNumberColumn: Int? = 1,
    @field:JsonProperty("motorist_dl_number_font")
    @get:JsonProperty("motorist_dl_number_font")
    var motoristDlNumberFont: Int = 0,
    @field:JsonProperty("motorist_dl_number_x")
    @get:JsonProperty("motorist_dl_number_x")
    var motoristDlNumberX: Double = 0.0,
    @field:JsonProperty("motorist_dl_number_y")
    @get:JsonProperty("motorist_dl_number_y")
    var motoristDlNumberY: Double = 0.0,
    // Block
    @field:JsonProperty("motorist_address_block")
    @get:JsonProperty("motorist_address_block")
    var motoristAddressBlock: String? = null,
    @field:JsonProperty("is_status_motorist_address_block")
    @get:JsonProperty("is_status_motorist_address_block")
    var isStatusMotoristAddressBlock: Boolean? = null,
    @field:JsonProperty("print_order_motorist_address_block")
    @get:JsonProperty("print_order_motorist_address_block")
    var mPrintOrderMotoristAddressBlock: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_address_block")
    @get:JsonProperty("print_layout_order_motorist_address_block")
    var mPrintLayoutOrderMotoristAddressBlock: String? = null,
    @field:JsonProperty("motorist_address_block_label")
    @get:JsonProperty("motorist_address_block_label")
    var motoristAddressBlockLabel: String? = null,
    @field:JsonProperty("motorist_address_block_column")
    @get:JsonProperty("motorist_address_block_column")
    var motoristAddressBlockColumn: Int? = 1,
    @field:JsonProperty("motorist_address_block_font")
    @get:JsonProperty("motorist_address_block_font")
    var motoristAddressBlockFont: Int = 0,
    @field:JsonProperty("motorist_address_block_x")
    @get:JsonProperty("motorist_address_block_x")
    var motoristAddressBlockX: Double = 0.0,
    @field:JsonProperty("motorist_address_block_y")
    @get:JsonProperty("motorist_address_block_y")
    var motoristAddressBlockY: Double = 0.0,
    // Street
    @field:JsonProperty("motorist_address_street")
    @get:JsonProperty("motorist_address_street")
    var motoristAddressStreet: String? = null,
    @field:JsonProperty("is_status_motorist_address_street")
    @get:JsonProperty("is_status_motorist_address_street")
    var isStatusMotoristAddressStreet: Boolean? = null,
    @field:JsonProperty("print_order_motorist_address_street")
    @get:JsonProperty("print_order_motorist_address_street")
    var mPrintOrderMotoristAddressStreet: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_address_street")
    @get:JsonProperty("print_layout_order_motorist_address_street")
    var mPrintLayoutOrderMotoristAddressStreet: String? = null,
    @field:JsonProperty("motorist_address_street_label")
    @get:JsonProperty("motorist_address_street_label")
    var motoristAddressStreetLabel: String? = null,
    @field:JsonProperty("motorist_address_street_column")
    @get:JsonProperty("motorist_address_street_column")
    var motoristAddressStreetColumn: Int? = 1,
    @field:JsonProperty("motorist_address_street_font")
    @get:JsonProperty("motorist_address_street_font")
    var motoristAddressStreetFont: Int = 0,
    @field:JsonProperty("motorist_address_street_x")
    @get:JsonProperty("motorist_address_street_x")
    var motoristAddressStreetX: Double = 0.0,
    @field:JsonProperty("motorist_address_street_y")
    @get:JsonProperty("motorist_address_street_y")
    var motoristAddressStreetY: Double = 0.0,
    // City
    @field:JsonProperty("motorist_address_city")
    @get:JsonProperty("motorist_address_city")
    var motoristAddressCity: String? = null,
    @field:JsonProperty("is_status_motorist_address_city")
    @get:JsonProperty("is_status_motorist_address_city")
    var isStatusMotoristAddressCity: Boolean? = null,
    @field:JsonProperty("print_order_motorist_address_city")
    @get:JsonProperty("print_order_motorist_address_city")
    var mPrintOrderMotoristAddressCity: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_address_city")
    @get:JsonProperty("print_layout_order_motorist_address_city")
    var mPrintLayoutOrderMotoristAddressCity: String? = null,
    @field:JsonProperty("motorist_address_city_label")
    @get:JsonProperty("motorist_address_city_label")
    var motoristAddressCityLabel: String? = null,
    @field:JsonProperty("motorist_address_city_column")
    @get:JsonProperty("motorist_address_city_column")
    var motoristAddressCityColumn: Int? = 1,
    @field:JsonProperty("motorist_address_city_font")
    @get:JsonProperty("motorist_address_city_font")
    var motoristAddressCityFont: Int = 0,
    @field:JsonProperty("motorist_address_city_x")
    @get:JsonProperty("motorist_address_city_x")
    var motoristAddressCityX: Double = 0.0,
    @field:JsonProperty("motorist_address_city_y")
    @get:JsonProperty("motorist_address_city_y")
    var motoristAddressCityY: Double = 0.0,
    // State
    @field:JsonProperty("motorist_address_state")
    @get:JsonProperty("motorist_address_state")
    var motoristAddressState: String? = null,
    @field:JsonProperty("is_status_motorist_address_state")
    @get:JsonProperty("is_status_motorist_address_state")
    var isStatusMotoristAddressState: Boolean? = null,
    @field:JsonProperty("print_order_motorist_address_state")
    @get:JsonProperty("print_order_motorist_address_state")
    var mPrintOrderMotoristAddressState: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_address_state")
    @get:JsonProperty("print_layout_order_motorist_address_state")
    var mPrintLayoutOrderMotoristAddressState: String? = null,
    @field:JsonProperty("motorist_address_state_label")
    @get:JsonProperty("motorist_address_state_label")
    var motoristAddressStateLabel: String? = null,
    @field:JsonProperty("motorist_address_state_column")
    @get:JsonProperty("motorist_address_state_column")
    var motoristAddressStateColumn: Int? = 1,
    @field:JsonProperty("motorist_address_state_font")
    @get:JsonProperty("motorist_address_state_font")
    var motoristAddressStateFont: Int = 0,
    @field:JsonProperty("motorist_address_state_x")
    @get:JsonProperty("motorist_address_state_x")
    var motoristAddressStateX: Double = 0.0,
    @field:JsonProperty("motorist_address_state_y")
    @get:JsonProperty("motorist_address_state_y")
    var motoristAddressStateY: Double = 0.0,
    // Zip
    @field:JsonProperty("motorist_address_zip")
    @get:JsonProperty("motorist_address_zip")
    var motoristAddressZip: String? = null,
    @field:JsonProperty("is_status_motorist_address_zip")
    @get:JsonProperty("is_status_motorist_address_zip")
    var isStatusMotoristAddressZip: Boolean? = null,
    @field:JsonProperty("print_order_motorist_address_zip")
    @get:JsonProperty("print_order_motorist_address_zip")
    var mPrintOrderMotoristAddressZip: Double = 0.0,
    @field:JsonProperty("print_layout_order_motorist_address_zip")
    @get:JsonProperty("print_layout_order_motorist_address_zip")
    var mPrintLayoutOrderMotoristAddressZip: String? = null,
    @field:JsonProperty("motorist_address_zip_label")
    @get:JsonProperty("motorist_address_zip_label")
    var motoristAddressZipLabel: String? = null,
    @field:JsonProperty("motorist_address_zip_column")
    @get:JsonProperty("motorist_address_zip_column")
    var motoristAddressZipColumn: Int? = 1,
    @field:JsonProperty("motorist_address_zip_font")
    @get:JsonProperty("motorist_address_zip_font")
    var motoristAddressZipFont: Int = 0,
    @field:JsonProperty("motorist_address_zip_x")
    @get:JsonProperty("motorist_address_zip_x")
    var motoristAddressZipX: Double = 0.0,
    @field:JsonProperty("motorist_address_zip_y")
    @get:JsonProperty("motorist_address_zip_y")
    var motoristAddressZipY: Double = 0.0
) : Parcelable
