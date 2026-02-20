package com.parkloyalty.lpr.scan.views.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<VB : ViewBinding> : BottomSheetDialogFragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected open val isSheetCancelable: Boolean = true
    protected open val isSheetDraggable: Boolean =
        false   // <--- disable swipe-to-dismiss by default
    protected open val isSheetHideable: Boolean = false    // <--- prevent sheet from hiding
    protected open val cancelOnTouchOutside: Boolean = isSheetCancelable


    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        super.onCreateDialog(savedInstanceState).apply {
            // optional: customize dialog window here if needed
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        //_binding = inflateBinding.invoke(inflater, container, false)
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    /**
     * For subclasses to use instead of overriding onCreateView.
     * Called after binding is created.
     */
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = isSheetCancelable
    }

    @CallSuper
    override fun onStart() {
        super.onStart()

        // Ensure we have a BottomSheetDialog and find the behavior from the bottom sheet view
        val dialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?: return

        val behavior = BottomSheetBehavior.from(bottomSheet)

        // Apply configurable behavior flags
        // IMPORTANT: isDraggable API exists in Material Components 1.4+; if using older, fallback below
        try {
            behavior.isDraggable = isSheetDraggable
        } catch (e: NoSuchMethodError) {
            // older Material versions don't have isDraggable setter; fallback to intercepting callbacks below
        }

        behavior.isHideable = isSheetHideable

        // Optional: keep it expanded so users can scroll content comfortably
        // behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Prevent the sheet from going to STATE_HIDDEN accidentally:
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // no-op or handle if you want (e.g. animate toolbar)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // If hideable == false, make sure it doesn't go to hidden state
                if (!isSheetHideable && newState == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                // If we disabled dragging, ensure it stays expanded or collapsed as per your need
                if (!isSheetDraggable) {
                    // if you want it always expanded, uncomment:
                    // if (newState != BottomSheetBehavior.STATE_EXPANDED) behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })

        // Extra: make outside-touch follow isCancelable as well
        dialog.setCancelable(isSheetCancelable)
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside)
    }

    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun getTheme(): Int {
        // keep default bottom sheet theme or change to app-specific theme
        return com.google.android.material.R.style.Theme_Material3_Light_BottomSheetDialog
    }
}