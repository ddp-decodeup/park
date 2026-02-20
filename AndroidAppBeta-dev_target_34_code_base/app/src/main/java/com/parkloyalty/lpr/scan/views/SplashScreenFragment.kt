package com.parkloyalty.lpr.scan.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.FragmentSplashScreenBinding
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenFragment : BaseFragment<FragmentSplashScreenBinding>() {
    private var param1: String? = null
    private var param2: String? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSplashScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {

    }

    override fun initViewLifecycleScope() {

    }

    override fun initialiseData() {

    }

    override fun setupClickListeners() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Use binding safely here, e.g.:
        // binding.yourView.text = "..."
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SplashScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}