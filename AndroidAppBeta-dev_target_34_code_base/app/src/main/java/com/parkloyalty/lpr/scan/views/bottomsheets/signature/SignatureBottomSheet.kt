package com.parkloyalty.lpr.scan.views.bottomsheets.signature


import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.parkloyalty.lpr.scan.databinding.BottomSheetSignatureBinding
import com.parkloyalty.lpr.scan.views.base.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // add when you need Hilt in this sheet
class SignatureBottomSheet : BaseBottomSheetFragment<BottomSheetSignatureBinding>() {

    companion object {
        const val REQUEST_KEY = "signature_request"
        const val BUNDLE_KEY_BITMAP = "signature_bitmap"
        const val BUNDLE_KEY_BASE64 = "signature_base64"
    }

    override val isSheetCancelable = false
    override val isSheetDraggable = false


    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        BottomSheetSignatureBinding.inflate(inflater, container, false)

    // binding is available after onCreateView; safe to use in onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access views via binding
        val pad = binding.signaturePad
        val btnClear = binding.clear
        //val btnUndo = binding.btnUndo
        val btnCancel = binding.imgCancel
        val btnSave = binding.save

        btnClear.setOnClickListener { pad.clear() }
        //btnUndo.setOnClickListener { pad.undo() }
        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            // Get cropped signature bitmap (null if empty)
            val bmp: Bitmap? = pad.getSignatureBitmap(
                paddingPx = 16, backgroundColorIfNull = Color.WHITE
            )
            val base64 = pad.exportToBase64(
                paddingPx = 16, backgroundColorIfNull = Color.WHITE
            )

            val result = Bundle().apply {
                bmp?.let { putParcelable(BUNDLE_KEY_BITMAP, it) }
                base64?.let { putString(BUNDLE_KEY_BASE64, it) }
            }

            setFragmentResult(REQUEST_KEY, result)
            dismiss()
        }
    }
}
