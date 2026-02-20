package com.parkloyalty.lpr.scan.network.api

import com.parkloyalty.lpr.scan.extensions.addConditionalOfficerNameInCreateTicket
import com.parkloyalty.lpr.scan.extensions.addImpoundCodeInCreateTicket
import com.parkloyalty.lpr.scan.extensions.addPeoDetailsInCreateTicket
import com.parkloyalty.lpr.scan.extensions.addTicketTypeValueWarningInCreateTicket
import com.parkloyalty.lpr.scan.extensions.addUnpaidCitationBasedInvoiceFeeStructureInCreateTicket
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationCommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationHeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationInvoiceFeeStructure
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLocationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationOfficerDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationVehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationViolationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.HeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.InvoiceFeeStructure
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.LocationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.MotoristDetailsModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.OfficerDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.ViolationDetails
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.utils.ApiConstants.TICKET_STATUS_VALID
import com.parkloyalty.lpr.scan.utils.ApiConstants.TICKET_TYPE_WARNING


object RequestHandler {
    fun getCreateCitationTicketRequest(
        welcomeForm: WelcomeForm?,
        insuranceModel: CitationInsurranceDatabaseModel?,
        mImages: List<String>,
        isReissue: Boolean,
        isTimeLimitEnforcement: Boolean? = null,
        timeLimitEnforcementObservedTime: String,
        lat: Double? = null,
        lng: Double? = null,
        printQuery: String? = null
    ): CreateTicketRequest {
        val ticketTypeValue = StringBuilder()

        // Helper to append ticket types
        fun appendTicketType(type: String?) {
            val value = type.nullSafety()
            if (value.isNotEmpty()) {
                if (ticketTypeValue.isNotEmpty()) ticketTypeValue.append(", ")
                ticketTypeValue.append(value)
            }
        }

        val createTicketRequest = CreateTicketRequest()
        // Location Details
        createTicketRequest.locationDetails = LocationDetails().apply {
            street = insuranceModel?.citationData?.location?.street.nullSafety()
            street_lookup_code = insuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
            block = insuranceModel?.citationData?.location?.block.nullSafety()
            meter = insuranceModel?.citationData?.location?.meterName.nullSafety()
            if (addImpoundCodeInCreateTicket()) {
                mImpoundCode = insuranceModel?.citationData?.location?.side.nullSafety()
            } else {
                side = insuranceModel?.citationData?.location?.side.nullSafety()
                direction = insuranceModel?.citationData?.location?.direction.nullSafety()
            }
            lot = insuranceModel?.citationData?.location?.lot.nullSafety()
            lotBranchId = insuranceModel?.citationData?.location?.lotBranchId.nullSafety()
            lotLookupCode = insuranceModel?.citationData?.location?.lotLookupCode.nullSafety()
            mSpaceId = insuranceModel?.citationData?.location?.spaceName.nullSafety().trim()
        }

        // Vehicle Details
        createTicketRequest.vehicleDetails = VehicleDetails().apply {
            body_style = insuranceModel?.citationData?.vehicle?.bodyStyle.nullSafety()
            body_style_lookup_code = insuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
            decal_year = insuranceModel?.citationData?.vehicle?.decalYear.nullSafety()
            decal_number = insuranceModel?.citationData?.vehicle?.decalNumber.nullSafety()
            vin_number = insuranceModel?.citationData?.vehicle?.vinNumber.nullSafety()
            make = insuranceModel?.citationData?.vehicle?.make.nullSafety()
            color = insuranceModel?.citationData?.vehicle?.color.nullSafety()
            model = insuranceModel?.citationData?.vehicle?.model.nullSafety()
            model_lookup_code = insuranceModel?.citationData?.vehicle?.model_lookup_code.nullSafety()
            lprNo = insuranceModel?.citationData?.vehicle?.licensePlate.nullSafety()
            state = insuranceModel?.citationData?.vehicle?.state.nullSafety()
            mLicenseExpiry = insuranceModel?.citationData?.vehicle?.expiration.nullSafety()
        }

        // Violation Details
        createTicketRequest.violationDetails = ViolationDetails().apply {
            code = insuranceModel?.citationData?.voilation?.code.nullSafety()
            violation = insuranceModel?.citationData?.voilation?.violationCode.nullSafety()
            description = insuranceModel?.citationData?.voilation?.locationDescr.nullSafety()
            try {
                fine = insuranceModel?.citationData?.voilation?.amount.nullSafety("0").toDouble()
                late_fine = insuranceModel?.citationData?.voilation?.amountDueDate.nullSafety("0").toDouble()
                due_15_days = insuranceModel?.citationData?.voilation?.dueDate.nullSafety("0").toDouble()
                due_30_days = insuranceModel?.citationData?.voilation?.dueDate30.nullSafety("0").toDouble()
                due_45_days = insuranceModel?.citationData?.voilation?.dueDate45.nullSafety("0").toDouble()
                mVioType = insuranceModel?.citationData?.voilation?.vioType.nullSafety()
                mVioTypeCode = insuranceModel?.citationData?.voilation?.vioTypeCode.nullSafety()
                mVioTypeDescription = insuranceModel?.citationData?.voilation?.vioTypeDescription.nullSafety()
                export_code = insuranceModel?.citationData?.voilation?.export_code.nullSafety()
                mCost = insuranceModel?.citationData?.voilation?.dueDateCost?.takeIf { it != "null" }?.nullSafety("0")?.toDouble() ?: 0.0
                mSanctionsType = insuranceModel?.citationData?.voilation?.mSanctionsType?.let {
                    when (it) {
                        1 -> "White Sticker"
                        2 -> "Red Sticker"
                        else -> ""
                    }
                } ?: ""
                try {
                    if (addUnpaidCitationBasedInvoiceFeeStructureInCreateTicket()) {
//                        val unpaidCount = insuranceModel?.citationData?.voilation?.mUnpaidCitationCount.nullSafety(0)
//                        val citationCount = if (unpaidCount <= 0) 1 else (unpaidCount + 1).coerceAtMost(3)
                        val basicRate = insuranceModel?.citationData?.voilation?.amount?.takeIf { it != "null" }?.nullSafety("0")?.toDouble() ?: 0.0
                        // Safely parse dueDateCost string to Double, fallback to 7.0 if invalid or null
                        val percent = insuranceModel?.citationData?.voilation?.dueDateCost
                            ?.takeIf { !it.isNullOrBlank() && it != "null" }
                            ?.toDoubleOrNull() ?: 7.0
//                        val baseRateOfCitationCount = basicRate * citationCount
                        val totalRate7Percent = (basicRate * percent) / 100
                        val transactionFee = insuranceModel?.citationData?.voilation?.dueDateCitationFee?.takeIf { it != "null" }?.nullSafety("0")?.toDouble()?.takeIf { it != 0.0 } ?: 0.0
                        createTicketRequest.invoiceFeeStructure = InvoiceFeeStructure().apply {
                            mSaleTax = totalRate7Percent
                            mCitationFee = transactionFee
                            mParkingFee = basicRate
                        }
                    } else {
                        createTicketRequest.invoiceFeeStructure = InvoiceFeeStructure().apply {
                            mSaleTax = insuranceModel?.citationData?.voilation?.dueDateCost?.takeIf { it != "null" }?.nullSafety("0")?.toDouble() ?: 0.0
                            mCitationFee = insuranceModel?.citationData?.voilation?.dueDateCitationFee?.takeIf { it != "null" }?.nullSafety("0")?.toDouble() ?: 0.0
                            mParkingFee = insuranceModel?.citationData?.voilation?.dueDateParkingFee?.takeIf { it != "null" }?.nullSafety("0")?.toDouble() ?: 0.0
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Officer Details
        createTicketRequest.officerDetails = OfficerDetails().apply {
            badgeId = insuranceModel?.citationData?.officer?.badgeId.nullSafety()
            officer_lookup_code = insuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
            officer_name = if (addConditionalOfficerNameInCreateTicket()) {
                Util.officerNameForBurbank(insuranceModel?.citationData?.officer?.officerDetails.nullSafety())
            } else {
                AppUtils.getOfficerName(insuranceModel?.citationData?.officer?.officerDetails.toString().trim().nullSafety())
            }
            if (addPeoDetailsInCreateTicket()) {
                peo_fname = AppUtils.getPOEName(insuranceModel?.citationData?.officer?.officerDetails.toString().trim().nullSafety(), 0)
                peo_lname = AppUtils.getPOEName(insuranceModel?.citationData?.officer?.officerDetails.toString().trim().nullSafety(), 1)
                peo_name = "$peo_fname, $peo_lname"
            }
            zone = insuranceModel?.citationData?.location?.pcbZone?.nullSafety()
                ?: insuranceModel?.citationData?.officer?.zone?.nullSafety() ?: ""
            signature = ""
            squad = insuranceModel?.citationData?.officer?.squad.nullSafety()
            beat = insuranceModel?.citationData?.officer?.beat.nullSafety()
            agency = insuranceModel?.citationData?.officer?.agency?.nullSafety().takeIf { !it.isNullOrEmpty() } ?: welcomeForm?.agency
            mShift = insuranceModel?.citationData?.officer?.shift.nullSafety()
            mDdeviceId = welcomeForm?.officerDeviceName.nullSafety()
            mDdeviceFriendlyName = welcomeForm?.officerDeviceName.nullSafety()
        }

        // Comment Details
        createTicketRequest.commentsDetails = CommentsDetails().apply {
            note1 = insuranceModel?.citationData?.locationNotes.nullSafety()
            note2 = insuranceModel?.citationData?.locationNotes1.nullSafety()
            note3 = insuranceModel?.citationData?.locationNotes2.nullSafety()
            remark1 = insuranceModel?.citationData?.locationRemarks.nullSafety()
            remark2 = insuranceModel?.citationData?.locationRemarks1.nullSafety() + " " + insuranceModel?.citationData?.locationRemarks2.nullSafety()
        }

        // Header Details
        createTicketRequest.headerDetails = HeaderDetails().apply {
            citationNumber = insuranceModel?.citationData?.ticketNumber.nullSafety()
            timestamp = insuranceModel?.citationData?.ticketDate.nullSafety()
        }

        // Extras
        createTicketRequest.lprNumber = insuranceModel?.citationData?.vehicle?.licensePlate.nullSafety()
        createTicketRequest.code = insuranceModel?.citationData?.voilation?.code.nullSafety()
        createTicketRequest.hearingDate = insuranceModel?.citationData?.hearingDate
        createTicketRequest.ticketNo = insuranceModel?.citationData?.ticketNumber.nullSafety()

        // Ticket Type Values
        if (insuranceModel?.citationData?.ticketType.nullSafety().isNotEmpty()) {
            ticketTypeValue.append(
                if (addTicketTypeValueWarningInCreateTicket()) TICKET_TYPE_WARNING
                else insuranceModel?.citationData?.ticketType.nullSafety()
            )
        }
        appendTicketType(insuranceModel?.citationData?.ticketType2)
        appendTicketType(insuranceModel?.citationData?.ticketType3)
        createTicketRequest.type = ticketTypeValue.toString()
        createTicketRequest.timeLimitEnforcementObservedTime = timeLimitEnforcementObservedTime
        createTicketRequest.imageUrls = mImages
        createTicketRequest.notes = insuranceModel?.citationData?.locationNotes.nullSafety()
        createTicketRequest.status = TICKET_STATUS_VALID
        createTicketRequest.citationStartTimestamp = insuranceModel?.citationData?.startTime.nullSafety()
        createTicketRequest.citationIssueTimestamp = insuranceModel?.citationData?.issueTime.nullSafety()
        createTicketRequest.isReissue = isReissue
        if (isTimeLimitEnforcement != null) {
            createTicketRequest.isTimeLimitEnforcement = isTimeLimitEnforcement
        }
        createTicketRequest.timeLimitEnforcementId = insuranceModel?.citationData?.timingId.nullSafety()
        if (lat != null) createTicketRequest.mLatitude = lat
        if (lng != null) createTicketRequest.mLongitiude = lng
        if (!printQuery.isNullOrEmpty()) createTicketRequest.printQuery = printQuery
        return createTicketRequest
    }

    fun getCreateMunicipalCitationTicketRequest(
        welcomeForm: WelcomeForm?,
        insuranceModel: CitationInsurranceDatabaseModel?,
        mImages: List<String>,
        isReissue: Boolean,
        isTimeLimitEnforcement: Boolean? = null,
        timeLimitEnforcementObservedTime: String,
        lat: Double? = null,
        lng: Double? = null,
        ticketCategory: String,
        printQuery: String? = null
    ): CreateMunicipalCitationTicketRequest {
        val ticketTypeValue = StringBuilder()

        // Helper to append ticket types
        fun appendTicketType(type: String?) {
            val value = type.nullSafety()
            if (value.isNotEmpty()) {
                if (ticketTypeValue.isNotEmpty()) ticketTypeValue.append(", ")
                ticketTypeValue.append(value)
            }
        }

        val createTicketRequest = CreateMunicipalCitationTicketRequest()
        // Location Details
        createTicketRequest.locationDetails = MunicipalCitationLocationDetails().apply {
            street = insuranceModel?.citationData?.location?.street.nullSafety()
            street_lookup_code =
                insuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
            block = insuranceModel?.citationData?.location?.block.nullSafety()
            meter = insuranceModel?.citationData?.location?.meterName.nullSafety()
            if (addImpoundCodeInCreateTicket()) {
                mImpoundCode = insuranceModel?.citationData?.location?.side.nullSafety()
            } else {
                side = insuranceModel?.citationData?.location?.side.nullSafety()
                direction = insuranceModel?.citationData?.location?.direction.nullSafety()
            }
            lot = insuranceModel?.citationData?.location?.lot.nullSafety()
            mSpaceId = insuranceModel?.citationData?.location?.spaceName.nullSafety().trim()
        }

        // Vehicle Details
        createTicketRequest.vehicleDetails = MunicipalCitationVehicleDetails().apply {
            body_style = insuranceModel?.citationData?.vehicle?.bodyStyle.nullSafety()
            body_style_lookup_code =
                insuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
            decal_year = insuranceModel?.citationData?.vehicle?.decalYear.nullSafety()
            decal_number = insuranceModel?.citationData?.vehicle?.decalNumber.nullSafety()
            vin_number = insuranceModel?.citationData?.vehicle?.vinNumber.nullSafety()
            make = insuranceModel?.citationData?.vehicle?.make.nullSafety()
            color = insuranceModel?.citationData?.vehicle?.color.nullSafety()
            model = insuranceModel?.citationData?.vehicle?.model.nullSafety()
            model_lookup_code =
                insuranceModel?.citationData?.vehicle?.model_lookup_code.nullSafety()
            lprNo = insuranceModel?.citationData?.vehicle?.licensePlate.nullSafety()
            state = insuranceModel?.citationData?.vehicle?.state.nullSafety()
            mLicenseExpiry = insuranceModel?.citationData?.vehicle?.expiration.nullSafety()
        }

        // Violation Details
        createTicketRequest.violationDetails = MunicipalCitationViolationDetails().apply {
            code = insuranceModel?.citationData?.voilation?.code.nullSafety()
            violation = insuranceModel?.citationData?.voilation?.violationCode.nullSafety()
            description = insuranceModel?.citationData?.voilation?.locationDescr.nullSafety()
            try {
                fine = insuranceModel?.citationData?.voilation?.amount.nullSafety("0").toDouble()
                late_fine = insuranceModel?.citationData?.voilation?.amountDueDate.nullSafety("0")
                    .toDouble()
                due_15_days =
                    insuranceModel?.citationData?.voilation?.dueDate.nullSafety("0").toDouble()
                due_30_days =
                    insuranceModel?.citationData?.voilation?.dueDate30.nullSafety("0").toDouble()
                due_45_days =
                    insuranceModel?.citationData?.voilation?.dueDate45.nullSafety("0").toDouble()
                export_code = insuranceModel?.citationData?.voilation?.export_code.nullSafety()
                mCost =
                    insuranceModel?.citationData?.voilation?.dueDateCost?.takeIf { it != "null" }
                        ?.nullSafety("0")?.toDouble() ?: 0.0

                try {
                    createTicketRequest.invoiceFeeStructure =
                        MunicipalCitationInvoiceFeeStructure().apply {
                            mSaleTax =
                                insuranceModel?.citationData?.voilation?.dueDateCost?.takeIf { it != "null" }
                                    ?.nullSafety("0")?.toDouble() ?: 0.0
                            mCitationFee =
                                insuranceModel?.citationData?.voilation?.dueDateCitationFee?.takeIf { it != "null" }
                                    ?.nullSafety("0")?.toDouble() ?: 0.0
                            mParkingFee =
                                insuranceModel?.citationData?.voilation?.dueDateParkingFee?.takeIf { it != "null" }
                                    ?.nullSafety("0")?.toDouble() ?: 0.0
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //Motorist Details
        createTicketRequest.motoristDetails = MotoristDetailsModel().apply {
            insuranceModel?.citationData?.municipalCitationMotoristDetailsModel?.let { motorist ->
                motoristFirstName = motorist.motoristFirstName.nullSafety().uppercase()
                motoristMiddleName = motorist.motoristMiddleName.nullSafety().uppercase()
                motoristLastName = motorist.motoristLastName.nullSafety().uppercase()
                motoristDateOfBirth = motorist.motoristDateOfBirth.nullSafety().uppercase()
                motoristDlNumber = motorist.motoristDlNumber.nullSafety().uppercase()
                motoristAddressBlock = motorist.motoristAddressBlock.nullSafety().uppercase()
                motoristAddressStreet = motorist.motoristAddressStreet.nullSafety().uppercase()
                motoristAddressCity = motorist.motoristAddressCity.nullSafety().uppercase()
                motoristAddressState = motorist.motoristAddressState.nullSafety().uppercase()
                motoristAddressZip = motorist.motoristAddressZip.nullSafety().uppercase()
            }
        }


        // Officer Details
        createTicketRequest.officerDetails = MunicipalCitationOfficerDetails().apply {
            badgeId = insuranceModel?.citationData?.officer?.badgeId.nullSafety()
            officer_lookup_code =
                insuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
            officer_name = if (addConditionalOfficerNameInCreateTicket()) {
                Util.officerNameForBurbank(insuranceModel?.citationData?.officer?.officerDetails.nullSafety())
            } else {
                AppUtils.getOfficerName(
                    insuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                        .nullSafety()
                )
            }
            if (addPeoDetailsInCreateTicket()) {
                peo_fname = AppUtils.getPOEName(
                    insuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                        .nullSafety(), 0
                )
                peo_lname = AppUtils.getPOEName(
                    insuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                        .nullSafety(), 1
                )
                peo_name = "$peo_fname, $peo_lname"
            }
            zone = insuranceModel?.citationData?.location?.pcbZone?.nullSafety()
                ?: insuranceModel?.citationData?.officer?.zone?.nullSafety() ?: ""
            signature = ""
            squad = insuranceModel?.citationData?.officer?.squad.nullSafety()
            beat = insuranceModel?.citationData?.officer?.beat.nullSafety()
            agency = insuranceModel?.citationData?.officer?.agency?.nullSafety()
                .takeIf { !it.isNullOrEmpty() } ?: welcomeForm?.agency
            mShift = insuranceModel?.citationData?.officer?.shift.nullSafety()
            mDdeviceId = welcomeForm?.officerDeviceName.nullSafety()
            mDdeviceFriendlyName = welcomeForm?.officerDeviceName.nullSafety()
        }

        // Comment Details
        createTicketRequest.commentsDetails = MunicipalCitationCommentsDetails().apply {
            note1 = insuranceModel?.citationData?.locationNotes.nullSafety()
            note2 = insuranceModel?.citationData?.locationNotes1.nullSafety()
            note3 = insuranceModel?.citationData?.locationNotes2.nullSafety()
            remark1 = insuranceModel?.citationData?.locationRemarks.nullSafety()
            remark2 =
                insuranceModel?.citationData?.locationRemarks1.nullSafety() + " " + insuranceModel?.citationData?.locationRemarks2.nullSafety()
        }

        // Header Details
        createTicketRequest.headerDetails = MunicipalCitationHeaderDetails().apply {
            citationNumber = insuranceModel?.citationData?.ticketNumber.nullSafety()
            timestamp = insuranceModel?.citationData?.ticketDate.nullSafety()
        }

        // Extras
        createTicketRequest.lprNumber =
            insuranceModel?.citationData?.vehicle?.licensePlate.nullSafety()
        createTicketRequest.code = insuranceModel?.citationData?.voilation?.code.nullSafety()
        createTicketRequest.hearingDate = insuranceModel?.citationData?.hearingDate
        createTicketRequest.ticketNo = insuranceModel?.citationData?.ticketNumber.nullSafety()

        // Ticket Type Values
        if (insuranceModel?.citationData?.ticketType.nullSafety().isNotEmpty()) {
            ticketTypeValue.append(
                if (addTicketTypeValueWarningInCreateTicket()) TICKET_TYPE_WARNING
                else insuranceModel?.citationData?.ticketType.nullSafety()
            )
        }
        appendTicketType(insuranceModel?.citationData?.ticketType2)
        appendTicketType(insuranceModel?.citationData?.ticketType3)
        createTicketRequest.type = ticketTypeValue.toString()
        createTicketRequest.timeLimitEnforcementObservedTime = timeLimitEnforcementObservedTime
        createTicketRequest.imageUrls = mImages
        createTicketRequest.notes = insuranceModel?.citationData?.locationNotes.nullSafety()
        createTicketRequest.status = TICKET_STATUS_VALID
        createTicketRequest.citationStartTimestamp =
            insuranceModel?.citationData?.startTime.nullSafety()
        createTicketRequest.citationIssueTimestamp =
            insuranceModel?.citationData?.issueTime.nullSafety()
        createTicketRequest.isReissue = isReissue
        if (isTimeLimitEnforcement != null) {
            createTicketRequest.isTimeLimitEnforcement = isTimeLimitEnforcement
        }
        createTicketRequest.timeLimitEnforcementId =
            insuranceModel?.citationData?.timingId.nullSafety()
        if (lat != null) createTicketRequest.mLatitude = lat
        if (lng != null) createTicketRequest.mLongitiude = lng
        if (!printQuery.isNullOrEmpty()) createTicketRequest.printQuery = printQuery

        createTicketRequest.category = ticketCategory


        return createTicketRequest
    }
}