package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true, value = ["stability"])
data class CitationIssuranceModel(
    @field:JsonProperty("timing_id")
    @get:JsonProperty("timing_id")
    var timingId: String? = null,
    @field:JsonProperty("ticket_number")
    @get:JsonProperty("ticket_number")
    var ticketNumber: String? = null,
    @field:JsonProperty("ticket_number_label")
    @get:JsonProperty("ticket_number_label")
    var ticketNumberLabel: String? = null,
    @field:JsonProperty("ticket_number_column")
    @get:JsonProperty("ticket_number_column")
    var ticketNumberColumn: Int? = 1,
    @field:JsonProperty("status_ticket_number")
    @get:JsonProperty("status_ticket_number")
    var isStatus_ticketNumber: Boolean = false,
    @field:JsonProperty("print_order_ticket_number")
    @get:JsonProperty("print_order_ticket_number")
    var mPrintOrderTickerNumber: Double = 0.0,
    @field:JsonProperty("print_layout_order_ticket_number")
    @get:JsonProperty("print_layout_order_ticket_number")
    var mPrintLayoutOrderTickerNumber: String? = null,
    @field:JsonProperty("ticket_number_x")
    @get:JsonProperty("ticket_number_x")
    var mTicketNumberX: Double = 0.0,
    @field:JsonProperty("ticket_number_y")
    @get:JsonProperty("ticket_number_y")
    var mTicketNumberY: Double = 0.0,
    @field:JsonProperty("ticket_number_font_size")
    @get:JsonProperty("ticket_number_font_size")
    var mTicketNumberFontSize: Int = 0,
    @field:JsonProperty("ticket_number_column_size")
    @get:JsonProperty("ticket_number_column_size")
    var mTicketNumberColumnSize: Int = 0,
    @field:JsonProperty("ticket_date")
    @get:JsonProperty("ticket_date")
    var ticketDate: String? = null,
    @field:JsonProperty("ticket_date_label")
    @get:JsonProperty("ticket_date_label")
    var ticketDateLabel: String? = null,
    @field:JsonProperty("ticket_date_column")
    @get:JsonProperty("ticket_date_column")
    var ticketDateColumn: Int? = 1,
    @field:JsonProperty("ticket_date_print")
    @get:JsonProperty("ticket_date_print")
    var ticketDatePrint: String? = null,
    @field:JsonProperty("status_ticket_date")
    @get:JsonProperty("status_ticket_date")
    var isStatus_ticket_date: Boolean = false,
    @field:JsonProperty("print_order_ticket_date")
    @get:JsonProperty("print_order_ticket_date")
    var mPrintOrderTicketDate: Double = 0.0,
    @field:JsonProperty("print_layout_order_ticket_date")
    @get:JsonProperty("print_layout_order_ticket_date")
    var mPrintLayoutOrderTicketDate: String? = null,
    @field:JsonProperty("ticket_date_x")
    @get:JsonProperty("ticket_date_x")
    var mTicketDateX: Double = 0.0,
    @field:JsonProperty("ticket_date_y")
    @get:JsonProperty("ticket_date_y")
    var mTicketDateY: Double = 0.0,
    @field:JsonProperty("ticket_date_font")
    @get:JsonProperty("ticket_date_font")
    var mticketDateFont: Int = 0,
    @field:JsonProperty("ticket_date_column_size")
    @get:JsonProperty("ticket_date_column_size")
    var mTicketDateColumnSize: Int = 0,
    @field:JsonProperty("ticket_time")
    @get:JsonProperty("ticket_time")
    var ticketTime: String? = null,
    @field:JsonProperty("ticket_time_label")
    @get:JsonProperty("ticket_time_label")
    var ticketTimeLabel: String? = null,
    @field:JsonProperty("ticket_time_column")
    @get:JsonProperty("ticket_time_column")
    var ticketTimeColumn: Int? = 1,
    @field:JsonProperty("ticket_time_print")
    @get:JsonProperty("ticket_time_print")
    var ticketTimePrint: String? = null,
    @field:JsonProperty("status_ticket_time")
    @get:JsonProperty("status_ticket_time")
    var isStatus_ticket_time: Boolean = false,
    @field:JsonProperty("print_order_ticket_time")
    @get:JsonProperty("print_order_ticket_time")
    var mPrintOrderTicketTime: Double = 0.0,
    @field:JsonProperty("print_layout_order_ticket_time")
    @get:JsonProperty("print_layout_order_ticket_time")
    var mPrintLayoutOrderTicketTime: String? = null,
    @field:JsonProperty("ticket_time_x")
    @get:JsonProperty("ticket_time_x")
    var mTicketTimeX: Double = 0.0,
    @field:JsonProperty("ticket_time_y")
    @get:JsonProperty("ticket_time_y")
    var mticketTimeY: Double = 0.0,
    @field:JsonProperty("ticket_time_font")
    @get:JsonProperty("ticket_time_font")
    var mticketTimeFont: Int = 0,
    @field:JsonProperty("ticket_time_column_size")
    @get:JsonProperty("ticket_time_column_size")
    var mTicketTimeColumnSize: Int = 0,
    @field:JsonProperty("ticket_week")
    @get:JsonProperty("ticket_week")
    var ticketWeek: String? = null,
    @field:JsonProperty("ticket_week_label")
    @get:JsonProperty("ticket_week_label")
    var ticketWeekLabel: String? = null,
    @field:JsonProperty("ticket_week_column")
    @get:JsonProperty("ticket_week_column")
    var ticketWeekColumn: Int? = 1,
    @field:JsonProperty("ticket_week_print")
    @get:JsonProperty("ticket_week_print")
    var ticketWeekPrint: String? = null,
    @field:JsonProperty("status_ticket_week")
    @get:JsonProperty("status_ticket_week")
    var isStatus_ticket_week: Boolean = false,
    @field:JsonProperty("print_order_ticket_week")
    @get:JsonProperty("print_order_ticket_week")
    var mPrintOrderTicketWeek: Double = 0.0,
    @field:JsonProperty("print_layout_order_ticket_week")
    @get:JsonProperty("print_layout_order_ticket_week")
    var mPrintLayoutOrderTicketWeek: String? = null,
    @field:JsonProperty("ticket_week_x")
    @get:JsonProperty("ticket_week_x")
    var mTicketWeekX: Double = 0.0,
    @field:JsonProperty("ticket_week_y")
    @get:JsonProperty("ticket_week_y")
    var mTicketWeekY: Double = 0.0,
    @field:JsonProperty("ticket_week_font")
    @get:JsonProperty("ticket_week_font")
    var mTicketWeekFont: Int = 0,
    @field:JsonProperty("ticket_week_column_size")
    @get:JsonProperty("ticket_week_column_size")
    var mTicketWeekColumnSize: Int = 0,
    @field:JsonProperty("code2010")
    @get:JsonProperty("code2010")
    var code2010: String? = null,
    @field:JsonProperty("code2010_label")
    @get:JsonProperty("code2010_label")
    var code2010Label: String? = null,
    @field:JsonProperty("code2010_column")
    @get:JsonProperty("code2010_column")
    var code2010Column: Int? = 1,
    @field:JsonProperty("code2010_print")
    @get:JsonProperty("code2010_print")
    var code2010Print: String? = null,
    @field:JsonProperty("status_code2010")
    @get:JsonProperty("status_code2010")
    var isStatus_code2010: Boolean = false,
    @field:JsonProperty("print_order_code2010")
    @get:JsonProperty("print_order_code2010")
    var mPrintOrdercode2010: Double = 0.0,
    @field:JsonProperty("print_layout_order_code2010")
    @get:JsonProperty("print_layout_order_code2010")
    var mPrintLayoutOrdercode2010: String? = null,
    @field:JsonProperty("code2010_x")
    @get:JsonProperty("code2010_x")
    var mCode2010X: Double = 0.0,
    @field:JsonProperty("code2010_y")
    @get:JsonProperty("code2010_y")
    var mCode2010Y: Double = 0.0,
    @field:JsonProperty("code2010_font")
    @get:JsonProperty("code2010_font")
    var mCode2010Font: Int = 0,
    @field:JsonProperty("code_2010_column_size")
    @get:JsonProperty("code_2010_column_size")
    var mCode2010ColumnSize: Int = 0,
    @field:JsonProperty("hearing_date")
    @get:JsonProperty("hearing_date")
    var hearingDate: String? = null,
    @field:JsonProperty("hearingdate_label")
    @get:JsonProperty("hearingdate_label")
    var hearingDateLabel: String? = null,
    @field:JsonProperty("hearing_date_column")
    @get:JsonProperty("hearing_date_column")
    var hearingDateColumn: Int? = 1,
    @field:JsonProperty("hearing_date_print")
    @get:JsonProperty("hearing_date_print")
    var hearingDatePrint: String? = null,
    @field:JsonProperty("status_hearing_date")
    @get:JsonProperty("status_hearing_date")
    var isStatus_hearingDate: Boolean = false,
    @field:JsonProperty("print_order_hearing_date")
    @get:JsonProperty("print_order_hearing_date")
    var mPrintOrderHearingDate: Double = 0.0,
    @field:JsonProperty("print_layout_order_hearing_date")
    @get:JsonProperty("print_layout_order_hearing_date")
    var mPrintLayoutOrderHearingDate: String? = null,
    @field:JsonProperty("hearing_date_x")
    @get:JsonProperty("hearing_date_x")
    var mHearingDateX: Double = 0.0,
    @field:JsonProperty("hearing_date_y")
    @get:JsonProperty("hearing_date_y")
    var mHearingDateY: Double = 0.0,
    @field:JsonProperty("hearing_date_font")
    @get:JsonProperty("hearing_date_font")
    var mHearingDateFont: Int = 0,
    @field:JsonProperty("hearing_date_column_size")
    @get:JsonProperty("hearing_date_column_size")
    var mHearingDateColumnSize: Int = 0,
    @field:JsonProperty("hearing_description")
    @get:JsonProperty("hearing_description")
    var hearingDescription: String? = null,
    @field:JsonProperty("hearing_description_label")
    @get:JsonProperty("hearing_description_label")
    var hearingDescriptionLabel: String? = null,
    @field:JsonProperty("hearing_description_column")
    @get:JsonProperty("hearing_description_column")
    var hearingDescriptionColumn: Int? = 1,
    @field:JsonProperty("hearing_description_print")
    @get:JsonProperty("hearing_description_print")
    var hearingDescriptionPrint: String? = null,
    @field:JsonProperty("status_hearing_description")
    @get:JsonProperty("status_hearing_description")
    var isStatus_hearingDescription: Boolean = false,
    @field:JsonProperty("print_order_hearing_description")
    @get:JsonProperty("print_order_hearing_description")
    var mPrintOrderHearingDescription: Double = 0.0,
    @field:JsonProperty("print_layout_order_hearing_description")
    @get:JsonProperty("print_layout_order_hearing_description")
    var mPrintLayoutOrderHearingDescription: String? = null,
    @field:JsonProperty("hearing_description_x")
    @get:JsonProperty("hearing_description_x")
    var mHearingDescriptionX: Double = 0.0,
    @field:JsonProperty("hearing_description_y")
    @get:JsonProperty("hearing_description_y")
    var mHearingDescriptionY: Double = 0.0,
    @field:JsonProperty("hearing_description_font")
    @get:JsonProperty("hearing_description_font")
    var mHearingDescriptionFont: Int = 0,
    @field:JsonProperty("hearing_description_column_size")
    @get:JsonProperty("hearing_description_column_size")
    var mHearingDesriptionColumnSize: Int = 0,
    @field:JsonProperty("officer_description")
    @get:JsonProperty("officer_description")
    var officerDescription: String? = null,
    @field:JsonProperty("officer_description_label")
    @get:JsonProperty("officer_description_label")
    var officerDescriptionLabel: String? = null,
    @field:JsonProperty("officer_description_column")
    @get:JsonProperty("officer_description_column")
    var officerDescriptionColumn: Int? = 1,
    @field:JsonProperty("officer_description_print")
    @get:JsonProperty("officer_description_print")
    var officerDescriptionPrint: String? = null,
    @field:JsonProperty("status_officer_description")
    @get:JsonProperty("status_officer_description")
    var isStatus_OfficerDescription: Boolean = false,
    @field:JsonProperty("print_order_officer_description")
    @get:JsonProperty("print_order_officer_description")
    var mPrintOrderOfficerDescription: Double = 0.0,
    @field:JsonProperty("print_layout_order_officer_description")
    @get:JsonProperty("print_layout_order_officer_description")
    var mPrintLayoutOrderOfficerDescription: String? = null,
    @field:JsonProperty("officer_description_x")
    @get:JsonProperty("officer_description_x")
    var mOfficerDescriptionX: Double = 0.0,
    @field:JsonProperty("officer_description_y")
    @get:JsonProperty("officer_description_y")
    var mOfficerDescriptionY: Double = 0.0,
    @field:JsonProperty("officer_description_font")
    @get:JsonProperty("officer_description_font")
    var mOfficerDescriptionFont: Int = 0,
    @field:JsonProperty("officer_description_column_size")
    @get:JsonProperty("officer_description_column_size")
    var mOfficerDescriptionColumnSize: Int = 0,
    @field:JsonProperty("ticket_type")
    @get:JsonProperty("ticket_type")
    var ticketType: String? = null,
    @field:JsonProperty("ticket_type2")
    @get:JsonProperty("ticket_type2")
    var ticketType2: String? = null,
    @field:JsonProperty("ticket_type3")
    @get:JsonProperty("ticket_type3")
    var ticketType3: String? = null,
    @field:JsonProperty("status_ticket_type")
    @get:JsonProperty("status_ticket_type")
    var isStatus_ticket_type: Boolean = false,
    @field:JsonProperty("print_order_ticket_type")
    @get:JsonProperty("print_order_ticket_type")
    var mPrintOrderTicketType: Double = 0.0,
    @field:JsonProperty("print_layout_order_ticket_type")
    @get:JsonProperty("print_layout_order_ticket_type")
    var mPrintLayoutOrderTicketType: String? = null,
    @field:JsonProperty("ticket_type_x")
    @get:JsonProperty("ticket_type_x")
    var mTicketTypeX: Double = 0.0,
    @field:JsonProperty("ticket_type_y")
    @get:JsonProperty("ticket_type_y")
    var mTicketTypeY: Double = 0.0,
    @field:JsonProperty("ticket_type_font")
    @get:JsonProperty("ticket_type_font")
    var mTicketTypeFont: Int = 0,
    @field:JsonProperty("ticket_type_column_size")
    @get:JsonProperty("ticket_type_column_size")
    var mTicketTypeColumnSize: Int = 0,
    @field:JsonProperty("vehicle")
    @get:JsonProperty("vehicle")
    var vehicle: CitationVehicleModel? = null,
    @field:JsonProperty("officer")
    @get:JsonProperty("officer")
    var officer: CitationOfficerModel? = null,
    @field:JsonProperty("location")
    @get:JsonProperty("location")
    var location: CitationLocationModel? = null,
    @field:JsonProperty("voilation")
    @get:JsonProperty("voilation")
    var voilation: CitationVoilationModel? = null,
    @field:JsonProperty("municipal_citation_motorist_details_model")
    @get:JsonProperty("municipal_citation_motorist_details_model")
    var municipalCitationMotoristDetailsModel: MunicipalCitationMotoristDetailsModel? = null,
    @field:JsonProperty("location_remarks")
    @get:JsonProperty("location_remarks")
    var locationRemarks: String? = null,
    @field:JsonProperty("location_remarks_observed")
    @get:JsonProperty("location_remarks_observed")
    var locationRemarksObserved: String? = null,
    @field:JsonProperty("location_remarks_label")
    @get:JsonProperty("location_remarks_label")
    var locationRemarksLabel: String? = null,
    @field:JsonProperty("location_remarks_column")
    @get:JsonProperty("location_remarks_column")
    var locationRemarksColumn: Int? = 1,
    @field:JsonProperty("status_location_remarks")
    @get:JsonProperty("status_location_remarks")
    var isStatus_location_remarks: Boolean = false,
    @field:JsonProperty("print_order_location_remarks")
    @get:JsonProperty("print_order_location_remarks")
    var mPrintOrderLocationRemarks: Double = 0.0,
    @field:JsonProperty("print_layout_order_location_remarks")
    @get:JsonProperty("print_layout_order_location_remarks")
    var mPrintLayoutOrderLocationRemarks: String? = null,
    @field:JsonProperty("remark_x")
    @get:JsonProperty("remark_x")
    var mRemarkX: Double = 0.0,
    @field:JsonProperty("remark_y")
    @get:JsonProperty("remark_y")
    var mRemarkY: Double = 0.0,
    @field:JsonProperty("remark_font")
    @get:JsonProperty("remark_font")
    var mRemarkFont: Int = 0,
    @field:JsonProperty("remark_column_size")
    @get:JsonProperty("remark_column_size")
    var mRemarkColumnSize: Int = 0,
    @field:JsonProperty("location_remarks1")
    @get:JsonProperty("location_remarks1")
    var locationRemarks1: String? = null,
    @field:JsonProperty("location_remarks1_observed")
    @get:JsonProperty("location_remarks1_observed")
    var locationRemarks1Observed: String? = null,
    @field:JsonProperty("location_remarks1_label")
    @get:JsonProperty("location_remarks1_label")
    var locationRemarks1Label: String? = null,
    @field:JsonProperty("location_remarks1_column")
    @get:JsonProperty("location_remarks1_column")
    var locationRemarks1Column: Int? = 1,
    @field:JsonProperty("status_location_remarks1")
    @get:JsonProperty("status_location_remarks1")
    var isStatus_location_remarks1: Boolean = false,
    @field:JsonProperty("print_order_location_remarks1")
    @get:JsonProperty("print_order_location_remarks1")
    var mPrintOrderLocationRemarks1: Double = 0.0,
    @field:JsonProperty("print_layout_order_location_remarks1")
    @get:JsonProperty("print_layout_order_location_remarks1")
    var mPrintLayoutOrderLocationRemarks1: String? = null,
    @field:JsonProperty("remark1_x")
    @get:JsonProperty("remark1_x")
    var mRemark1X: Double = 0.0,
    @field:JsonProperty("remark1_y")
    @get:JsonProperty("remark1_y")
    var mRemark1Y: Double = 0.0,
    @field:JsonProperty("remark1_font")
    @get:JsonProperty("remark1_font")
    var mRemark1Font: Int = 0,
    @field:JsonProperty("remark1_column_size")
    @get:JsonProperty("remark1_column_size")
    var mRemark1ColumnSize: Int = 0,
    @field:JsonProperty("location_remarks2")
    @get:JsonProperty("location_remarks2")
    var locationRemarks2: String? = null,
    @field:JsonProperty("location_remarks1_label2")
    @get:JsonProperty("location_remarks1_label2")
    var locationRemarks2Label: String? = null,
    @field:JsonProperty("location_remarks2_column")
    @get:JsonProperty("location_remarks2_column")
    var locationRemarks2Column: Int? = 1,
    @field:JsonProperty("status_location_remarks2")
    @get:JsonProperty("status_location_remarks2")
    var isStatus_location_remarks2: Boolean = false,
    @field:JsonProperty("print_order_location_remarks2")
    @get:JsonProperty("print_order_location_remarks2")
    var mPrintOrderLocationRemarks2: Double = 0.0,
    @field:JsonProperty("print_layout_order_location_remarks2")
    @get:JsonProperty("print_layout_order_location_remarks2")
    var mPrintLayoutOrderLocationRemarks2: String? = null,
    @field:JsonProperty("remark2_x")
    @get:JsonProperty("remark2_x")
    var mRemark2X: Double = 0.0,
    @field:JsonProperty("remark2_y")
    @get:JsonProperty("remark2_y")
    var mRemark2Y: Double = 0.0,
    @field:JsonProperty("remark2_font")
    @get:JsonProperty("remark2_font")
    var mRemark2Font: Int = 0,
    @field:JsonProperty("remark2_column_size")
    @get:JsonProperty("remark2_column_size")
    var mRemark2ColumnSize: Int = 0,
    @field:JsonProperty("location_notes")
    @get:JsonProperty("location_notes")
    var locationNotes: String? = null,
    @field:JsonProperty("location_notes_label")
    @get:JsonProperty("location_notes_label")
    var locationNotesLabel: String? = null,
    @field:JsonProperty("location_notes_column")
    @get:JsonProperty("location_notes_column")
    var locationNotesColumn: Int? = 1,
    @field:JsonProperty("status_location_notes")
    @get:JsonProperty("status_location_notes")
    var isStatus_location_notes: Boolean = false,
    @field:JsonProperty("print_order_location_notes")
    @get:JsonProperty("print_order_location_notes")
    var mPrintOrderLocationNotes: Double = 0.0,
    @field:JsonProperty("print_layout_order_location_notes")
    @get:JsonProperty("print_layout_order_location_notes")
    var mPrintLayoutOrderLocationNotes: String? = null,
    @field:JsonProperty("location_notes1")
    @get:JsonProperty("location_notes1")
    var locationNotes1: String? = null,
    @field:JsonProperty("location_notes1_label")
    @get:JsonProperty("location_notes1_label")
    var locationNotes1Label: String? = null,
    @field:JsonProperty("location_notes1_column")
    @get:JsonProperty("location_notes1_column")
    var locationNotes1Column: Int? = 1,
    @field:JsonProperty("status_location_notes1")
    @get:JsonProperty("status_location_notes1")
    var isStatus_location_notes1: Boolean = false,
    @field:JsonProperty("print_order_location_notes1")
    @get:JsonProperty("print_order_location_notes1")
    var mPrintOrderLocationNotes1: Double = 0.0,
    @field:JsonProperty("print_layout_order_location_notes1")
    @get:JsonProperty("print_layout_order_location_notes1")
    var mPrintLayoutOrderLocationNotes1: String? = null,
    @field:JsonProperty("location_notes2")
    @get:JsonProperty("location_notes2")
    var locationNotes2: String? = null,
    @field:JsonProperty("location_notes2_label")
    @get:JsonProperty("location_notes2_label")
    var locationNotes2Label: String? = null,
    @field:JsonProperty("location_notes2_column")
    @get:JsonProperty("location_notes2_column")
    var locationNotes2Column: Int? = 1,
    @field:JsonProperty("status_location_notes2")
    @get:JsonProperty("status_location_notes2")
    var isStatus_location_notes2: Boolean = false,
    @field:JsonProperty("print_order_location_notes2")
    @get:JsonProperty("print_order_location_notes2")
    var mPrintOrderLocationNotes2: Double = 0.0,
    @field:JsonProperty("print_layout_order_location_notes2")
    @get:JsonProperty("print_layout_order_location_notes2")
    var mPrintLayoutOrderLocationNotes2: String? = null,
    @field:JsonProperty("location_bottomtext")
    @get:JsonProperty("location_bottomtext")
    var locationBottomText: String? = null,
    @field:JsonProperty("location_bottomtext_label")
    @get:JsonProperty("location_bottomtext_label")
    var locationBottomTextLabel: String? = null,
    @field:JsonProperty("location_bottomtext_column")
    @get:JsonProperty("location_bottomtext_column")
    var locationBottomTextColumn: Int? = 1,
    @field:JsonProperty("status_location_bottomtext")
    @get:JsonProperty("status_location_bottomtext")
    var isStatus_location_bottomtext: Boolean = false,
    @field:JsonProperty("print_order_location_bottomtext")
    @get:JsonProperty("print_order_location_bottomtext")
    var mPrintOrderLocationBottomText: Double = 0.0,
    @field:JsonProperty("print_layout_order_location_bottomtext")
    @get:JsonProperty("print_layout_order_location_bottomtext")
    var mPrintLayoutOrderLocationBottomText: String? = null,
    @field:JsonProperty("bottom_text_x")
    @get:JsonProperty("bottom_text_x")
    var mBottomTextX: Double = 0.0,
    @field:JsonProperty("bottom_text_y")
    @get:JsonProperty("bottom_text_y")
    var mBottomTextY: Double = 0.0,
    @field:JsonProperty("bottom_text_font")
    @get:JsonProperty("bottom_text_font")
    var mBottomTextFont: Int = 0,
    @field:JsonProperty("start_time")
    @get:JsonProperty("start_time")
    var startTime: String? = null,
    @field:JsonProperty("start_time_label")
    @get:JsonProperty("start_time_label")
    var startTimeLabel: String? = null,
    @field:JsonProperty("start_time_column")
    @get:JsonProperty("start_time_column")
    var startTimeColumn: Int? = 1,
    @field:JsonProperty("status_start_time")
    @get:JsonProperty("status_start_time")
    var isStatus_start_time: Boolean = false,
    @field:JsonProperty("print_order_start_time")
    @get:JsonProperty("print_order_start_time")
    var mPrintOrderStartTime: Double = 0.0,
    @field:JsonProperty("print_layout_order_start_time")
    @get:JsonProperty("print_layout_order_start_time")
    var mPrintLayoutOrderStartTime: String? = null,
    @field:JsonProperty("issue_time")
    @get:JsonProperty("issue_time")
    var issueTime: String? = null,
    @field:JsonProperty("issue_time_label")
    @get:JsonProperty("issue_time_label")
    var issueTimeLabel: String? = null,
    @field:JsonProperty("issue_time_column")
    @get:JsonProperty("issue_time_column")
    var issueTimeColumn: Int? = 1,
    @field:JsonProperty("status_issue_time")
    @get:JsonProperty("status_issue_time")
    var isStatus_issue_time: Boolean = false,
    @field:JsonProperty("print_order_issue_time")
    @get:JsonProperty("print_order_issue_time")
    var mPrintOrderIssueTime: Double = 0.0,
    @field:JsonProperty("print_layout_order_issue_time")
    @get:JsonProperty("print_layout_order_issue_time")
    var mPrintLayoutOrderIssueTime: String? = null,
    @field:JsonProperty("images_list")
    @get:JsonProperty("images_list")
    var imagesList: @kotlinx.parcelize.RawValue List<CitationImagesModel>? = null,
    @field:JsonProperty("print_layout_order")
    @get:JsonProperty("print_layout_order")
    var mPrintLayoutOrder: String? = null
) : Parcelable
