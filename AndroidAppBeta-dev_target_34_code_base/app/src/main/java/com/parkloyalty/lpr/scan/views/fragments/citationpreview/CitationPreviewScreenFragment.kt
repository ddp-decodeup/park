package com.parkloyalty.lpr.scan.views.fragments.citationpreview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.parkloyalty.lpr.scan.databinding.FragmentSettingsScreenBinding
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import com.parkloyalty.lpr.scan.views.fragments.abandonedvehicle.AbandonedVehicleScreenViewModel
import com.parkloyalty.lpr.scan.views.fragments.checksetup.CheckSetupScreenViewModel
import com.parkloyalty.lpr.scan.views.fragments.citationform.CitationFormScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CitationPreviewScreenFragment : BaseFragment<FragmentSettingsScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    //Can be used later for settings specific logic
    private val citationPreviewScreenViewModel: CitationPreviewScreenViewModel by viewModels()



    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsScreenBinding.inflate(inflater, container, false)

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