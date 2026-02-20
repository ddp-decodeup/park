package com.parkloyalty.lpr.scan.util

import android.content.Context
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.xfprinter.FontType
import com.parkloyalty.lpr.scan.util.AppUtils.after21DateFromCurrentDate
import com.parkloyalty.lpr.scan.util.AppUtils.splitID
import com.parkloyalty.lpr.scan.util.Util.getOfficerNameBasedOnSettingsFlag

object CitationPrintSectionUtils {

    //Start Of Citation Info
    fun getTicketNumber(mIssuranceModel: CitationInsurranceDatabaseModel?, mCitationType : String=""): VehicleListModel {
        val citation = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)) {
            if( mCitationType == Constants.MUNICIPAL_ACTIVITY){
                citation.offNameFirst = mIssuranceModel?.citationData?.ticketNumberLabel
                citation.offTypeFirst = mIssuranceModel?.citationData?.ticketNumber
            }else {
                citation.offNameFirst = ""
                citation.offTypeFirst =
                    mIssuranceModel?.citationData?.ticketNumberLabel + ": " + mIssuranceModel?.citationData?.ticketNumber
            }
        } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true)){
            citation.offNameFirst = ""
            citation.offTypeFirst = mIssuranceModel?.citationData?.ticketNumber
        }else{
            citation.offNameFirst = mIssuranceModel?.citationData?.ticketNumberLabel
            citation.offTypeFirst = mIssuranceModel?.citationData?.ticketNumber
        }
        citation.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderTickerNumber.nullSafety()
        citation.type = 1
        citation.mHorizontalColon = 1
        citation.mAxisX = mIssuranceModel?.citationData?.mTicketNumberX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mTicketNumberY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mTicketNumberFontSize!!

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)
        ) {
            citation.mFontSize = Constants.PRINT_TEXT_LARGE!!
            citation.type = 2
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            citation.type = 3
        }
        if (mIssuranceModel?.citationData?.mTicketNumberColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mTicketNumberColumnSize.nullSafety(1).toInt()
        }
        return citation
    }

    fun getTicketDate(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): Pair<VehicleListModel, VehicleListModel> {

        //                    String currentString = getFullDate(mIssuranceModel.getCitationData().getTicketDate());
        val currentString = mIssuranceModel?.citationData?.ticketDatePrint.toString()
        val separated = currentString.split(" ").toTypedArray()

        val citation = VehicleListModel()
        citation.offNameFirst = mIssuranceModel?.citationData?.ticketDateLabel
        citation.offTypeFirst =
            separated[1] + "/" + separated[0] + "/" + separated[2]
        citation.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderTicketDate.nullSafety()
        //                        mCitationList.add(citation);
        var value =
            mIssuranceModel?.citationData?.mPrintOrderTicketDate.nullSafety()
        value += if (com.parkloyalty.lpr.scan.BuildConfig.FLAVOR.equals(
                com.parkloyalty.lpr.scan.interfaces.Constants.FLAVOR_TYPE_CLIFTON,
                true
            )
        ) {
            0.66
        } else if (value == 1.0 || value == 2.0 || value == 3.0) {
            0.5
        } else {
            0.16
        }
        val citationTime = VehicleListModel()
        citationTime.offNameFirst = context.getString(R.string.scr_lbl_print_issue_time)
        citationTime.offTypeFirst = separated[3] + " " + separated[4]
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true)) {
//                                citationTime.mPrintOrder = 2.66
            citationTime.mPrintOrder = value
            citation.type = 2
        } else {
            citationTime.mPrintOrder = value
        }

        citation.mHorizontalColon = 1

        citation.mAxisX = mIssuranceModel?.citationData?.mTicketDateX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mTicketDateY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mticketDateFont!!

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)
        ) {
            citationTime.mAxisX = citation.mAxisX + 340
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITY_VIRGINIA,
                ignoreCase = true
            )) {
            citation.type = 2
            citationTime.mAxisX = citation.mAxisX + 320
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)
        ) {
            citationTime.mAxisX = citation.mAxisX + 320
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            citationTime.mAxisX = citation.mAxisX
            citationTime.type = 2
        } else {
            citationTime.mAxisX = citation.mAxisX + 148 // Orleans
        }

        citationTime.mHorizontalColon = 0
        citationTime.mAxisY = mIssuranceModel?.citationData?.mTicketDateY!!
        citationTime.mFontSizeInt = mIssuranceModel?.citationData?.mticketDateFont!!

//                            printcommand = AppUtils.setXYforPrint(citation,sequence,"citation",printcommand!!)
//                            citation.mSectionHeader = layoutSectionTitle
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)
        ) {
            citation.offTypeFirst =
                separated[1] + "/" + separated[0] + "/" + separated[2] + " " + separated[3] + " " + separated[4]
        }
        if (mIssuranceModel?.citationData?.mTicketDateColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mTicketDateColumnSize.nullSafety(1).toInt()
        }
        return Pair(citation, citationTime)
    }

    fun getTicketTime(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val currentString =
            mIssuranceModel?.citationData?.ticketTimePrint.toString()
        val separated = currentString.split(" ").toTypedArray()

        val citation = VehicleListModel()
//                                    citation.offNameFirst = getString(R.string.scr_lbl_time_colon)
        citation.offNameFirst =
            mIssuranceModel?.citationData?.ticketTimeLabel
        citation.mAxisX = mIssuranceModel?.citationData?.mTicketTimeX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mticketTimeY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mticketTimeFont!!
        citation.offTypeFirst =
            separated[3] + " " + separated[4]
        citation.mPrintOrder = 2.5
        if (mIssuranceModel?.citationData?.mTicketTimeColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mTicketTimeColumnSize.nullSafety(1).toInt()
        }
        return citation
    }

    fun getTicketWeek(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val citation = VehicleListModel()
//                                    citation.offNameFirst = getString(R.string.scr_lbl_week_of_day)
        citation.offNameFirst =
            mIssuranceModel?.citationData?.ticketWeekLabel
        citation.offTypeFirst = mIssuranceModel?.citationData?.ticketWeek
        citation.mHorizontalColon = 1
        citation.mAxisX = mIssuranceModel?.citationData?.mTicketWeekX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mTicketWeekY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mTicketWeekFont!!
        citation.mPrintOrder = 2.0
        if (mIssuranceModel?.citationData?.mTicketWeekColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mTicketWeekColumnSize.nullSafety(1).toInt()
        }
        return citation
    }

    fun getCode2010(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val citation = VehicleListModel()
//                                    citation.offNameFirst = getString(R.string.scr_lbl_week_of_day)
        citation.offNameFirst =
            mIssuranceModel?.citationData?.code2010Label
        citation.offTypeFirst = mIssuranceModel?.citationData?.code2010
        citation.mHorizontalColon = 1
        citation.mAxisX = mIssuranceModel?.citationData?.mCode2010X!!
        citation.mAxisY = mIssuranceModel?.citationData?.mCode2010Y!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mCode2010Font!!
        citation.mPrintOrder = mIssuranceModel?.citationData?.mPrintOrdercode2010.nullSafety()

        if (mIssuranceModel?.citationData?.mCode2010ColumnSize.nullSafety(0) >= 1) {
            citation.type = mIssuranceModel?.citationData?.mCode2010ColumnSize.nullSafety(1).toInt()
        }
        return citation
    }

    fun getHearingDate(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val citation = VehicleListModel()
        citation.offNameFirst =
            mIssuranceModel?.citationData?.hearingDateLabel
        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true))
        {
            citation.offNameFirst = ""
        }
        citation.offTypeFirst = mIssuranceModel?.citationData?.hearingDate

        citation.mAxisX = mIssuranceModel?.citationData?.mHearingDateX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mHearingDateY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mHearingDateFont!!
        citation.mPrintOrder = mIssuranceModel?.citationData?.mPrintOrderHearingDate.nullSafety()
        citation.mHorizontalColon = 1
        citation.type = 2
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)
        ) {
            citation.type = 0
        }
        if (mIssuranceModel?.citationData?.mHearingDateColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mHearingDateColumnSize.nullSafety(1).toInt()
        }
        return citation
    }

    fun getHearingDescription(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val citation = VehicleListModel()

        citation.offNameFirst = ""
        citation.offTypeFirst = mIssuranceModel?.citationData?.hearingDescription

        citation.mAxisX = mIssuranceModel?.citationData?.mHearingDescriptionX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mHearingDescriptionY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mHearingDescriptionFont!!
        citation.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderHearingDescription.nullSafety()
        citation.mNoBox = 1
        citation.mHorizontalColon = 1
        citation.type = 3
        if (mIssuranceModel?.citationData?.mHearingDesriptionColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mHearingDesriptionColumnSize.nullSafety(1).toInt()
        }
        return citation
    }

    fun getOfficerDescription(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val citation = VehicleListModel()
        citation.offNameFirst =
            ""
        citation.offTypeFirst = mIssuranceModel?.citationData?.officerDescription

        citation.mAxisX = mIssuranceModel?.citationData?.mOfficerDescriptionX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mOfficerDescriptionY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mOfficerDescriptionFont!!
        citation.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderOfficerDescription.nullSafety()
        citation.mNoBox = 1
        citation.type = 3
        if (mIssuranceModel?.citationData?.mOfficerDescriptionColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mOfficerDescriptionColumnSize.nullSafety(1).toInt()
        }
        return citation
    }

    fun getTicketType(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): Pair<Boolean, VehicleListModel> {
        var isWarningSelected = false
        val ticketType = StringBuilder()
        val ticketTypeValue = StringBuilder()
        if (mIssuranceModel?.citationData?.ticketType != null && !mIssuranceModel?.citationData?.ticketType.nullSafety()
                .isEmpty()
        ) {
            ticketType.append(1)
            isWarningSelected = true
            if (ticketTypeValue.isEmpty()) {
                ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType)
            } else {
                ticketTypeValue.append(", " + mIssuranceModel?.citationData?.ticketType)
            }
        }
        if (mIssuranceModel?.citationData?.ticketType2 != null && !mIssuranceModel?.citationData?.ticketType2.nullSafety()
                .isEmpty()
        ) {
            ticketType.append(2)
            //                    ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType2)
            if (ticketTypeValue.isEmpty()) {
                ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType2)
            } else {
                ticketTypeValue.append(", " + mIssuranceModel?.citationData?.ticketType2)
            }
        }
        if (mIssuranceModel?.citationData?.ticketType3 != null && !mIssuranceModel?.citationData?.ticketType3.nullSafety()
                .isEmpty()
        ) {
            ticketType.append(3)
            //                    ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType3)
            if (ticketTypeValue.isEmpty()) {
                ticketTypeValue.append(mIssuranceModel?.citationData?.ticketType3)
            } else {
                ticketTypeValue.append(", " + mIssuranceModel?.citationData?.ticketType3)
            }
        }
        val citation = VehicleListModel()
        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                ignoreCase = false
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                ignoreCase = false
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = false) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = false)
        ) {
            citation.offNameFirst = " "
        } else {
            citation.offNameFirst = context.getString(R.string.scr_lbl_ticket_type)
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
        ) {
            citation.offTypeFirst = ticketType.toString()
            citation.checkBoxValue = ticketTypeValue.toString()
        } else {
            if (ticketTypeValue.toString().isEmpty()) {
                citation.offTypeFirst = Constants.COMMA
            } else {
                citation.offTypeFirst = ticketTypeValue.toString()
                citation.checkBoxValue = ticketTypeValue.toString()
            }
        }
        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)
        ) {
            citation.mHorizontalColon = 10// hide the ':' from print out Prime parking
        }
        citation.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderTicketType.nullSafety()


        citation.mAxisX = mIssuranceModel?.citationData?.mTicketTypeX!!
        citation.mAxisY = mIssuranceModel?.citationData?.mTicketTypeY!!
        citation.mFontSizeInt = mIssuranceModel?.citationData?.mTicketTypeFont!!
        if (mIssuranceModel?.citationData?.mTicketTypeColumnSize.nullSafety(0) >= 1) {
            citation.type =
                mIssuranceModel?.citationData?.mTicketTypeColumnSize.nullSafety(1).toInt()
        }
        return Pair(isWarningSelected, citation)
    }
    //End Of Citation Info

    //Start Of Officer Info
    fun getOfficerDetails(
        officerNameFormatForPrint: String?,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val data = VehicleListModel()
        data.offNameFirst = mIssuranceModel?.citationData?.officer?.officerDetailsLabel

        if (!officerNameFormatForPrint.isNullOrEmpty()) {
            data.offTypeFirst = getOfficerNameBasedOnSettingsFlag(
                officerName = mIssuranceModel?.citationData?.officer?.officerDetails.nullSafety(),
                flag = officerNameFormatForPrint.nullSafety()
            )
        } else {
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)
            ) {
                data.offTypeFirst =
                    Util.officerNameForBurbank(mIssuranceModel?.citationData?.officer?.officerDetails.nullSafety())
            } else {
                data.offTypeFirst =
                    Util.officerName(mIssuranceModel?.citationData?.officer?.officerDetails.nullSafety())
                //                        data.offTypeFirst = mIssuranceModel?.citationData?.officer?.officerDetails
            }
        }

        data.mHorizontalColon = 1
        data.mAxisX = mIssuranceModel?.citationData?.officer?.mOfficerDetailsX!!
        data.mAxisY = mIssuranceModel?.citationData?.officer?.mOfficerDetailsY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mOfficerDetailsFont!!
        data.mPrintOrder =
            mIssuranceModel?.citationData?.officer?.mPrintOrderOfficerDetails.nullSafety()

        if (mIssuranceModel?.citationData?.officer?.mOfficerDetailsColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.officer?.mOfficerDetailsColumnSize.nullSafety(1)
                    .toInt()
        }
        return data
    }

    fun getOfficerID(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        //                    data.offNameFirst = getString(R.string.scr_lbl_officer_id)
        data.offNameFirst = mIssuranceModel?.citationData?.officer?.officerIdLabel
        data.offTypeFirst =
            splitID(mIssuranceModel?.citationData?.officer?.officerId.nullSafety())
        data.mPrintOrder =
            mIssuranceModel?.citationData?.officer?.mPrintOrderOfficerId.nullSafety()

        data.mAxisX = mIssuranceModel?.citationData?.officer?.mBadgeIdX!!
        data.mAxisY = mIssuranceModel?.citationData?.officer?.mBadgeIdY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mBadgeIdFont!!
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)
        ) {
            data.mHorizontalColon = 1
        }

        if (mIssuranceModel?.citationData?.officer?.mBadgeIdColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.officer?.mBadgeIdColumnSize.nullSafety(1).toInt()
        }
        return data
    }

    fun getBadgeID(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)) {

            data.offNameFirst =
                mIssuranceModel?.citationData?.officer?.badgeIdLabel + "   " + mIssuranceModel?.citationData?.officer?.agencyLabel + ":"
            data.offTypeFirst =
                mIssuranceModel?.citationData?.officer?.badgeId + "        " + mIssuranceModel?.citationData?.officer?.agency
            data.mPrintOrder =
                mIssuranceModel?.citationData?.officer?.mPrintOrderBadgeId.nullSafety()
            data.mAxisX = mIssuranceModel?.citationData?.officer?.mBadgeIdX!!
            data.mAxisY = mIssuranceModel?.citationData?.officer?.mBadgeIdY!!
            data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mBadgeIdFont!!

            return data
        } else {
            data.offNameFirst = mIssuranceModel?.citationData?.officer?.badgeIdLabel
            data.offTypeFirst = mIssuranceModel?.citationData?.officer?.badgeId
            data.mPrintOrder =
                mIssuranceModel?.citationData?.officer?.mPrintOrderBadgeId.nullSafety()

            data.mAxisX = mIssuranceModel?.citationData?.officer?.mBadgeIdX!!
            data.mAxisY = mIssuranceModel?.citationData?.officer?.mBadgeIdY!!
            data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mBadgeIdFont!!
            //                    mOfficerList.add(data);
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)
            ) {
                data.mHorizontalColon = 1
            }

            if (mIssuranceModel?.citationData?.officer?.mBadgeIdColumnSize.nullSafety(0) >= 1) {
                data.type =
                    mIssuranceModel?.citationData?.officer?.mBadgeIdColumnSize.nullSafety(1).toInt()
            }
            return data
        }
    }

    fun getAgency(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        //                        data.offNameFirst = getString(R.string.scr_lbl_agency)
        data.offNameFirst = mIssuranceModel?.citationData?.officer?.agencyLabel
        data.offTypeFirst = mIssuranceModel?.citationData?.officer?.agency
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)
        ) {
            try {
                data.offTypeFirst = mIssuranceModel?.citationData?.officer?.agency!!.split("-")[0]
            } catch (e: Exception) {
                data.offTypeFirst = mIssuranceModel?.citationData?.officer?.agency
            }
        }
        data.mPrintOrder =
            mIssuranceModel?.citationData?.officer?.mPrintOrderAgency.nullSafety()

        data.mAxisX = mIssuranceModel?.citationData?.officer?.mAgencyX!!
        data.mAxisY = mIssuranceModel?.citationData?.officer?.mAgencyY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mAgencyFont!!

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            ) ||BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)
        ) {
            data.type = 2
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, true)
        ) {
            data.type = 1
        }
        if (mIssuranceModel?.citationData?.officer?.mAgencyColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.officer?.mAgencyColumnSize.nullSafety(1).toInt()
        }
        return data
    }

    fun getBeat(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        //                    data.offNameFirst = getString(R.string.scr_lbl_beat)
        data.offNameFirst = mIssuranceModel?.citationData?.officer?.beatLabel
//                        data.offTypeFirst = mIssuranceModel?.citationData?.officer?.beat?.getOfficerBeatNam;eForPrint()
        data.offTypeFirst =
            if (mIssuranceModel?.citationData?.officer?.beat!!.toString().isNotEmpty())
                mIssuranceModel?.citationData?.officer?.beat else Constants.COMMA

        data.mPrintOrder =
            mIssuranceModel?.citationData?.officer?.mPrintOrderBeat.nullSafety()

        data.mAxisX = mIssuranceModel?.citationData?.officer?.mBeatX!!
        data.mAxisY = mIssuranceModel?.citationData?.officer?.mBeatY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mBeatFont!!
        if (mIssuranceModel?.citationData?.officer?.mBeatColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.officer?.mBeatColumnSize.nullSafety(1).toInt()
        }
        return data
    }

    fun getSquad(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        //                    data.offNameFirst = getString(R.string.scr_lbl_squad)
        data.offNameFirst = mIssuranceModel?.citationData?.officer?.squadLable
        data.offTypeFirst = mIssuranceModel?.citationData?.officer?.squad
        data.mPrintOrder =
            mIssuranceModel?.citationData?.officer?.mPrintOrderSquad.nullSafety()

        data.mAxisX = mIssuranceModel?.citationData?.officer?.mSquadX!!
        data.mAxisY = mIssuranceModel?.citationData?.officer?.mSquadY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mSquadFont!!
        if (mIssuranceModel?.citationData?.officer?.mSquadColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.officer?.mSquadColumnSize.nullSafety(1).toInt()
        }
        return data
    }

    fun getObservationTimeLabel(
        sharedPreference: SharedPref,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val data = VehicleListModel()
        data.offNameFirst =
            mIssuranceModel?.citationData?.officer?.observationTimeLabel//getString(R.string.scr_lbl_observation_time)

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, true)) {
            data.offTypeFirst = mIssuranceModel?.citationData?.officer?.observationTime
        } else {
            data.offTypeFirst =
                sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
        }

        data.mPrintOrder =
            mIssuranceModel?.citationData?.officer?.mPrintOrderObservationtime.nullSafety()

        data.mAxisX = mIssuranceModel?.citationData?.officer?.mObservationTimeX!!
        data.mAxisY = mIssuranceModel?.citationData?.officer?.mObservationTimeY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.officer?.mObservationTimeFont!!
        if (mIssuranceModel?.citationData?.officer?.mObservationColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.officer?.mObservationColumnSize.nullSafety(1).toInt()
        }
        return data
    }
    //End Of Officer Info

    //Start of Remark Info
    fun getLocationNotes(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        //                    data.offNameFirst = getString(R.string.scr_lbl_notes)
        data.offNameFirst = mIssuranceModel?.citationData?.locationNotesLabel
        data.offTypeFirst = mIssuranceModel?.citationData?.locationNotes
        data.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderLocationNotes.nullSafety()

        return data
    }

    fun getLocationNotes1(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        //                    data.offNameFirst = getString(R.string.scr_lbl_note1)
        data.offNameFirst = mIssuranceModel?.citationData?.locationNotes1Label
        data.offTypeFirst = mIssuranceModel?.citationData?.locationNotes1
        data.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderLocationNotes1.nullSafety()

        return data
    }

    fun getLocationRemarks(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true)
        ) {
            data.offNameFirst = ""
        } else {
            //                        data.offNameFirst = getString(R.string.scr_lbl_remarks)
            data.offNameFirst = mIssuranceModel?.citationData?.locationRemarksLabel
        }
        data.mTextAlignment = Constants.PRINT_LAYOUT_VERTICAL
        data.mFontSize = Constants.PRINT_TEXT_LARGE!!
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)) {
            if (mIssuranceModel?.citationData?.locationRemarks.nullSafety().isEmpty()) {
                data.offTypeFirst = Constants.DOT
            } else {
                data.offTypeFirst = mIssuranceModel?.citationData?.locationRemarks
            }

        } else {
            data.offTypeFirst = mIssuranceModel?.citationData?.locationRemarks.nullSafety()
        }

//        data.offTypeFirst = mIssuranceModel?.citationData?.locationRemarks.nullSafety()
        data.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderLocationRemarks.nullSafety()
        data.mAxisX = mIssuranceModel?.citationData?.mRemarkX!!
        data.mAxisY = mIssuranceModel?.citationData?.mRemarkY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.mRemarkFont!!
        data.fontTypeForXFPrinter = FontType.TYPE_BOLD
        data.mNoBox = 1

        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
        ) {
            data.mHorizontalColon = 1
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)
        ) {
            data.type = 2
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            data.type = 3
        }
//        if(mIssuranceModel?.citationData?.mRemarkColumnSize.nullSafety(0) >= 1)
//        {
//            data.type = mIssuranceModel?.citationData?.mRemarkColumnSize.nullSafety(1).toInt()
//        }
        return data
    }

    fun getLocationRemark1(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                DuncanBrandingApp13()
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PARK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_COB, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true
            )

            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BURLINGTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_Easton, ignoreCase = true
            )
        ) {
            data.offNameFirst = ""
        } else {
            //                        data.offNameFirst = getString(R.string.scr_lbl_remark1)
            data.offNameFirst = mIssuranceModel?.citationData?.locationRemarks1Label
        }
        //                    data.setOffNameFirst(getString(R.string.scr_lbl_remark1));

        data.offTypeFirst = mIssuranceModel?.citationData?.locationRemarks1.nullSafety()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)) {
            if (mIssuranceModel?.citationData?.locationRemarks1.nullSafety().isEmpty()) {
                data.offTypeFirst = Constants.DOT
            } else {
                data.offTypeFirst = mIssuranceModel?.citationData?.locationRemarks1
            }

        }
        data.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderLocationRemarks1.nullSafety()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)
        ) {
            data.type = 2
        } else {
            data.type = 1
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)
        ) {
            data.mHorizontalColon = 1
        }

        data.mAxisX = mIssuranceModel?.citationData?.mRemark1X!!
        data.mAxisY = mIssuranceModel?.citationData?.mRemark1Y!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.mRemark1Font!!
        data.mNoBox = 1
        data.fontTypeForXFPrinter = FontType.TYPE_BOLD
//        if(mIssuranceModel?.citationData?.mRemark1ColumnSize.nullSafety(0) >= 1)
//        {
//            data.type = mIssuranceModel?.citationData?.mRemark1ColumnSize.nullSafety(1).toInt()
//        }
        return data
    }

    fun getLocationRemark2(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val data = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            ) ||  BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)
        ) {
            data.offNameFirst = ""
        } else {
            data.offNameFirst = mIssuranceModel?.citationData?.locationRemarks2Label
        }
        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true))
        {
            data.mHorizontalColon = 1
        }
        data.offTypeFirst = mIssuranceModel?.citationData?.locationRemarks2.nullSafety()
        data.mPrintOrder =
            mIssuranceModel?.citationData?.mPrintOrderLocationRemarks2.nullSafety()

        data.mAxisX = mIssuranceModel?.citationData?.mRemark2X!!
        data.mAxisY = mIssuranceModel?.citationData?.mRemark2Y!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.mRemark2Font!!
//        if(mIssuranceModel?.citationData?.mRemark2ColumnSize.nullSafety(0) >= 1)
//        {
//            data.type = mIssuranceModel?.citationData?.mRemark2ColumnSize.nullSafety(1).toInt()
//        }
        return data
    }
    //End of Remark Info

    //Start of Location Info
    fun getLocation(
        context: Context,
        sharedPreference: SharedPref,
        mLocation: String,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): Pair<String, VehicleListModel> {
        val location = VehicleListModel()
        var orderNumber: String? = null

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
        ) {
            orderNumber =
                if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderStreet != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderStreet!! else Constants.PRINT_LAYOUT_ORDER_SPARATER
            location.mPrintOrder =
                mIssuranceModel?.citationData?.location?.mPrintOrderStreet.nullSafety()
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)
        ) {
            orderNumber =
                if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderLot != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderLot!! else Constants.PRINT_LAYOUT_ORDER_SPARATER
            location.mPrintOrder =
                mIssuranceModel?.citationData?.location?.mPrintOrderLot.nullSafety()
        } else {
            orderNumber =
                if (mIssuranceModel?.citationData?.location?.mPrintLayoutOrderblock != null) mIssuranceModel?.citationData?.location?.mPrintLayoutOrderblock!! else Constants.PRINT_LAYOUT_ORDER_SPARATER
            location.mPrintOrder =
                mIssuranceModel?.citationData?.location?.mPrintOrderblock.nullSafety()
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            val locationDec =
                sharedPreference.read(SharedPrefKey.SM_LOCATION_DESCRIPTION_PRINT, "").toString()!!
                    .split("#")
            val finalLocation = locationDec[0].toString() + "\n" + locationDec[1].toString()
            location.offNameFirst = finalLocation
            location.type = 3
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                ignoreCase = true
            )
        ) {
            location.offNameFirst =
                mIssuranceModel?.citationData?.location?.streetLabel.nullSafety()
        }else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_RISE_TEK_OKC,
                ignoreCase = true
            )
        ) {
            location.offNameFirst = ""
        } else {
            location.offNameFirst = context.getString(R.string.scr_lbl_location)
        }

//                location.offNameFirst = getString(R.string.scr_lbl_location)
        location.offTypeFirst = mLocation.trim()
        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true)
        ) {
            location.offNameFirst = "Zone"
        }

        location.type = 2
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)
        ) {
            location.type = 3
        }
        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
        ) {
            location.type = 1
        }

        if ((BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)) ||
            mIssuranceModel?.citationData?.location?.mBlockY!!.equals(0.0)
        ) {
            location.mAxisX = mIssuranceModel?.citationData?.location?.mStreetX!!
            location.mAxisY = mIssuranceModel?.citationData?.location?.mStreetY!!
            location.mFontSizeInt = mIssuranceModel?.citationData?.location?.mStreetFont!!
        } else {
            location.mAxisX = mIssuranceModel?.citationData?.location?.mBlockX!!
            location.mAxisY = mIssuranceModel?.citationData?.location?.mBlockY!!
            location.mFontSizeInt = mIssuranceModel?.citationData?.location?.mBlockFont!!
        }
        location.mHorizontalColon = 1
        if (mIssuranceModel?.citationData?.location?.mBlockColumnSize.nullSafety(0) >= 1) {
            location.type =
                mIssuranceModel?.citationData?.location?.mBlockColumnSize.nullSafety(1).toInt()
        }
        return Pair(orderNumber, location)
    }

    fun getDirection(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val location = VehicleListModel()
//                    location.offNameFirst = getString(R.string.scr_lbl_direction)
        location.offNameFirst = mIssuranceModel?.citationData?.location?.directionLabel
        location.offTypeFirst =
            if (mIssuranceModel?.citationData?.location?.direction!!.toString().isNotEmpty())
                mIssuranceModel?.citationData?.location?.direction else Constants.COMMA
        location.mPrintOrder =
            mIssuranceModel?.citationData?.location?.mPrintOrderDirection.nullSafety()

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)) {
            location.type = 3
        }

        location.mAxisX = mIssuranceModel?.citationData?.location?.mDirectionX!!
        location.mAxisY = mIssuranceModel?.citationData?.location?.mDirectionY!!
        location.mFontSizeInt = mIssuranceModel?.citationData?.location?.mDirectionFont!!
        if (mIssuranceModel?.citationData?.location?.mDirectionColumnSize.nullSafety(0) >= 1) {
            location.type =
                mIssuranceModel?.citationData?.location?.mDirectionColumnSize.nullSafety(1).toInt()
        }
        return location
    }

    fun getMeterName(
        sharedPreference: SharedPref,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val location = VehicleListModel()
//                    location.offNameFirst = getString(R.string.scr_lbl_meter_name)
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            val meterDec =
                sharedPreference.read(SharedPrefKey.SM_METER_DESCRIPTION_PRINT, "").toString()!!
                    .split("#")
            val finalMeter =
                mIssuranceModel?.citationData?.voilation?.locationDescr + "\n \t \t \t" + mIssuranceModel?.citationData?.location?.meterNameLabel + ": " + mIssuranceModel?.citationData?.location?.meterName + "\n" + meterDec[1].toString()
            location.offNameFirst = finalMeter
            location.offTypeFirst = ""
            location.type = 3
        } else {
            location.offNameFirst = mIssuranceModel?.citationData?.location?.meterNameLabel
            location.offTypeFirst = mIssuranceModel?.citationData?.location?.meterName
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)) {
            location.mHorizontalColon = 1
        }

        location.mPrintOrder =
            mIssuranceModel?.citationData?.location?.mPrintOrderMeterName.nullSafety()

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
        ) {
            location.type = 2
        }

        location.mAxisX = mIssuranceModel?.citationData?.location?.mMeterX!!
        location.mAxisY = mIssuranceModel?.citationData?.location?.mMeterY!!
        location.mFontSizeInt = mIssuranceModel?.citationData?.location?.mMeterFont!!
        if (mIssuranceModel?.citationData?.location?.mMeterColumnSize.nullSafety(0) >= 1) {
            location.type =
                mIssuranceModel?.citationData?.location?.mMeterColumnSize.nullSafety(1).toInt()
        }
        return location
    }

    fun getLot(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val location = VehicleListModel()
//                    location.offNameFirst = getString(R.string.scr_lbl_lot)
        location.offNameFirst = mIssuranceModel?.citationData?.location?.lotLabel
        location.offTypeFirst = mIssuranceModel?.citationData?.location?.lot
        location.mPrintOrder =
            mIssuranceModel?.citationData?.location?.mPrintOrderLot.nullSafety()

        location.type = 2
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)) {
            location.offNameFirst = ""
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)
        ) {
            location.type = 3
        }
        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)
        ) {
            location.type = 1
        }
        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true))
        {
            location.mHorizontalColon = 1
        }

        location.mAxisX = mIssuranceModel?.citationData?.location?.mLotX!!
        location.mAxisY = mIssuranceModel?.citationData?.location?.mLotY!!
        location.mFontSizeInt = mIssuranceModel?.citationData?.location?.mLotFont!!
        if (mIssuranceModel?.citationData?.location?.mLotColumnSize.nullSafety(0) >= 1) {
            location.type =
                mIssuranceModel?.citationData?.location?.mLotColumnSize.nullSafety(1).toInt()
        }
        return location
    }

    fun getBlock(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val block = VehicleListModel()
        block.offNameFirst = mIssuranceModel?.citationData?.location?.blockLabel
        block.offTypeFirst = mIssuranceModel?.citationData?.location?.block
        block.mPrintOrder =
            mIssuranceModel?.citationData?.location?.mPrintOrderblock.nullSafety()

        block.mAxisX = mIssuranceModel?.citationData?.location?.mBlockX!!
        block.mAxisY = mIssuranceModel?.citationData?.location?.mBlockY!!
        block.mFontSizeInt = mIssuranceModel?.citationData?.location?.mBlockFont!!

        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                ignoreCase = true
            )
        ) {
            block.mHorizontalColon = 1
        }


        if (mIssuranceModel?.citationData?.location?.mBlockColumnSize.nullSafety(0) >= 1) {
            block.type =
                mIssuranceModel?.citationData?.location?.mBlockColumnSize.nullSafety(1).toInt()
        }
        return block
    }

    fun getSpaceName(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val location = VehicleListModel()
//                    location.offNameFirst = getString(R.string.scr_lbl_space_without_colon)
        location.offNameFirst = mIssuranceModel?.citationData?.location?.spaceNameLabel
        location.offTypeFirst = mIssuranceModel?.citationData?.location?.spaceName
        location.mPrintOrder =
            mIssuranceModel?.citationData?.location?.mPrintOrderSpaceName.nullSafety()

        location.mAxisX = mIssuranceModel?.citationData?.location?.mSpaceX!!
        location.mAxisY = mIssuranceModel?.citationData?.location?.mSpaceY!!
        location.mFontSizeInt = mIssuranceModel?.citationData?.location?.mSpaceFont!!

        if (
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DANVILLE_VA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CAMDEN,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTH_LAKE,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WINPARK_TX,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PARKX,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                ignoreCase = true
            )
        ) {
            location.mHorizontalColon = 1
        }
        if (mIssuranceModel?.citationData?.location?.mSpaceColumnSize.nullSafety(0) >= 1) {
            location.type =
                mIssuranceModel?.citationData?.location?.mSpaceColumnSize.nullSafety(1).toInt()
        }
        return location
    }

    fun getCityZone(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val data = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DUNCAN,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HARTFORD,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MACKAY_SAMPLE,
                ignoreCase = true
            )
        ) {
            data.offNameFirst = context.getString(R.string.scr_lbl_pbc_zone)
            data.offTypeFirst = mIssuranceModel?.citationData?.location?.pcbZone
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
                ignoreCase = true
            )||BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
                ignoreCase = true
            )
        ) {
            data.offNameFirst = Constants.COMMA
            data.offTypeFirst = mIssuranceModel?.citationData?.location?.cityZone
        } else {
//                        data.offNameFirst = getString(R.string.scr_lbl_zone)
            data.offNameFirst =
                if (mIssuranceModel?.citationData?.location?.pcbZoneLabel != null) mIssuranceModel?.citationData?.location?.pcbZoneLabel else mIssuranceModel?.citationData?.location?.cityZoneLabel
            if (mIssuranceModel?.citationData?.location?.pcbZone != null && mIssuranceModel?.citationData?.location?.pcbZone!!.isNotEmpty()) {
                data.offTypeFirst = mIssuranceModel?.citationData?.location?.pcbZone
            } else {
                data.offTypeFirst = mIssuranceModel?.citationData?.location?.cityZone
            }
        }

        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAMETRO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CORPUSCHRISTI,
                ignoreCase = true
            )
        ) {
            data.type = 2
        }
        data.mHorizontalColon = 1
        data.mPrintOrder =
            mIssuranceModel?.citationData?.location?.mPrintOrderCityZone.nullSafety()

        data.mAxisX = mIssuranceModel?.citationData?.location?.mCityZoneX!!
        data.mAxisY = mIssuranceModel?.citationData?.location?.mCityZoneY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.location?.mCityZoneFont!!
        if (mIssuranceModel?.citationData?.location?.mCityZoneColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.location?.mCityZoneColumnSize.nullSafety(1).toInt()
        }
        return data
    }

    fun getPCBZone(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val data = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)
        ) {
            data.offNameFirst = context.getString(R.string.scr_lbl_pbc_zone)
        } else {
            data.offNameFirst = mIssuranceModel?.citationData?.location?.pcbZoneLabel
        }
        data.offTypeFirst = mIssuranceModel?.citationData?.location?.pcbZone
        data.mPrintOrder =
            mIssuranceModel?.citationData?.location?.mPrintOrderPcbZone.nullSafety()

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
        ) {
            data.type = 3
        }
        data.mHorizontalColon = 1
        data.mAxisX = mIssuranceModel?.citationData?.location?.mPcbZoneX!!
        data.mAxisY = mIssuranceModel?.citationData?.location?.mPcbZoneY!!
        data.mFontSizeInt = mIssuranceModel?.citationData?.location?.mPcbZoneFont!!
        if (mIssuranceModel?.citationData?.location?.mPcbZoneColumnSize.nullSafety(0) >= 1) {
            data.type =
                mIssuranceModel?.citationData?.location?.mPcbZoneColumnSize.nullSafety(1).toInt()
        }
        return data
    }
    //End of Location Info

    //Start of Vehicle Info
    fun getLicensePlate(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.licensePlateLabel
        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.licensePlate
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderLicensePlate.nullSafety()

        Vehdata.mHorizontalColon = 1
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true)
        ) {
            Vehdata.type = 2
        }

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mLicensePlateX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mLicensePlateY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mLicenseFont!!
        if (mIssuranceModel?.citationData?.vehicle?.mLicenseColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mLicenseColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getLicensePlateState(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.stateLabel
        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.state
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderState.nullSafety()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)) {
            Vehdata.type = 1
        }
        Vehdata.mHorizontalColon = 1

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mStateX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mStateY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mStateFont!!
        if (mIssuranceModel?.citationData?.vehicle?.mStateColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mStateColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getVehicleMake(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.makeFullNameLabel
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)
        ) {
            Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.makeFullName
        } else {
            Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.make
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true)) {
            Vehdata.offNameFirst = "Vehicle:"
            Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.make + "  " +
                    mIssuranceModel?.citationData?.vehicle?.color
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true)) {
            Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.makeFullName
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderMake.nullSafety()
        //                    mVehicleList.add(Vehdata);
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, true)
        ) {
            Vehdata.type = 1
        } else if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true)
        ) {
            Vehdata.type = 0
        } else {
            Vehdata.type = 2
        }

        Vehdata.mHorizontalColon = 1

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mMakeX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mMakeY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mMakeFont!!
        if (mIssuranceModel?.citationData?.vehicle?.mMakeColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mMakeColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getVehicleModel(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.modelLabel
        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.model
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderModel.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mModelX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mModelY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mModelFont!!
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)
        ) {
            Vehdata.type = 2
        }
        Vehdata.mHorizontalColon = 1
        if (mIssuranceModel?.citationData?.vehicle?.mModelColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mModelColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getVehicleColor(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.colorLabel

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)
        ) {
            Vehdata.offTypeFirst =
                mIssuranceModel?.citationData?.vehicle?.colorCodeFullName
        } else {
            Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.color
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderColor.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mColorX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mColorY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mColorFont!!
        Vehdata.mHorizontalColon = 1

        if (mIssuranceModel?.citationData?.vehicle?.mColorColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mColorColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getVehicleBodyStyle(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.bodyStyleLabel
        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.bodyStyle
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderBodyStyle.nullSafety()

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)
        ) {
            Vehdata.type = 2
        }

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mBodyStyleX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mBodyStyleY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mBodyFont!!

        if (
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DANVILLE_VA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CAMDEN,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTH_LAKE,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WINPARK_TX,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PARKX,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_OXFORD,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                ignoreCase = true
            )
        ) {
            Vehdata.mHorizontalColon = 1
        }

        if (mIssuranceModel?.citationData?.vehicle?.mBodyColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mBodyColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getDecalYear(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.decalYearLabel
        Vehdata.offTypeFirst =
            if (mIssuranceModel?.citationData?.vehicle?.decalYear!!.toString().isNotEmpty())
                mIssuranceModel?.citationData?.vehicle?.decalYear else Constants.COMMA
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderDecalYear.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mDecalYearX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mDecalYearY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mDecalYearFont!!

        Vehdata.mHorizontalColon = 1
        if (mIssuranceModel?.citationData?.vehicle?.mDecalYearColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mDecalYearColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getDecalNumber(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.decalNumberLabel
        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.decalNumber
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
        ) {
            Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.decalNumber + " " +
                    mIssuranceModel?.citationData?.vehicle?.bodyStyle
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderDecalNumber.nullSafety()
        Vehdata.mHorizontalColon = 1
        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mDecalNumberX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mDecalNumberY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mDecalNumberFont!!
        if (mIssuranceModel?.citationData?.vehicle?.mDecalNumberColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mDecalNumberColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getVinNumber(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.vinNumberLabel

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true)) {
            if (mIssuranceModel?.citationData?.vehicle?.vinNumber.nullSafety().isEmpty()) {
                Vehdata.offTypeFirst = Constants.DOT
            } else {
                Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.vinNumber
            }

        } else {
            Vehdata.offTypeFirst =
                if (mIssuranceModel?.citationData?.vehicle?.vinNumber!!.toString().isNotEmpty())
                    mIssuranceModel?.citationData?.vehicle?.vinNumber else Constants.COMMA
        }


        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderVinNumber.nullSafety()
        Vehdata.type = 2
        Vehdata.mHorizontalColon = 1
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, true)
        ) {
            Vehdata.type = 1
        }

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mVinNumberX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mVinNumberY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mVinNumberFont!!

        if (mIssuranceModel?.citationData?.vehicle?.mVinNumberColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mVinNumberColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getVehicleExpiration(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.vehicle?.expirationLabel
        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.vehicle?.expiration

        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.vehicle?.mPrintOrderExpiration.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.vehicle?.mExpirationX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.vehicle?.mExpirationY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.vehicle?.mExpirationFont!!

        Vehdata.mHorizontalColon = 1
        if (mIssuranceModel?.citationData?.vehicle?.mExpirationColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.vehicle?.mExpirationColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }
    //End of Vehicle Info

    //Start of Violation Info
    fun getViolationCode(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val Vehdata = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
        ) {
            Vehdata.offNameFirst = context.getString(R.string.scr_lbl_code)
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_RISE_TEK_OKC,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            ) ||BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true)
        ) {
            Vehdata.offNameFirst = ""
        } else {
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.codeLabel
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)
        ) {
            Vehdata.mTextAlignment = Constants.PRINT_LAYOUT_VERTICAL
            Vehdata.mFontSize = Constants.PRINT_TEXT_LARGE
        }

//                        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.voilation?.code
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true
            )
        ) {
            Vehdata.offTypeFirst =
                "IN VIOLATION OF SEC " + mIssuranceModel?.citationData?.voilation?.code
            Vehdata.type = 3
        } else {
            Vehdata.offTypeFirst = mIssuranceModel?.citationData?.voilation?.code
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderCode.nullSafety()
        Vehdata.mHorizontalColon = 1
        //                    mViolationList.add(Vehdata);

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
                ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, true)
        ) {
            Vehdata.type = 2
        }

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mViolationX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mViolationY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mViolationFont!!
        Vehdata.mNoBox = 1 // key box

        if (mIssuranceModel?.citationData?.voilation?.mViolationColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mViolationColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    fun getViolationAmount(mIssuranceModel: CitationInsurranceDatabaseModel?,mCitationType : String=""): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.amountLabel

        if ((mIssuranceModel?.citationData?.voilation?.amountDay ?: 0) > 0) {
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.amountLabel + " " + mIssuranceModel?.citationData?.voilation?.amountDay?.let {
                    after21DateFromCurrentDate(it)
                } + ""
        }


        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)
        ) {
            val d =
                mIssuranceModel?.citationData?.voilation?.amount.nullSafety().toDouble()
            val s = String.format("%.2f", d)
            Vehdata.offTypeFirst = "$ $s"
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)) {
                if( mCitationType == Constants.MUNICIPAL_ACTIVITY){

                }else {
                    Vehdata.offNameFirst = ""
                    Vehdata.offTypeFirst =
                        mIssuranceModel?.citationData?.voilation?.amountLabel?.replace("#","$$s")
                }
            }
        } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)){
            // Safely get violation amount and convert to Double
            val amountStr = mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
            val baseAmount = amountStr.toDoubleOrNull() ?: 0.0

            // Safely parse dueDateCost string to Double, fallback to 7.0 if invalid or null
            val percent = mIssuranceModel?.citationData?.voilation?.dueDateCost
                ?.takeIf { !it.isNullOrBlank() && it != "null" }
                ?.toDoubleOrNull() ?: 7.0

            // Calculate 7% extra
            val totalRate7Percent = (baseAmount * percent) / 100

            val calculatedAmount = baseAmount + totalRate7Percent

            // Format values to two decimals
//            val formattedBaseAmount = String.format("%.2f", baseAmount)
            val formattedTotalAmount = String.format("%.2f", calculatedAmount)

            // Build label text
            val label = mIssuranceModel?.citationData?.voilation?.amountLabel.nullSafety()

            // Assign values safely
            Vehdata.offNameFirst = "$label"
            Vehdata.offTypeFirst = "$formattedTotalAmount"

        }else {
            val amount =
                mIssuranceModel?.citationData?.voilation?.amount.nullSafety()
                    .split(".").toTypedArray()[0]
            Vehdata.offTypeFirst = "$ $amount.00"
        }


        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
        ) {
            Vehdata.mFontSize = Constants.PRINT_TEXT_LARGE
        }
        Vehdata.type = 2
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)
        ) {
            Vehdata.type = 1
        }


        if (
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                ignoreCase = true
            ) ||BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DANVILLE_VA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CAMDEN,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTH_LAKE,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WINPARK_TX,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PARKX,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_OXFORD,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                ignoreCase = true
            )|| BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PRIME_PARKING,
                ignoreCase = true
            )
        ) {
            Vehdata.mHorizontalColon = 1
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                ignoreCase = true
            )
        ) {
            Vehdata.mHorizontalColon = 3
        } else {
            Vehdata.mHorizontalColon = 2
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
        ) {
            Vehdata.type = 1
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderAmount.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mAmountX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mAmountY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mAmountFont!!
        if (mIssuranceModel?.citationData?.voilation?.mAmountColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mAmountColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    /**
     * Due Date: If Paid After
     */
    fun getAmountDueDate(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        if (mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.isNotEmpty() &&
            mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.equals(
                "If Paid After", ignoreCase = true
            ) ||
            mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.isNotEmpty() &&
            mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.equals(
                "If Paid After:", ignoreCase = true
            ) ||
            mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.isNotEmpty() &&
            mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.equals(
                "Due Date", ignoreCase = true
            ) ||
            mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.isNotEmpty() &&
            mIssuranceModel?.citationData?.voilation?.amountDueDateLabel!!.equals(
                "Due After", ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
        ) {
            Vehdata.type = 2
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.amountDueDateLabel + " " + mIssuranceModel?.citationData?.voilation?.mLateFineDays?.let {
                    after21DateFromCurrentDate(it)
                } + ""
        } else {
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.amountDueDateLabel + " " + mIssuranceModel?.citationData?.voilation?.mLateFineDays?.let {
                    after21DateFromCurrentDate(it)
                } + ""
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)) {
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.mLateFineDays?.let {
                    after21DateFromCurrentDate(it)
                } + " " + mIssuranceModel?.citationData?.voilation?.amountDueDateLabel
        }
        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)
        ) {
            Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.amountDueDateLabel
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)
        ) {
            val d =
                mIssuranceModel?.citationData?.voilation?.amountDueDate.nullSafety().toDouble()
            val s = String.format("%.2f", d)
            Vehdata.offTypeFirst = "$ $s"
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)) {
                Vehdata.offNameFirst = ""
                Vehdata.offTypeFirst =
                    mIssuranceModel?.citationData?.voilation?.amountDueDateLabel + ": " + "$ $s"
            }
        } else {
            val amount =
                mIssuranceModel?.citationData?.voilation?.amountDueDate.nullSafety()
                    .split(".").toTypedArray()[0]
            Vehdata.offTypeFirst = "$ $amount.00"


        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true)
        ) {
            Vehdata.offTypeFirst = ""
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true)) {
            Vehdata.mHorizontalColon = 3
        } else {
            Vehdata.mHorizontalColon = 2
        }


        /**
         * We do not need to show this label & value, if the site is Carta & late fine day is equals to 0
         * So reason being, we are hiding this & changing the order to align next value to replace this
         * If paid after with be moved to righ & hidden
         * Due to this, Right side of the value with be here in next iterate
         */
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CARTA,
                true
            ) && mIssuranceModel?.citationData?.voilation?.mLateFineDays.nullSafety() == 0
        ) {

            Vehdata.mPrintOrder = 3.66
            Vehdata.type = 1
            Vehdata.offNameFirst = ""
            Vehdata.offTypeFirst = ""
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)) {
            Vehdata.type = 2
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderAmountDueDate.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mLateFineX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mLateFineY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mLateFineFont!!
        if (mIssuranceModel?.citationData?.voilation?.mLateFineColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mLateFineColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    /**
     * Due Date 10/15
     */
    fun getDueDate(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()

        Vehdata.offNameFirst =
            mIssuranceModel?.citationData?.voilation?.dueDateLabel + " " + mIssuranceModel?.citationData?.voilation?.mLateFineDays?.let {
                after21DateFromCurrentDate(it)
            } + ""

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)
        ) {
            val d =
                mIssuranceModel?.citationData?.voilation?.dueDate.nullSafety().toDouble()
            val s = String.format("%.2f", d)
            Vehdata.offTypeFirst = "$ $s"
        } else {
            val amount =
                mIssuranceModel?.citationData?.voilation?.dueDate.nullSafety()
                    .split(".").toTypedArray()[0]
            Vehdata.offTypeFirst = "$ $amount.00"
        }

        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)
        ) {
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.dueDateLabel + " " + mIssuranceModel?.citationData?.voilation?.dueDateDay?.let {
                    after21DateFromCurrentDate(it)
                } + " "

            /**
             * We have to change the type = 1 here when the site is carta
             * So that it will align with if paid after type = 2 value
             * Hence it will be in the same row
             */
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)) {
                Vehdata.type = 1
            } else {
                Vehdata.type = 2
            }
        } else if (mIssuranceModel?.citationData?.voilation?.dueDateDay!! > 0) {
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.dueDateLabel + " " + mIssuranceModel?.citationData?.voilation?.dueDateDay?.let {
                    after21DateFromCurrentDate(it)
                } + " "
        }

        //Overriding label name with text only, no need of date for Satellite Beach SIte
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SATELLITE,
                true
            ) || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true)
        ) {
            Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.dueDateLabel
        }

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true)) {
            Vehdata.mHorizontalColon = 3
        } else {
            Vehdata.mHorizontalColon = 2
        }

        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderDueDate.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mDueDateX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mDueDateY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mDueDateFont!!

        /**
         * If the site is Carta & late fine day is equals to 0 : We hide the if Pay after label
         * So reason being, We are changing the order to align this value to replace this older one
         * Now this due x day label will move to left
         */
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CARTA,
                true
            ) && mIssuranceModel?.citationData?.voilation?.mLateFineDays.nullSafety() == 0
        ) {
            Vehdata.mPrintOrder = 3.0
            Vehdata.type = 2
        }
        if (mIssuranceModel?.citationData?.voilation?.mDueDateColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mDueDateColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    /**
     * Due Date 30
     */
    fun getDueDate30(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.dueDate30Label
        if (mIssuranceModel?.citationData?.voilation?.dueDate30Days!! > 0) {
            Vehdata.offNameFirst =
                mIssuranceModel?.citationData?.voilation?.dueDate30Label + " " + mIssuranceModel?.citationData?.voilation?.dueDate30Days?.let {
                    after21DateFromCurrentDate(it)
                } + " "
        }

        if (
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true)
        ) {
            Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.dueDate30Label
        }

        val d =
            mIssuranceModel?.citationData?.voilation?.dueDate30.nullSafety().toDouble()
        if (d >= 0) {
            val s = String.format("%.2f", d)
            Vehdata.offTypeFirst = "$ $s"
        } else {
            Vehdata.offTypeFirst = " "
        }

        /**
         * Type will be 2 when site is Carta & When we need this to in new line
         */
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA)) {
            Vehdata.type = 2
        }

        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderDueDate30.nullSafety()

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true)) {
            Vehdata.mHorizontalColon = 3
        } else {
            Vehdata.mHorizontalColon = 2
        }

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mDueDate30X!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mDueDate30Y!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mDueDate30Font!!

        if (mIssuranceModel?.citationData?.voilation?.mDueDate30ColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mDueDate30ColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    /**
     * Due Date 45
     */
    fun getDueDate45(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst =
            mIssuranceModel?.citationData?.voilation?.dueDate45Label + " " + mIssuranceModel?.citationData?.voilation?.dueDate45Days?.let {
                after21DateFromCurrentDate(it)
            } + " "

        val amount =
            mIssuranceModel?.citationData?.voilation?.dueDate45.nullSafety()
                .split(".").toTypedArray()[0]

        Vehdata.offTypeFirst = "$ $amount.00"
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderDueDate45.nullSafety()

        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                ignoreCase = true
            )
        ) {
            Vehdata.mHorizontalColon = 3
        } else {
            Vehdata.mHorizontalColon = 2
        }

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mDueDate45X!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mDueDate45Y!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mDueDate45Font!!

        if (mIssuranceModel?.citationData?.voilation?.mDueDate45ColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mDueDate45ColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }

    fun getViolationVioType(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst =
            mIssuranceModel?.citationData?.voilation?.vioTypeLabel

        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.voilation?.vioType

        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderVioType.nullSafety()
        Vehdata.mHorizontalColon = 2
        Vehdata.type = 1

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mVioTypeX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mVioTypeY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mVioTypeFont!!
        Vehdata.mNoBox = 1 // key box

        if (mIssuranceModel?.citationData?.voilation?.mVioTypeColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mVioTypeColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    fun getViolationVioTypeCode(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst =
            mIssuranceModel?.citationData?.voilation?.vioTypeCodeLabel

        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.voilation?.vioTypeCode

        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderVioTypeCode.nullSafety()
        Vehdata.mHorizontalColon = 2
        Vehdata.type = 1

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mVioTypeCodeX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mVioTypeCodeY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mVioTypeCodeFont!!
        Vehdata.mNoBox = 1 // key box

        if (mIssuranceModel?.citationData?.voilation?.mVioTypeCodeColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mVioTypeCodeColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }
    fun getViolationVioTypeDescription(
        context: Context,
        mIssuranceModel: CitationInsurranceDatabaseModel?
    ): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst =
            mIssuranceModel?.citationData?.voilation?.vioTypeDescriptionLabel

        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.voilation?.vioTypeDescription

        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderVioTypeDescription.nullSafety()
        Vehdata.mHorizontalColon = 2
        Vehdata.type = 1

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mVioTypeDescriptionX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mVioTypeDescriptionY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mVioTypeDescriptionFont!!
        Vehdata.mNoBox = 1 // key box

        if (mIssuranceModel?.citationData?.voilation?.mVioTypeDescriptionColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mVioTypeDescriptionColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    fun getDueDateCost(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.dueDateCostLabel

        val d =
            mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety().toDouble()
        if (d >= 0) {
            val s = String.format("%.2f", d)
            Vehdata.offTypeFirst = "$ $s"
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderDueDateCost.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mDueDateCostX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mDueDateCostY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mDueDateCostFont!!
        if (mIssuranceModel?.citationData?.voilation?.mDueDateCostColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mDueDateCostColumnSize.nullSafety(1)
                    .toInt()
        }
        return Vehdata
    }

    fun getDueDateTotal(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.dueDateTotalLabel

        val d =
            mIssuranceModel?.citationData?.voilation?.dueDateTotal.nullSafety().toDouble()
        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mDueDateTotalX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mDueDateTotalY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mDueDateTotalFont!!
        if (d > 0) {
            val s = String.format("%.2f", d)
            Vehdata.offTypeFirst = "$ $s"
        }
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderDueDateTotal.nullSafety()

        Vehdata.mFontSize = Constants.PRINT_TEXT_LARGE
        Vehdata.type = 2
        if (mIssuranceModel?.citationData?.voilation?.mDueDateTotalColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mDueDateTotalColumnSize.nullSafety(1)
                    .toInt()
        }
        return Vehdata
    }

    fun getPayAtOnline(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.payAtOnlineLabel

        Vehdata.offTypeFirst = mIssuranceModel?.citationData?.voilation?.payAtOnline.nullSafety()

        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderPayAtOnline.nullSafety()

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mPayOnlineX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mPayOnlineY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mPayOnlineFont!!

        Vehdata.mFontSize = Constants.PRINT_TEXT_LARGE
        Vehdata.type = 2
        if (mIssuranceModel?.citationData?.voilation?.mPayOnlineColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mPayOnlineColumnSize.nullSafety(1).toInt()
        }

        return Vehdata
    }

    fun getLocationDesc(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val Vehdata = VehicleListModel()
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                DuncanBrandingApp13()
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true
            )

            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PARK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BURLINGTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CFFB, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAWRENCE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true
            )

            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CARTA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOL, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true
            )
        ) {
            Vehdata.offNameFirst = ""
        } else {
            Vehdata.offNameFirst = mIssuranceModel?.citationData?.voilation?.locationDescrLabel
        }
        Vehdata.offTypeFirst =
            mIssuranceModel?.citationData?.voilation?.locationDescr
        Vehdata.mPrintOrder =
            mIssuranceModel?.citationData?.voilation?.mPrintOrderLocationDescr.nullSafety()
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true
            )
            || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true
            )
        ) {
            Vehdata.type = 2
        } else if (
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PARKX, ignoreCase = true
            )
        ) {
            Vehdata.type = 1
        } else {
            Vehdata.type = 3
        }

        Vehdata.mHorizontalColon = 1

        Vehdata.mAxisX = mIssuranceModel?.citationData?.voilation?.mLocationDescrX!!
        Vehdata.mAxisY = mIssuranceModel?.citationData?.voilation?.mLocationDescrY!!
        Vehdata.mFontSizeInt = mIssuranceModel?.citationData?.voilation?.mDescrFont!!
        Vehdata.mNoBox = 1

        if (mIssuranceModel?.citationData?.voilation?.mDescrColumnSize.nullSafety(0) >= 1) {
            Vehdata.type =
                mIssuranceModel?.citationData?.voilation?.mDescrColumnSize.nullSafety(1).toInt()
        }
        return Vehdata
    }
    //End of Violation Info

    //Start of Motorist Information
    fun getMotoristFirstName(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristFirstNameModel = VehicleListModel()

        motoristFirstNameModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstNameLabel
        motoristFirstNameModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstName

        motoristFirstNameModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristFirstName.nullSafety()
        motoristFirstNameModel.type = 1
        motoristFirstNameModel.mHorizontalColon = 1
        motoristFirstNameModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstNameX.nullSafety()
        motoristFirstNameModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstNameY.nullSafety()
        motoristFirstNameModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstNameFont.nullSafety()
        motoristFirstNameModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristFirstNameModel
    }

    fun getMotoristMiddleName(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristMiddleNameModel = VehicleListModel()

        motoristMiddleNameModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleNameLabel
        motoristMiddleNameModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleName

        motoristMiddleNameModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristMiddleName.nullSafety()
        motoristMiddleNameModel.type = 1
        motoristMiddleNameModel.mHorizontalColon = 1
        motoristMiddleNameModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleNameX.nullSafety()
        motoristMiddleNameModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleNameY.nullSafety()
        motoristMiddleNameModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleNameFont.nullSafety()
        motoristMiddleNameModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristMiddleNameModel
    }


    fun getMotoristLastName(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristLastNameModel = VehicleListModel()

        motoristLastNameModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristLastNameLabel
        motoristLastNameModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristLastName

        motoristLastNameModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristLastName.nullSafety()
        motoristLastNameModel.type = 1
        motoristLastNameModel.mHorizontalColon = 1
        motoristLastNameModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristLastNameX.nullSafety()
        motoristLastNameModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristLastNameY.nullSafety()
        motoristLastNameModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristLastNameFont.nullSafety()
        motoristLastNameModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristLastNameModel
    }

    fun getMotoristDateOfBirth(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristDateOfBirthModel = VehicleListModel()

        motoristDateOfBirthModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirthLabel
        motoristDateOfBirthModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirth

        motoristDateOfBirthModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristDateOfBirth.nullSafety()
        motoristDateOfBirthModel.type = 1
        motoristDateOfBirthModel.mHorizontalColon = 1
        motoristDateOfBirthModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirthX.nullSafety()
        motoristDateOfBirthModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirthY.nullSafety()
        motoristDateOfBirthModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirthFont.nullSafety()
        motoristDateOfBirthModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristDateOfBirthModel
    }

    fun getMotoristDlNumber(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristDlNumberModel = VehicleListModel()

        motoristDlNumberModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumberLabel
        motoristDlNumberModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumber

        motoristDlNumberModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristDlNumber.nullSafety()
        motoristDlNumberModel.type = 1
        motoristDlNumberModel.mHorizontalColon = 1
        motoristDlNumberModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumberX.nullSafety()
        motoristDlNumberModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumberY.nullSafety()
        motoristDlNumberModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumberFont.nullSafety()
        motoristDlNumberModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristDlNumberModel
    }

    fun getMotoristAddressBlock(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristAddressBlockModel = VehicleListModel()

        motoristAddressBlockModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlockLabel
        motoristAddressBlockModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlock

        motoristAddressBlockModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristAddressBlock.nullSafety()
        motoristAddressBlockModel.type = 1
        motoristAddressBlockModel.mHorizontalColon = 1
        motoristAddressBlockModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlockX.nullSafety()
        motoristAddressBlockModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlockY.nullSafety()
        motoristAddressBlockModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlockFont.nullSafety()
        motoristAddressBlockModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristAddressBlockModel
    }

    fun getMotoristAddressStreet(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristAddressStreetModel = VehicleListModel()

        motoristAddressStreetModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreetLabel
        motoristAddressStreetModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreet

        motoristAddressStreetModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristAddressStreet.nullSafety()
        motoristAddressStreetModel.type = 1
        motoristAddressStreetModel.mHorizontalColon = 1
        motoristAddressStreetModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreetX.nullSafety()
        motoristAddressStreetModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreetY.nullSafety()
        motoristAddressStreetModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreetFont.nullSafety()
        motoristAddressStreetModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristAddressStreetModel
    }

    fun getMotoristAddressCity(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristAddressCityModel = VehicleListModel()

        motoristAddressCityModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCityLabel
        motoristAddressCityModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCity

        motoristAddressCityModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristAddressCity.nullSafety()
        motoristAddressCityModel.type = 1
        motoristAddressCityModel.mHorizontalColon = 1
        motoristAddressCityModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCityX.nullSafety()
        motoristAddressCityModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCityY.nullSafety()
        motoristAddressCityModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCityFont.nullSafety()
        motoristAddressCityModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristAddressCityModel
    }


    fun getMotoristAddressState(mIssuranceModel: CitationInsurranceDatabaseModel?): VehicleListModel {
        val motoristAddressStateModel = VehicleListModel()

        motoristAddressStateModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStateLabel
        motoristAddressStateModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressState

        motoristAddressStateModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristAddressState.nullSafety()
        motoristAddressStateModel.type = 1
        motoristAddressStateModel.mHorizontalColon = 1
        motoristAddressStateModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStateX.nullSafety()
        motoristAddressStateModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStateY.nullSafety()
        motoristAddressStateModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStateFont.nullSafety()
        motoristAddressStateModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristAddressStateModel
    }

    fun getMotoristAddressZip(
        mIssuranceModel: CitationInsurranceDatabaseModel?,
        isMunicipalCitation: Boolean? = false
    ): VehicleListModel {
        val motoristAddressZipModel = VehicleListModel()

        motoristAddressZipModel.offNameFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZipLabel
        motoristAddressZipModel.offTypeFirst =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZip

        motoristAddressZipModel.mPrintOrder =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.mPrintOrderMotoristAddressZip.nullSafety()
        motoristAddressZipModel.type = 1
        motoristAddressZipModel.mHorizontalColon = 1
        motoristAddressZipModel.mAxisX =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZipX.nullSafety()
        motoristAddressZipModel.mAxisY =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZipY.nullSafety()
        motoristAddressZipModel.mFontSizeInt =
            mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZipFont.nullSafety()
        motoristAddressZipModel.mFontSize = Constants.PRINT_TEXT_LARGE

        return motoristAddressZipModel
    }
    //End of Motorist Information
}