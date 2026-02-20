package com.parkloyalty.lpr.scan.views.fragments.settings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.parkloyalty.lpr.scan.databinding.FragmentSettingsScreenBinding
import com.parkloyalty.lpr.scan.extensions.addGrayDivider
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsScreenFragment : BaseFragment<FragmentSettingsScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    //Can be used later for settings specific logic
    private val settingsScreenViewModel: SettingsScreenViewModel by viewModels()

    private lateinit var settingsListAdapter: SettingsListAdapter


    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        // Initialize adapter and RecyclerView
        settingsListAdapter = SettingsListAdapter()

        binding.rvSettings.addGrayDivider()
        binding.rvSettings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSettings.adapter = settingsListAdapter
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
            val settingsList = mainActivityViewModel.getSettingsListFromDataSet()
            if (settingsList.isNullOrEmpty()) {
                settingsListAdapter.clear()
                return@launch
            }

            settingsListAdapter.updateList(settingsList)
        }
    }

    override fun setupClickListeners() {
        //Nothing to implement
    }
}