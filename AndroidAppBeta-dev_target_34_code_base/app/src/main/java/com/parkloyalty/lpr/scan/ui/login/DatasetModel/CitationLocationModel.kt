package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import androidx.annotation.Keep
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationLocationModel(
    @field:JsonProperty("block")
    @get:JsonProperty("block")
    var block: String? = null,
    @field:JsonProperty("block_label")
    @get:JsonProperty("block_label")
    var blockLabel: String? = null,
    @field:JsonProperty("block_column")
    @get:JsonProperty("block_column")
    var blockColumn: Int? = 1,
    @field:JsonProperty("status_block")
    @get:JsonProperty("status_block")
    var isStatus_block: Boolean = false,
    @field:JsonProperty("print_order_block")
    @get:JsonProperty("print_order_block")
    var mPrintOrderblock: Double = 0.0,
    @field:JsonProperty("print_layout_order_block")
    @get:JsonProperty("print_layout_order_block")
    var mPrintLayoutOrderblock: String? = null,
    @field:JsonProperty("block_x")
    @get:JsonProperty("block_x")
    var mBlockX: Double = 0.0,
    @field:JsonProperty("block_y")
    @get:JsonProperty("block_y")
    var mBlockY: Double = 0.0,
    @field:JsonProperty("block_font")
    @get:JsonProperty("block_font")
    var mBlockFont: Int = 0,
    @field:JsonProperty("block_column_size")
    @get:JsonProperty("block_column_size")
    var mBlockColumnSize: Int = 0,
    @field:JsonProperty("street")
    @get:JsonProperty("street")
    var street: String? = null,
    @field:JsonProperty("street_lookup_code")
    @get:JsonProperty("street_lookup_code")
    var mStreetLookupCode: String? = null,
    @field:JsonProperty("street_label")
    @get:JsonProperty("street_label")
    var streetLabel: String? = null,
    @field:JsonProperty("street_column")
    @get:JsonProperty("street_column")
    var streetColumn: Int? = 1,
    @field:JsonProperty("status_street")
    @get:JsonProperty("status_street")
    var isStatus_street: Boolean = false,
    @field:JsonProperty("print_order_street")
    @get:JsonProperty("print_order_street")
    var mPrintOrderStreet: Double = 0.0,
    @field:JsonProperty("print_layout_order_street")
    @get:JsonProperty("print_layout_order_street")
    var mPrintLayoutOrderStreet: String? = null,
    @field:JsonProperty("street_x")
    @get:JsonProperty("street_x")
    var mStreetX: Double = 0.0,
    @field:JsonProperty("street_y")
    @get:JsonProperty("street_y")
    var mStreetY: Double = 0.0,
    @field:JsonProperty("street_font")
    @get:JsonProperty("street_font")
    var mStreetFont: Int = 0,
    @field:JsonProperty("street_column_size")
    @get:JsonProperty("street_column_size")
    var mStreetColumnSize: Int = 0,
    @field:JsonProperty("direction")
    @get:JsonProperty("direction")
    var direction: String? = null,
    @field:JsonProperty("direction_label")
    @get:JsonProperty("direction_label")
    var directionLabel: String? = null,
    @field:JsonProperty("direction_column")
    @get:JsonProperty("direction_column")
    var directionColumn: Int? = 1,
    @field:JsonProperty("status_direction")
    @get:JsonProperty("status_direction")
    var isStatus_direction: Boolean = false,
    @field:JsonProperty("print_order_direction")
    @get:JsonProperty("print_order_direction")
    var mPrintOrderDirection: Double = 0.0,
    @field:JsonProperty("print_layout_order_direction")
    @get:JsonProperty("print_layout_order_direction")
    var mPrintLayoutOrderDirection: String? = null,
    @field:JsonProperty("direction_x")
    @get:JsonProperty("direction_x")
    var mDirectionX: Double = 0.0,
    @field:JsonProperty("direction_y")
    @get:JsonProperty("direction_y")
    var mDirectionY: Double = 0.0,
    @field:JsonProperty("direction_font")
    @get:JsonProperty("direction_font")
    var mDirectionFont: Int = 0,
    @field:JsonProperty("direction_column_size")
    @get:JsonProperty("direction_column_size")
    var mDirectionColumnSize: Int = 0,
    @field:JsonProperty("side")
    @get:JsonProperty("side")
    var side: String? = null,
    @field:JsonProperty("side_label")
    @get:JsonProperty("side_label")
    var sideLabel: String? = null,
    @field:JsonProperty("side_column")
    @get:JsonProperty("side_column")
    var sideColumn: Int? = 1,
    @field:JsonProperty("status_side")
    @get:JsonProperty("status_side")
    var isStatus_side: Boolean = false,
    @field:JsonProperty("print_order_side")
    @get:JsonProperty("print_order_side")
    var mPrintOrderSide: Double = 0.0,
    @field:JsonProperty("print_layout_order_side")
    @get:JsonProperty("print_layout_order_side")
    var mPrintLayoutOrderSide: String? = null,
    @field:JsonProperty("side_x")
    @get:JsonProperty("side_x")
    var mSideX: Double = 0.0,
    @field:JsonProperty("side_y")
    @get:JsonProperty("side_y")
    var mSideY: Double = 0.0,
    @field:JsonProperty("side_font")
    @get:JsonProperty("side_font")
    var mSideFont: Int = 0,
    @field:JsonProperty("side_column_size")
    @get:JsonProperty("side_column_size")
    var mSideColumnSize: Int = 0,
    @field:JsonProperty("meter_name")
    @get:JsonProperty("meter_name")
    var meterName: String? = null,
    @field:JsonProperty("meter_name_label")
    @get:JsonProperty("meter_name_label")
    var meterNameLabel: String? = null,
    @field:JsonProperty("meter_column")
    @get:JsonProperty("meter_column")
    var meterColumn: Int? = 1,
    @field:JsonProperty("status_meter_name")
    @get:JsonProperty("status_meter_name")
    var isStatus_meter_name: Boolean = false,
    @field:JsonProperty("print_order_meter_name")
    @get:JsonProperty("print_order_meter_name")
    var mPrintOrderMeterName: Double = 0.0,
    @field:JsonProperty("print_layout_order_meter_name")
    @get:JsonProperty("print_layout_order_meter_name")
    var mPrintLayoutOrderMeterName: String? = null,
    @field:JsonProperty("meter_x")
    @get:JsonProperty("meter_x")
    var mMeterX: Double = 0.0,
    @field:JsonProperty("meter_y")
    @get:JsonProperty("meter_y")
    var mMeterY: Double = 0.0,
    @field:JsonProperty("meter_font")
    @get:JsonProperty("meter_font")
    var mMeterFont: Int = 0,
    @field:JsonProperty("meter_column_size")
    @get:JsonProperty("meter_column_size")
    var mMeterColumnSize: Int = 0,
    @field:JsonProperty("space_name")
    @get:JsonProperty("space_name")
    var spaceName: String? = null,
    @field:JsonProperty("space_name_label")
    @get:JsonProperty("space_name_label")
    var spaceNameLabel: String? = null,
    @field:JsonProperty("space_column")
    @get:JsonProperty("space_column")
    var spaceColumn: Int? = 1,
    @field:JsonProperty("status_space_name")
    @get:JsonProperty("status_space_name")
    var isStatus_space_name: Boolean = false,
    @field:JsonProperty("print_order_space_name")
    @get:JsonProperty("print_order_space_name")
    var mPrintOrderSpaceName: Double = 0.0,
    @field:JsonProperty("print_layout_order_space_name")
    @get:JsonProperty("print_layout_order_space_name")
    var mPrintLayoutOrderSpaceName: String? = null,
    @field:JsonProperty("space_x")
    @get:JsonProperty("space_x")
    var mSpaceX: Double = 0.0,
    @field:JsonProperty("space_y")
    @get:JsonProperty("space_y")
    var mSpaceY: Double = 0.0,
    @field:JsonProperty("space_font")
    @get:JsonProperty("space_font")
    var mSpaceFont: Int = 0,
    @field:JsonProperty("space_column_size")
    @get:JsonProperty("space_column_size")
    var mSpaceColumnSize: Int = 0,
    @field:JsonProperty("lot_name")
    @get:JsonProperty("lot_name")
    var lotName: String? = null,
    @field:JsonProperty("lot_label")
    @get:JsonProperty("lot_label")
    var lotLabel: String? = null,
    @field:JsonProperty("lot_column")
    @get:JsonProperty("lot_column")
    var lotColumn: Int? = 1,
    @field:JsonProperty("lot")
    @get:JsonProperty("lot")
    var lot: String? = null,
    @field:JsonProperty("branch_lotid")
    @get:JsonProperty("branch_lotid")
    var lotBranchId: String? = null,
    @field:JsonProperty("lot_lookup_code")
    @get:JsonProperty("lot_lookup_code")
    var lotLookupCode: String? = null,
    @field:JsonProperty("status_lot")
    @get:JsonProperty("status_lot")
    var isStatus_lot: Boolean = false,
    @field:JsonProperty("print_order_lot")
    @get:JsonProperty("print_order_lot")
    var mPrintOrderLot: Double = 0.0,
    @field:JsonProperty("print_layout_order_lot")
    @get:JsonProperty("print_layout_order_lot")
    var mPrintLayoutOrderLot: String? = null,
    @field:JsonProperty("lot_x")
    @get:JsonProperty("lot_x")
    var mLotX: Double = 0.0,
    @field:JsonProperty("lot_y")
    @get:JsonProperty("lot_y")
    var mLotY: Double = 0.0,
    @field:JsonProperty("lot_font")
    @get:JsonProperty("lot_font")
    var mLotFont: Int = 0,
    @field:JsonProperty("lot_column_size")
    @get:JsonProperty("lot_column_size")
    var mLotColumnSize: Int = 0,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: String? = null,
    @field:JsonProperty("location_label")
    @get:JsonProperty("location_label")
    var locationLabel: String? = null,
    @field:JsonProperty("location_column")
    @get:JsonProperty("location_column")
    var locationColumn: Int? = 1,
    @field:JsonProperty("status_location")
    @get:JsonProperty("status_location")
    var isStatus_location: Boolean = false,
    @field:JsonProperty("print_order_location")
    @get:JsonProperty("print_order_location")
    var mPrintOrderLocation: Double = 0.0,
    @field:JsonProperty("print_layout_order_location")
    @get:JsonProperty("print_layout_order_location")
    var mPrintLayoutOrderLocation: String? = null,
    @field:JsonProperty("location_x")
    @get:JsonProperty("location_x")
    var mLocationX: Double = 0.0,
    @field:JsonProperty("location_y")
    @get:JsonProperty("location_y")
    var mLocationY: Double = 0.0,
    @field:JsonProperty("location_font")
    @get:JsonProperty("location_font")
    var mLocationFont: Int = 0,
    @field:JsonProperty("location_column_size")
    @get:JsonProperty("location_column_size")
    var mLocationColumnSize: Int = 0,
    @field:JsonProperty("city_zone")
    @get:JsonProperty("city_zone")
    var cityZone: String? = null,
    @field:JsonProperty("city_zone_label")
    @get:JsonProperty("city_zone_label")
    var cityZoneLabel: String? = null,
    @field:JsonProperty("city_zone_column")
    @get:JsonProperty("city_zone_column")
    var cityZoneColumn: Int? = 1,
    @field:JsonProperty("status_city_zone")
    @get:JsonProperty("status_city_zone")
    var isStatus_CityZone: Boolean = false,
    @field:JsonProperty("print_order_city_zone")
    @get:JsonProperty("print_order_city_zone")
    var mPrintOrderCityZone: Double = 0.0,
    @field:JsonProperty("print_layout_order_city_zone")
    @get:JsonProperty("print_layout_order_city_zone")
    var mPrintLayoutOrderCityZone: String? = null,
    @field:JsonProperty("city_zone_x")
    @get:JsonProperty("city_zone_x")
    var mCityZoneX: Double = 0.0,
    @field:JsonProperty("city_zone_y")
    @get:JsonProperty("city_zone_y")
    var mCityZoneY: Double = 0.0,
    @field:JsonProperty("city_zone_font")
    @get:JsonProperty("city_zone_font")
    var mCityZoneFont: Int = 0,
    @field:JsonProperty("city_zone_column_size")
    @get:JsonProperty("city_zone_column_size")
    var mCityZoneColumnSize: Int = 0,
    @field:JsonProperty("pcb_zone")
    @get:JsonProperty("pcb_zone")
    var pcbZone: String? = null,
    @field:JsonProperty("pcb_zone_label")
    @get:JsonProperty("pcb_zone_label")
    var pcbZoneLabel: String? = null,
    @field:JsonProperty("pcb_zone_column")
    @get:JsonProperty("pcb_zone_column")
    var pcbZoneColumn: Int? = 1,
    @field:JsonProperty("status_pcb_zone")
    @get:JsonProperty("status_pcb_zone")
    var isStatus_PcbZone: Boolean = false,
    @field:JsonProperty("print_order_pcb_zone")
    @get:JsonProperty("print_order_pcb_zone")
    var mPrintOrderPcbZone: Double = 0.0,
    @field:JsonProperty("print_layout_order_pcb_zone")
    @get:JsonProperty("print_layout_order_pcb_zone")
    var mPrintLayoutOrderPcbZone: String? = null,
    @field:JsonProperty("pcb_zone_x")
    @get:JsonProperty("pcb_zone_x")
    var mPcbZoneX: Double = 0.0,
    @field:JsonProperty("pcb_zone_y")
    @get:JsonProperty("pcb_zone_y")
    var mPcbZoneY: Double = 0.0,
    @field:JsonProperty("pcb_zone_font")
    @get:JsonProperty("pcb_zone_font")
    var mPcbZoneFont: Int = 0,
    @field:JsonProperty("pcb_zone_column_size")
    @get:JsonProperty("pcb_zone_column_size")
    var mPcbZoneColumnSize: Int = 0,
    @field:JsonProperty("form_layout_order")
    @get:JsonProperty("form_layout_order")
    var mFormLayoutOrder: String? = null
) : Parcelable
