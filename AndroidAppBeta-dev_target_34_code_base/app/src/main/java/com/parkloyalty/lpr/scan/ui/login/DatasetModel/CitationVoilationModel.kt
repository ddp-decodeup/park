package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationVoilationModel(
    @field:JsonProperty("violation")
    @get:JsonProperty("violation")
    var violationCode: String? = null,

    @field:JsonProperty("violation_label")
    @get:JsonProperty("violation_label")
    var violationCodeLabel: String? = null,

    @field:JsonProperty("violation_column")
    @get:JsonProperty("violation_column")
    var violationColumn: Int? = 1,

    @field:JsonProperty("violation_x")
    @get:JsonProperty("violation_x")
    var mViolationX: Double = 0.0,

    @field:JsonProperty("violation_y")
    @get:JsonProperty("violation_y")
    var mViolationY: Double = 0.0,

    @field:JsonProperty("violation_font")
    @get:JsonProperty("violation_font")
    var mViolationFont: Int = 0,

    @field:JsonProperty("violation_column_size")
    @get:JsonProperty("violation_column_size")
    var mViolationColumnSize: Int = 0,

    @field:JsonProperty("code")
    @get:JsonProperty("code")
    var code: String? = null,

    @field:JsonProperty("code_label")
    @get:JsonProperty("code_label")
    var codeLabel: String? = null,

    @field:JsonProperty("code_column")
    @get:JsonProperty("code_column")
    var codeColumn: Int? = 1,

    @field:JsonProperty("status_code")
    @get:JsonProperty("status_code")
    var isStatus_code: Boolean = false,

    @field:JsonProperty("print_order_code")
    @get:JsonProperty("print_order_code")
    var mPrintOrderCode: Double = 0.0,

    @field:JsonProperty("print_layout_order_code")
    @get:JsonProperty("print_layout_order_code")
    var mPrintLayoutOrderCode: String? = null,

    @field:JsonProperty("code_x")
    @get:JsonProperty("code_x")
    var mCodeX: Double = 0.0,

    @field:JsonProperty("code_y")
    @get:JsonProperty("code_y")
    var mCodeY: Double = 0.0,

    @field:JsonProperty("code_font")
    @get:JsonProperty("code_font")
    var mCodeFont: Int = 0,

    @field:JsonProperty("code_column_size")
    @get:JsonProperty("code_column_size")
    var mCodeColumnSize: Int = 0,

    @field:JsonProperty("amount")
    @get:JsonProperty("amount")
    var amount: String? = null,

    @field:JsonProperty("amount_column")
    @get:JsonProperty("amount_column")
    var amountColumn: Int? = 1,

    @field:JsonProperty("amount_day")
    @get:JsonProperty("amount_day")
    var amountDay: Int? = 1,

    @field:JsonProperty("amount_label")
    @get:JsonProperty("amount_label")
    var amountLabel: String? = null,

    @field:JsonProperty("status_amount")
    @get:JsonProperty("status_amount")
    var isStatus_amount: Boolean = false,

    @field:JsonProperty("print_order_amount")
    @get:JsonProperty("print_order_amount")
    var mPrintOrderAmount: Double = 0.0,

    @field:JsonProperty("unpaidCitationCount")
    @get:JsonProperty("unpaidCitationCount")
    var mUnpaidCitationCount: Int? = 0,

    @field:JsonProperty("print_layout_order_amount")
    @get:JsonProperty("print_layout_order_amount")
    var mPrintLayoutOrderAmount: String? = null,

    @field:JsonProperty("amount_x")
    @get:JsonProperty("amount_x")
    var mAmountX: Double = 0.0,

    @field:JsonProperty("amount_y")
    @get:JsonProperty("amount_y")
    var mAmountY: Double = 0.0,

    @field:JsonProperty("amount_font")
    @get:JsonProperty("amount_font")
    var mAmountFont: Int = 0,

    @field:JsonProperty("amount_column_size")
    @get:JsonProperty("amount_column_size")
    var mAmountColumnSize: Int = 0,

    @field:JsonProperty("location_descr")
    @get:JsonProperty("location_descr")
    var locationDescr: String? = null,

    @field:JsonProperty("location_descr_label")
    @get:JsonProperty("location_descr_label")
    var locationDescrLabel: String? = null,

    @field:JsonProperty("location_descr_column")
    @get:JsonProperty("location_descr_column")
    var locationDescrColumn: Int? = 1,

    @field:JsonProperty("status_location_descr")
    @get:JsonProperty("status_location_descr")
    var isStatus_location_descr: Boolean = false,

    @field:JsonProperty("print_order_location_descr")
    @get:JsonProperty("print_order_location_descr")
    var mPrintOrderLocationDescr: Double = 0.0,

    @field:JsonProperty("print_layout_order_location_descr")
    @get:JsonProperty("print_layout_order_location_descr")
    var mPrintLayoutOrderLocationDescr: String? = null,

    @field:JsonProperty("descr_x")
    @get:JsonProperty("descr_x")
    var mLocationDescrX: Double = 0.0,

    @field:JsonProperty("descr_y")
    @get:JsonProperty("descr_y")
    var mLocationDescrY: Double = 0.0,

    @field:JsonProperty("descr_font")
    @get:JsonProperty("descr_font")
    var mDescrFont: Int = 0,

    @field:JsonProperty("descr_column_size")
    @get:JsonProperty("descr_column_size")
    var mDescrColumnSize: Int = 0,

    @field:JsonProperty("due_date")
    @get:JsonProperty("due_date")
    var dueDate: String? = null,

    @field:JsonProperty("due_date_label")
    @get:JsonProperty("due_date_label")
    var dueDateLabel: String? = null,

    @field:JsonProperty("due_date_column")
    @get:JsonProperty("due_date_column")
    var dueDateColumn: Int? = 1,

    @field:JsonProperty("due_date_day")
    @get:JsonProperty("due_date_day")
    var dueDateDay: Int? = 1,

    @field:JsonProperty("status_due_date")
    @get:JsonProperty("status_due_date")
    var isStatus_due_date: Boolean = false,

    @field:JsonProperty("print_order_due_date")
    @get:JsonProperty("print_order_due_date")
    var mPrintOrderDueDate: Double = 0.0,

    @field:JsonProperty("print_layout_order_due_date")
    @get:JsonProperty("print_layout_order_due_date")
    var mPrintLayoutOrderDueDate: String? = null,

    @field:JsonProperty("due_date_x")
    @get:JsonProperty("due_date_x")
    var mDueDateX: Double = 0.0,

    @field:JsonProperty("due_date_y")
    @get:JsonProperty("due_date_y")
    var mDueDateY: Double = 0.0,

    @field:JsonProperty("due_date_font")
    @get:JsonProperty("due_date_font")
    var mDueDateFont: Int = 0,

    @field:JsonProperty("due_date_column_size")
    @get:JsonProperty("due_date_column_size")
    var mDueDateColumnSize: Int = 0,

    @field:JsonProperty("due_date_30")
    @get:JsonProperty("due_date_30")
    var dueDate30: String? = null,

    @field:JsonProperty("due_date_30_label")
    @get:JsonProperty("due_date_30_label")
    var dueDate30Label: String? = null,

    @field:JsonProperty("due_date_30_days")
    @get:JsonProperty("due_date_30_days")
    var dueDate30Days: Int = 0,

    @field:JsonProperty("due_date_30_column")
    @get:JsonProperty("due_date_30_column")
    var dueDate30Column: Int? = 1,

    @field:JsonProperty("status_due_date_30")
    @get:JsonProperty("status_due_date_30")
    var isStatus_due_date_30: Boolean = false,

    @field:JsonProperty("print_order_due_date_30")
    @get:JsonProperty("print_order_due_date_30")
    var mPrintOrderDueDate30: Double = 0.0,

    @field:JsonProperty("print_layout_order_due_date_30")
    @get:JsonProperty("print_layout_order_due_date_30")
    var mPrintLayoutOrderDueDate30: String? = null,

    @field:JsonProperty("due_date_30_x")
    @get:JsonProperty("due_date_30_x")
    var mDueDate30X: Double = 0.0,

    @field:JsonProperty("due_date_30_y")
    @get:JsonProperty("due_date_30_y")
    var mDueDate30Y: Double = 0.0,

    @field:JsonProperty("due_date_30font")
    @get:JsonProperty("due_date_30font")
    var mDueDate30Font: Int = 0,

    @field:JsonProperty("due_date_30_column_size")
    @get:JsonProperty("due_date_30_column_size")
    var mDueDate30ColumnSize: Int = 0,

    @field:JsonProperty("due_date_45")
    @get:JsonProperty("due_date_45")
    var dueDate45: String? = null,

    @field:JsonProperty("due_date_45_label")
    @get:JsonProperty("due_date_45_label")
    var dueDate45Label: String? = null,

    @field:JsonProperty("due_date_45_days")
    @get:JsonProperty("due_date_45_days")
    var dueDate45Days: Int = 0,

    @field:JsonProperty("due_date_45_column")
    @get:JsonProperty("due_date_45_column")
    var dueDate45Column: Int? = 1,

    @field:JsonProperty("status_due_date_45")
    @get:JsonProperty("status_due_date_45")
    var isStatus_due_date_45: Boolean = false,

    @field:JsonProperty("print_order_due_date_45")
    @get:JsonProperty("print_order_due_date_45")
    var mPrintOrderDueDate45: Double = 0.0,

    @field:JsonProperty("print_layout_order_due_date_45")
    @get:JsonProperty("print_layout_order_due_date_45")
    var mPrintLayoutOrderDueDate45: String? = null,

    @field:JsonProperty("due_date_45_x")
    @get:JsonProperty("due_date_45_x")
    var mDueDate45X: Double = 0.0,

    @field:JsonProperty("due_date_45_y")
    @get:JsonProperty("due_date_45_y")
    var mDueDate45Y: Double = 0.0,

    @field:JsonProperty("due_date_45font")
    @get:JsonProperty("due_date_45font")
    var mDueDate45Font: Int = 0,

    @field:JsonProperty("due_date_45_column_size")
    @get:JsonProperty("due_date_45_column_size")
    var mDueDate45ColumnSize: Int = 0,

    @field:JsonProperty("due_date_cost")
    @get:JsonProperty("due_date_cost")
    var dueDateCost: String? = null,

    @field:JsonProperty("due_date_cost_label")
    @get:JsonProperty("due_date_cost_label")
    var dueDateCostLabel: String? = null,

    @field:JsonProperty("due_date_cost_column")
    @get:JsonProperty("due_date_cost_column")
    var dueDateCostColumn: Int? = 1,

    @field:JsonProperty("status_due_date_cost")
    @get:JsonProperty("status_due_date_cost")
    var isStatus_due_date_cost: Boolean = false,

    @field:JsonProperty("print_order_due_date_cost")
    @get:JsonProperty("print_order_due_date_cost")
    var mPrintOrderDueDateCost: Double = 0.0,

    @field:JsonProperty("print_layout_order_due_date_cost")
    @get:JsonProperty("print_layout_order_due_date_cost")
    var mPrintLayoutOrderDueDateCost: String? = null,

    @field:JsonProperty("due_date_cost_x")
    @get:JsonProperty("due_date_cost_x")
    var mDueDateCostX: Double = 0.0,

    @field:JsonProperty("due_date_cost_y")
    @get:JsonProperty("due_date_cost_y")
    var mDueDateCostY: Double = 0.0,

    @field:JsonProperty("due_date_cost_font")
    @get:JsonProperty("due_date_cost_font")
    var mDueDateCostFont: Int = 0,

    @field:JsonProperty("due_date_cost_column_size")
    @get:JsonProperty("due_date_cost_column_size")
    var mDueDateCostColumnSize: Int = 0,

    @field:JsonProperty("due_date_parking_fee")
    @get:JsonProperty("due_date_parking_fee")
    var dueDateParkingFee: String? = null,

    @field:JsonProperty("due_date_parking_fee_label")
    @get:JsonProperty("due_date_parking_fee_label")
    var dueDateParkingFeeLabel: String? = null,

    @field:JsonProperty("due_date_parking_fee_column")
    @get:JsonProperty("due_date_parking_fee_column")
    var dueDateParkingFeeColumn: Int? = 1,

    @field:JsonProperty("status_due_date_parking_fee")
    @get:JsonProperty("status_due_date_parking_fee")
    var isStatus_due_date_parking_fee: Boolean = false,

    @field:JsonProperty("print_order_due_date_parking_fee")
    @get:JsonProperty("print_order_due_date_parking_fee")
    var mPrintOrderDueDateParkingFee: Double = 0.0,

    @field:JsonProperty("print_layout_order_due_date_parking_fee")
    @get:JsonProperty("print_layout_order_due_date_parking_fee")
    var mPrintLayoutOrderDueDateParkingFee: String? = null,

    @field:JsonProperty("due_date_parking_fee_x")
    @get:JsonProperty("due_date_parking_fee_x")
    var mDueDateParkingFeeX: Double = 0.0,

    @field:JsonProperty("due_date_parking_fee_y")
    @get:JsonProperty("due_date_parking_fee_y")
    var mDueDateParkingFeeY: Double = 0.0,

    @field:JsonProperty("due_date_parking_fee_font")
    @get:JsonProperty("due_date_parking_fee_font")
    var mDueDateParkingFeeFont: Int = 0,

    @field:JsonProperty("due_date_parking_fee_column_size")
    @get:JsonProperty("due_date_parking_fee_column_size")
    var mDueDateParkingFeeColumnSize: Int = 0,

    @field:JsonProperty("due_date_citation_fee")
    @get:JsonProperty("due_date_citation_fee")
    var dueDateCitationFee: String? = null,

    @field:JsonProperty("due_date_citation_fee_label")
    @get:JsonProperty("due_date_citation_fee_label")
    var dueDateCitationFeeLabel: String? = null,

    @field:JsonProperty("due_date_citation_fee_column")
    @get:JsonProperty("due_date_citation_fee_column")
    var dueDateCitationFeeColumn: Int? = 1,

    @field:JsonProperty("status_due_date_citation_fee")
    @get:JsonProperty("status_due_date_citation_fee")
    var isStatus_due_date_citation_fee: Boolean = false,

    @field:JsonProperty("print_order_due_date_citation_fee")
    @get:JsonProperty("print_order_due_date_citation_fee")
    var mPrintOrderDueDateCitationFee: Double = 0.0,

    @field:JsonProperty("print_layout_order_due_date_citation_fee")
    @get:JsonProperty("print_layout_order_due_date_citation_fee")
    var mPrintLayoutOrderDueDateCitationFee: String? = null,

    @field:JsonProperty("due_date_citation_fee_x")
    @get:JsonProperty("due_date_citation_fee_x")
    var mDueDateCitationFeeX: Double = 0.0,

    @field:JsonProperty("due_date_citation_fee_y")
    @get:JsonProperty("due_date_citation_fee_y")
    var mDueDateCitationFeeY: Double = 0.0,

    @field:JsonProperty("due_date_citation_fee_font")
    @get:JsonProperty("due_date_citation_fee_font")
    var mDueDateCitationFeeFont: Int = 0,

    @field:JsonProperty("due_date_citation_fee_column_size")
    @get:JsonProperty("due_date_citation_fee_column_size")
    var mDueDateCitationFeeColumnSize: Int = 0,

    @field:JsonProperty("due_date_total")
    @get:JsonProperty("due_date_total")
    var dueDateTotal: String? = null,

    @field:JsonProperty("due_date_total_label")
    @get:JsonProperty("due_date_total_label")
    var dueDateTotalLabel: String? = null,

    @field:JsonProperty("due_date_total_column")
    @get:JsonProperty("due_date_total_column")
    var dueDateTotalColumn: Int? = 1,

    @field:JsonProperty("status_due_date_total")
    @get:JsonProperty("status_due_date_total")
    var isStatus_due_date_total: Boolean = false,

    @field:JsonProperty("print_order_due_date_total")
    @get:JsonProperty("print_order_due_date_total")
    var mPrintOrderDueDateTotal: Double = 0.0,

    @field:JsonProperty("print_layout_order_due_date_total")
    @get:JsonProperty("print_layout_order_due_date_total")
    var mPrintLayoutOrderDueDateTotal: String? = null,

    @field:JsonProperty("due_date_total_x")
    @get:JsonProperty("due_date_total_x")
    var mDueDateTotalX: Double = 0.0,

    @field:JsonProperty("due_date_total_y")
    @get:JsonProperty("due_date_total_y")
    var mDueDateTotalY: Double = 0.0,

    @field:JsonProperty("due_date_total_font")
    @get:JsonProperty("due_date_total_font")
    var mDueDateTotalFont: Int = 0,

    @field:JsonProperty("due_date_total_column_size")
    @get:JsonProperty("due_date_total_column_size")
    var mDueDateTotalColumnSize: Int = 0,

    @field:JsonProperty("pay_at_online")
    @get:JsonProperty("pay_at_online")
    var payAtOnline: String? = null,

    @field:JsonProperty("pay_at_online_label")
    @get:JsonProperty("pay_at_online_label")
    var payAtOnlineLabel: String? = null,

    @field:JsonProperty("pay_at_online_column")
    @get:JsonProperty("pay_at_online_column")
    var payAtOnlineColumn: Int? = 1,

    @field:JsonProperty("status_pay_at_online")
    @get:JsonProperty("status_pay_at_online")
    var isStatus_pay_at_online: Boolean = false,

    @field:JsonProperty("print_order_pay_at_online")
    @get:JsonProperty("print_order_pay_at_online")
    var mPrintOrderPayAtOnline: Double = 0.0,

    @field:JsonProperty("print_layout_order_pay_at_online")
    @get:JsonProperty("print_layout_order_pay_at_online")
    var mPrintLayoutOrderPayAtOnline: String? = null,

    @field:JsonProperty("pay_online_x")
    @get:JsonProperty("pay_online_x")
    var mPayOnlineX: Double = 0.0,

    @field:JsonProperty("pay_online_y")
    @get:JsonProperty("pay_online_y")
    var mPayOnlineY: Double = 0.0,

    @field:JsonProperty("pay_online_font")
    @get:JsonProperty("pay_online_font")
    var mPayOnlineFont: Int = 0,

    @field:JsonProperty("pay_online_column_size")
    @get:JsonProperty("pay_online_column_size")
    var mPayOnlineColumnSize: Int = 0,

    @field:JsonProperty("amount_due_date")
    @get:JsonProperty("amount_due_date")
    var amountDueDate: String? = null,

    @field:JsonProperty("amount_due_date_label")
    @get:JsonProperty("amount_due_date_label")
    var amountDueDateLabel: String? = null,

    @field:JsonProperty("amount_due_date_column")
    @get:JsonProperty("amount_due_date_column")
    var amountDueDateColumn: Int? = 1,

    @field:JsonProperty("status_amount_due_date")
    @get:JsonProperty("status_amount_due_date")
    var isStatus_amount_due_date: Boolean = false,

    @field:JsonProperty("print_order_amount_due_date")
    @get:JsonProperty("print_order_amount_due_date")
    var mPrintOrderAmountDueDate: Double = 0.0,

    @field:JsonProperty("print_layout_order_amount_due_date")
    @get:JsonProperty("print_layout_order_amount_due_date")
    var mPrintLayoutOrderAmountDueDate: String? = null,

    @field:JsonProperty("amount_due_date_x")
    @get:JsonProperty("amount_due_date_x")
    var mAmountDueDateX: Double = 0.0,

    @field:JsonProperty("amount_due_date_y")
    @get:JsonProperty("amount_due_date_y")
    var mAmountDueDateY: Double = 0.0,

    @field:JsonProperty("amount_due_date_font")
    @get:JsonProperty("amount_due_date_font")
    var mAmountDueDateFont: Int = 0,

    @field:JsonProperty("amount_due_date_column_size")
    @get:JsonProperty("amount_due_date_column_size")
    var mAmountDueDateColumnSize: Int = 0,

    @field:JsonProperty("form_layout_order")
    @get:JsonProperty("form_layout_order")
    var mFormLayoutOrder: String? = null,

    @field:JsonProperty("late_fine_days")
    @get:JsonProperty("late_fine_days")
    var mLateFineDays: Int = 0,

    @field:JsonProperty("late_fine_days_label")
    @get:JsonProperty("late_fine_days_label")
    var mLateFineDaysLabel: Int = 0,

    @field:JsonProperty("late_fine_column")
    @get:JsonProperty("late_fine_column")
    var lateFineColumn: Int? = 1,

    @field:JsonProperty("late_fine_x")
    @get:JsonProperty("late_fine_x")
    var mLateFineX: Double = 0.0,

    @field:JsonProperty("late_fine_y")
    @get:JsonProperty("late_fine_y")
    var mLateFineY: Double = 0.0,

    @field:JsonProperty("late_fine_font")
    @get:JsonProperty("late_fine_font")
    var mLateFineFont: Int = 0,

    @field:JsonProperty("late_fine_column_size")
    @get:JsonProperty("late_fine_column_size")
    var mLateFineColumnSize: Int = 0,

    @field:JsonProperty("export_code")
    @get:JsonProperty("export_code")
    var export_code: String? = null,

    @field:JsonProperty("time_limit_vio")
    @get:JsonProperty("time_limit_vio")
    var timeLimitVio: String? = null,

    @field:JsonProperty("sanctions_type")
    @get:JsonProperty("sanctions_type")
    var mSanctionsType: Int? = 0,

//  Add new 3 parameter in violation section
    @field:JsonProperty("vio_type_code")
    @get:JsonProperty("vio_type_code")
    var vioTypeCode: String? = null,

    @field:JsonProperty("vio_type_code_label")
    @get:JsonProperty("vio_type_code_label")
    var vioTypeCodeLabel: String? = null,

    @field:JsonProperty("vio_type_code_column")
    @get:JsonProperty("vio_type_code_column")
    var vioTypeCodeColumn: Int? = 1,

    @field:JsonProperty("status_vio_type_code")
    @get:JsonProperty("status_vio_type_code")
    var isStatus_VioTypeCode: Boolean = false,

    @field:JsonProperty("print_order_vio_type_code")
    @get:JsonProperty("print_order_vio_type_code")
    var mPrintOrderVioTypeCode: Double = 0.0,

    @field:JsonProperty("print_layout_order_vio_type_code")
    @get:JsonProperty("print_layout_order_vio_type_code")
    var mPrintLayoutOrderVioTypeCode: String? = null,

    @field:JsonProperty("vio_type_code_x")
    @get:JsonProperty("vio_type_code_x")
    var mVioTypeCodeX: Double = 0.0,

    @field:JsonProperty("vio_type_code_y")
    @get:JsonProperty("vio_type_code_y")
    var mVioTypeCodeY: Double = 0.0,

    @field:JsonProperty("vio_type_code_font")
    @get:JsonProperty("vio_type_code_font")
    var mVioTypeCodeFont: Int = 0,

    @field:JsonProperty("vio_type_code_column_size")
    @get:JsonProperty("vio_type_code_column_size")
    var mVioTypeCodeColumnSize: Int = 0,


    @field:JsonProperty("vio_type_description")
    @get:JsonProperty("vio_type_description")
    var vioTypeDescription: String? = null,

    @field:JsonProperty("vio_type_description_label")
    @get:JsonProperty("vio_type_description_label")
    var vioTypeDescriptionLabel: String? = null,

    @field:JsonProperty("vio_type_description_column")
    @get:JsonProperty("vio_type_description_column")
    var vioTypeDescriptionColumn: Int? = 1,

    @field:JsonProperty("status_vio_type_description")
    @get:JsonProperty("status_vio_type_description")
    var isStatus_VioTypeDescription: Boolean = false,

    @field:JsonProperty("print_order_vio_type_description")
    @get:JsonProperty("print_order_vio_type_description")
    var mPrintOrderVioTypeDescription: Double = 0.0,

    @field:JsonProperty("print_layout_order_vio_type_description")
    @get:JsonProperty("print_layout_order_vio_type_description")
    var mPrintLayoutOrderVioTypeDescription: String? = null,

    @field:JsonProperty("vio_type_description_x")
    @get:JsonProperty("vio_type_description_x")
    var mVioTypeDescriptionX: Double = 0.0,

    @field:JsonProperty("vio_type_description_y")
    @get:JsonProperty("vio_type_description_y")
    var mVioTypeDescriptionY: Double = 0.0,

    @field:JsonProperty("vio_type_description_font")
    @get:JsonProperty("vio_type_description_font")
    var mVioTypeDescriptionFont: Int = 0,

    @field:JsonProperty("vio_type_description_column_size")
    @get:JsonProperty("vio_type_description_column_size")
    var mVioTypeDescriptionColumnSize: Int = 0,

    @field:JsonProperty("vio_type")
    @get:JsonProperty("vio_type")
    var vioType: String? = null,

    @field:JsonProperty("vio_type__label")
    @get:JsonProperty("vio_type_label")
    var vioTypeLabel: String? = null,

    @field:JsonProperty("vio_type_column")
    @get:JsonProperty("vio_type_column")
    var vioTypeColumn: Int? = 1,

    @field:JsonProperty("status_vio_type")
    @get:JsonProperty("status_vio_type")
    var isStatus_VioType: Boolean = false,

    @field:JsonProperty("print_order_vio_type")
    @get:JsonProperty("print_order_vio_type")
    var mPrintOrderVioType: Double = 0.0,

    @field:JsonProperty("print_layout_order_vio_type")
    @get:JsonProperty("print_layout_order_vio_type")
    var mPrintLayoutOrderVioType: String? = null,

    @field:JsonProperty("vio_type_x")
    @get:JsonProperty("vio_type_x")
    var mVioTypeX: Double = 0.0,

    @field:JsonProperty("vio_type_y")
    @get:JsonProperty("vio_type_y")
    var mVioTypeY: Double = 0.0,

    @field:JsonProperty("vio_type_font")
    @get:JsonProperty("vio_type_font")
    var mVioTypeFont: Int = 0,

    @field:JsonProperty("vio_type_column_size")
    @get:JsonProperty("vio_type_column_size")
    var mVioTypeColumnSize: Int = 0
) : Parcelable
