package com.parkloyalty.lpr.scan.views.fragments.search

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.common.model.InactiveMeterBuzzerViewModel
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.databinding.FragmentScanResultsScreenBinding
import com.parkloyalty.lpr.scan.databinding.FragmentSearchScreenBinding
import com.parkloyalty.lpr.scan.databinding.FragmentSettingsScreenBinding
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.LastSecondCheckViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.HistoryAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.DataFromLprViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.PaymentDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimigMarkViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingMarkData
import com.parkloyalty.lpr.scan.ui.check_setup.model.TimingMarkResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.CitationDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.CitationResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ExemptDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ExemptResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.MakeModelColorData
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.MakeModelColorResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.PaymentResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.PermitDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.PermitResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.StolenDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.StolenResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.vehiclestickerscan.model.VehicleInfoModel
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import com.parkloyalty.lpr.scan.views.fragments.checksetup.CheckSetupScreenViewModel
import com.parkloyalty.lpr.scan.views.fragments.citationform.CitationFormScreenViewModel
import com.parkloyalty.lpr.scan.views.fragments.scanresult.ScanResultScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.ArrayList
import java.util.HashSet

@AndroidEntryPoint
class SearchScreenFragment : BaseFragment<FragmentSearchScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    //Can be used later for settings specific logic
    private val searchScreenViewModel: SearchScreenViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSearchScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {

    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initViewLifecycleScope() {
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
        viewLifecycleOwner.lifecycleScope.launch {

        }
    }

    override fun setupClickListeners() {
        //Nothing to implement
    }
}