import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import java.lang.ref.WeakReference

object DialogUtil {
    private var loaderDialogRef: WeakReference<Dialog>? = null

    /**
     * Show a loader dialog with app logo, circular progress, and message.
     * Only one loader is shown at a time. No background dim.
     * @param context Context (Activity or Application context)
     * @param message Message to show below loader
     * @param cancelable Whether dialog is cancelable
     */
    fun showLoader(context: Context, message: String? = context.getString(R.string.loader_text_please_wait), cancelable: Boolean? = false) {
        loaderDialogRef?.get()?.let {
            if (it.isShowing) {
                // Loader is already showing
                return
            }
        }

        //hideLoader()
        val dialog = Dialog(context, R.style.ThemeDialogCustomFullScreen)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_progress_loader, null)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.setCancelable(cancelable.nullSafety())
        dialog.setCanceledOnTouchOutside(cancelable.nullSafety())
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        // Set up views
//        val logo = view.findViewById<ImageView>(R.id.loaderLogo)
//        val progress = view.findViewById<ProgressBar>(R.id.loaderProgress)
        val text = view.findViewById<TextView>(R.id.mMessage)
//        logo.setImageResource(R.drawable.ic_app_name) // Replace with your logo resource
        text.text = message
        dialog.show()
        loaderDialogRef = WeakReference(dialog)
    }

    /**
     * Hide the loader dialog if showing.
     */
    fun hideLoader() {
        loaderDialogRef?.get()?.let {
            if (it.isShowing) it.dismiss()
            loaderDialogRef = null
        }
    }
}