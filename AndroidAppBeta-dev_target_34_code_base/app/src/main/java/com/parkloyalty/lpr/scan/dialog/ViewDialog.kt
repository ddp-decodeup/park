package com.parkloyalty.lpr.scan.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety

class ViewDialog     //..we need the context else we can not create the dialog so get context in constructor
    (private val activity: Activity) {
    private var dialog: Dialog? = null

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun showDialog(message: String?) {
        if (dialog != null) {
            if (dialog?.isShowing.nullSafety()) {
                dialog?.dismiss()
                dialog?.cancel()
                dialog = null
            }
        }
        dialog = Dialog(activity, R.style.ThemeDialogCustomFullScreen)
        val view = activity.layoutInflater.inflate(R.layout.layout_progress_loader, null)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //...set cancelable false so that it's never get hidden
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        //...that's the layout i told you will inflate later
        dialog?.setContentView(view)
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        val appCompatTextView: AppCompatTextView =
            dialog?.findViewById(R.id.mMessage) as AppCompatTextView
        appCompatTextView.text = message
        //...initialize the imageView form infalted layout
        //  ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);

        /*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        */
        //GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

        //...now load that gif which we put inside the drawble folder here with the help of Glide

//        Glide.with(activity)
//                .load(R.drawable.loading)
//                .placeholder(R.drawable.loading)
//                .centerCrop()
//                .crossFade()
//                .into(imageViewTarget);

        //...finaly show it
        // For Full Screen Dialog Box
        if (dialog?.window != null) {
            dialog?.window?.setFlags(0, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            dialog?.window?.statusBarColor = ContextCompat.getColor(
                activity.applicationContext,
                R.color.colorSemiTransparentWhite
            )
        }
        if (!activity.isDestroyed && !activity.isFinishing && !dialog!!.isShowing) dialog?.show()
    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        if (dialog != null) dialog?.dismiss()
    }
}