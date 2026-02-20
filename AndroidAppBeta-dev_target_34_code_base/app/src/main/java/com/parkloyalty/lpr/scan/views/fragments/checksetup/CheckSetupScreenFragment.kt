package com.parkloyalty.lpr.scan.views.fragments.checksetup

import DialogUtil
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.FragmentCheckSetupScreenBinding
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.CheckSetData
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.CheckSetupResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.SDF_HHmm
import com.parkloyalty.lpr.scan.util.SDF_MMM
import com.parkloyalty.lpr.scan.util.SDF_hhmm_a
import com.parkloyalty.lpr.scan.util.setAsAccessibilityHeading
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CHECK_SETUP
import com.parkloyalty.lpr.scan.utils.NoInternetDialogListener
import com.parkloyalty.lpr.scan.utils.NoInternetDialogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class CheckSetupScreenFragment : BaseFragment<FragmentCheckSetupScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    //Can be used later for settings specific logic
    private val checkSetupScreenViewModel: CheckSetupScreenViewModel by viewModels()


    var mTextViewScofflawCount: AppCompatTextView? = null
    var mTextViewPaymentCount: AppCompatTextView? = null
    var mTextViewStolenCount: AppCompatTextView? = null
    var mTextViewTimingCount: AppCompatTextView? = null
    var mTextViewExtemptCount: AppCompatTextView? = null
    var mTextViewPermitCount: AppCompatTextView? = null
    var mTextViewScofflawDate: AppCompatTextView? = null
    var mTextViewPaymentDate: AppCompatTextView? = null
    var mTextViewStolenDate: AppCompatTextView? = null
    var mTextViewTimingDate: AppCompatTextView? = null
    var mTextViewExtemptDate: AppCompatTextView? = null
    var mTextViewPermitDate: AppCompatTextView? = null
    var mTextViewZoneListCount: AppCompatTextView? = null
    var mTextViewStreetListCount: AppCompatTextView? = null
    var mTextViewMeterListCount: AppCompatTextView? = null
    lateinit var tvCheckSetup: AppCompatTextView
    lateinit var llScofflaw: LinearLayoutCompat
    lateinit var llActivePayment: LinearLayoutCompat
    lateinit var llStolenVehicles: LinearLayoutCompat
    lateinit var llTimingRecords: LinearLayoutCompat
    lateinit var llExempt: LinearLayoutCompat
    lateinit var llPermit: LinearLayoutCompat
    private var mDate: String? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCheckSetupScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        mTextViewScofflawCount = binding.tvScofflawCount
        mTextViewPaymentCount = binding.tvPaymentCount
        mTextViewStolenCount = binding.tvStolenCount
        mTextViewTimingCount = binding.tvTimingCount
        mTextViewExtemptCount = binding.tvExtemptCount
        mTextViewPermitCount = binding.tvPermitCount
        mTextViewScofflawDate = binding.tvScofflawDate
        mTextViewPaymentDate = binding.tvPaymentDate
        mTextViewStolenDate = binding.tvStolenDate
        mTextViewTimingDate = binding.tvTimingDate
        mTextViewExtemptDate = binding.tvExtemptDate
        mTextViewPermitDate = binding.tvPermitDate
        mTextViewZoneListCount = binding.zonelistcount
        mTextViewStreetListCount = binding.streetlistcount
        mTextViewMeterListCount = binding.meterlistcount
        tvCheckSetup = binding.tvCheckSetup
        llScofflaw = binding.llScofflaw
        llActivePayment = binding.llActivePayment
        llStolenVehicles = binding.llStolenVehicles
        llTimingRecords = binding.llTimingRecords
        llExempt = binding.llExempt
        llPermit = binding.llPermit
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    checkSetupScreenViewModel.checkSetupResponse.collect(::consumeResponse)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainActivityViewModel.setToolbarVisibility(true)
                    mainActivityViewModel.setToolbarComponents(
                        showBackButton = true, showLogo = true, showHemMenu = false
                    )
                }
            }
        }
    }

    override fun initialiseData() {
        init()
        setListCount()
    }

    override fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            callCheckSetupApi()
        }
    }

    private fun init() {
        callCheckSetupApi()
        mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()
    }

    fun setAccessibilityForComponents() {
        setAsAccessibilityHeading(tvCheckSetup)

        llScofflaw.contentDescription =
            "${getString(R.string.scr_lbl_scofflaw)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewScofflawCount?.text} ${getString(R.string.pause_in_talkback_long)} ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewScofflawDate?.text}"

        llActivePayment.contentDescription =
            "${getString(R.string.scr_lbl_active_payment)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPaymentCount?.text} ${getString(R.string.pause_in_talkback_long)} ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPaymentDate?.text}"

        llStolenVehicles.contentDescription =
            "${getString(R.string.scr_lbl_stolen_vehicles)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewStolenCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewStolenDate?.text}"

        llTimingRecords.contentDescription =
            "${getString(R.string.scr_lbl_timing_records)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewTimingCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewTimingDate?.text}"

        llExempt.contentDescription =
            "${getString(R.string.scr_lbl_exempt)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewExtemptCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewExtemptDate?.text}"

        llPermit.contentDescription =
            "${getString(R.string.scr_btn_permit)} ${getString(R.string.scr_lbl_details)} ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPermitCount?.text} ${getString(R.string.pause_in_talkback_long)}  ${
                getString(R.string.scr_lbl_last_updated_at)
            } ${
                getString(
                    R.string.pause_in_talkback_long
                )
            } ${mTextViewPermitDate?.text}"

    }

    //API Response Consumer function for all APIs
    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(context = requireContext())
            }

            is NewApiResponse.Success -> {
                try {
                    when (newApiResponse.apiNameTag) {
                        API_TAG_NAME_CHECK_SETUP -> {

                            val responseModel = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                CheckSetupResponse::class.java
                            )

                            when (responseModel.status) {
                                true -> {
                                    checkSetupDetails(responseModel.data?.firstOrNull())
                                }

                                else -> {
                                    AlertDialogUtils.showDialog(
                                        context = requireContext(),
                                        title = getString(R.string.error_title_check_setup_api_response),
                                        message = responseModel.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                                        positiveButtonText = getString(R.string.button_text_ok)
                                    )
                                }
                            }
                        }
                    }
                } catch (e: JsonMappingException) {
                    requireContext().toast(message = getString(R.string.error_desc_please_login_again_to_use_the_application))
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is NewApiResponse.ApiError -> {
                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_api_error),
                    message = getString(
                        R.string.error_desc_api_error,
                        newApiResponse.code.toString(),
                        newApiResponse.getErrorMessage()
                            .nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            is NewApiResponse.NetworkError -> {


                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_network_error),
                    message = getString(
                        R.string.error_desc_network_error,
                        newApiResponse.exception.message.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            is NewApiResponse.UnknownError -> {
                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_unknown_error),
                    message = getString(
                        R.string.error_desc_unknown_error,
                        newApiResponse.throwable.localizedMessage.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    /* Call Api For Check setup */
    private fun callCheckSetupApi() {
        if (requireContext().isInternetAvailable()) {
            checkSetupScreenViewModel.callCheckSetupAPI()
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callCheckSetupApi()
                    }
                })
        }
    }

    private fun splitsFormat(Date: String?): String {
        val separated = Date!!.split("-").toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        val calendar = Calendar.getInstance()
        calendar[Calendar.MONTH] = MM.toInt()
        SDF_MMM.calendar = calendar
        val monthName = SDF_MMM.format(calendar.time)
        return MM + "-" + DD + "-" + YYYY.substring(2, 4)
    }

    private fun getHours(hours: String): String? {
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvert(`val`)
    }

    fun splitDate(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHours(`val`)
        } catch (e: Exception) {
            date
        }
    }

    private fun dateConvert(`val`: String): String? {
        try {
            val _24HourSDF = SDF_HHmm
            val _12HourSDF = SDF_hhmm_a
            val _24HourDt = _24HourSDF.parse(`val`)
            return splitsFormat(mDate) + " " + _12HourSDF.format(_24HourDt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun checkSetupDetails(mCheckSetupData: CheckSetData?) {
        try {
            mTextViewScofflawCount?.text = mCheckSetupData?.scofflawData?.totalRecords.toString()
            mTextViewScofflawDate?.text =
                splitDate(mCheckSetupData?.scofflawData?.lastModified.toString())
            mTextViewPaymentCount?.text = mCheckSetupData?.paymentData?.totalRecords.toString()
            mTextViewPaymentDate?.text =
                splitDate(mCheckSetupData?.paymentData?.lastModified.toString())
            mTextViewStolenCount?.text = mCheckSetupData?.stolenData?.totalRecords.toString()
            mTextViewStolenDate?.text =
                splitDate(mCheckSetupData?.stolenData?.lastModified.toString())
            mTextViewTimingCount?.text = mCheckSetupData?.timingData?.totalRecords.toString()
            mTextViewTimingDate?.text =
                splitDate(mCheckSetupData?.timingData?.lastModified.toString())
            mTextViewExtemptCount?.text = mCheckSetupData?.exemptData?.totalRecords.toString()
            mTextViewExtemptDate?.text =
                splitDate(mCheckSetupData?.exemptData?.lastModified.toString())
            mTextViewPermitCount?.text = mCheckSetupData?.permitData?.totalRecords.toString()
            mTextViewPermitDate?.text =
                splitDate(mCheckSetupData?.permitData?.lastModified.toString())


            setAccessibilityForComponents()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            val mStreetListCount = mainActivityViewModel.getStreetListFromDataSet()
            val mMeterListCount = mainActivityViewModel.getMeterListFromDataSet()
            val mWelcomeList = mainActivityViewModel.getWelcomeObject()

            mTextViewMeterListCount?.post {
                mTextViewMeterListCount?.text = mMeterListCount?.size.nullSafety().toString()

            }

            mTextViewStreetListCount?.post {
                mTextViewStreetListCount?.text = mStreetListCount?.size.nullSafety().toString()

            }

            mTextViewZoneListCount?.post {
                mTextViewZoneListCount?.text =
                    mWelcomeList?.welcomeList?.zoneStats?.size?.nullSafety().toString()
            }
        }
    }
}