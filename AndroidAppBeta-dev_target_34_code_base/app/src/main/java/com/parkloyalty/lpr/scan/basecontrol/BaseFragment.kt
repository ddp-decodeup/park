package com.parkloyalty.lpr.scan.basecontrol

import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.util.CustomAnimationUtil
import com.parkloyalty.lpr.scan.dialog.ViewDialog
import android.os.Bundle
import com.parkloyalty.lpr.scan.R
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.AppCompatEditText
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.widget.AppCompatTextView
import android.app.Activity
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.*
import android.widget.ViewSwitcher
import com.google.android.material.snackbar.Snackbar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.announceForAccessibility
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutCrossButtons
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.lang.Exception
import java.lang.StringBuilder

/* base fragment for all fragments
* please use this naming convention
*
 public static final int SOME_CONSTANT = 42;
    public int publicField;
    private static MyClass sSingleton;
    int mPackagePrivate;
    private int mPrivate;
    protected int mProtected;
    boolean isBoolean;
    boolean hasBoolean;
    View mMyView;
*
*
* */
@AndroidEntryPoint
abstract class BaseFragment : Fragment() {
    private var alertDialog: AlertDialog? = null
    private var customAnimationUtil: CustomAnimationUtil? = null
    private var viewDialog: ViewDialog? = null
    @Inject
    lateinit var sharedPreference: SharedPref
    private var mDb: AppDatabase? = null


    //This flag is used to show hide cross clear icon with input field
   // private var isEnableCrossClearButton : Boolean = false

    val ioScope = CoroutineScope(
        Job() + Dispatchers.IO
    )

    val mainScope = CoroutineScope(
        Job() + Dispatchers.Main
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDb = BaseApplication.instance?.getAppDatabase()

        getSettingFileValues()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Function used to fetch setting files values
     */
    private fun getSettingFileValues() {
//        val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
//
//        isEnableCrossClearButton = settingsList?.firstOrNull {
//            it.type.equals(
//                SETTINGS_FLAG_SHOW_CLEAR_ICON_FOR_INPUT_FIELDS,
//                true
//            ) && it.mValue.toBooleanFromYesNo()
//        }?.mValue.toBooleanFromYesNo()
    }

    /*
     * call this method to display full screen progress loader
     * */
    fun showProgressLoader(message: String?) {
        try {
            if (viewDialog == null) {
                viewDialog = ViewDialog(requireActivity())
            }
            viewDialog?.showDialog(message)
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
    }

    /*
     * call this method to dismiss progress loader
     * */
    fun dismissLoader() {
        try {
            if (viewDialog != null) {
                viewDialog?.hideDialog()
            }
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
        /*try {
            if (!this.isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    fun showCustomAlertDialog(
        mContext: Context?,
        title: String?,
        msg: String?,
        yesBTNTxt: String?,
        noBTNTxt: String?,
        customDialogHelper: CustomDialogHelper
    ) {
        AlertDialog.Builder(mContext!!)
            .setTitle("")
            .setCancelable(false)
            .setMessage(msg)
            .setPositiveButton(yesBTNTxt) { dialog, which ->
                dialog.dismiss()
                customDialogHelper.onYesButtonClick()
            }
            .setNegativeButton(noBTNTxt) { dialog, which -> dialog.dismiss() }
            .show()
    }

    fun setDialogVisible(mContext: Context?) {
        try {
            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setCancelable(false)
            val viewSwitcher = ViewSwitcher(mContext)
            //            viewSwitcher.addView(ViewSwitcher.inflate(mContext, R.layout.progressbar, null));
//            TextView txtView = (TextView) viewSwitcher.findViewById(R.id.textView);
//            ProgressBar progressBar = (ProgressBar) viewSwitcher.findViewById(R.id.progressBar_cyclic);
            builder.setView(viewSwitcher)
            alertDialog = builder.create()
            alertDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDialogCancel(mContext: Context?) {
        try {
            if (alertDialog != null && alertDialog?.isShowing == true) alertDialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSnakeBar(msg: String) {
        val snackbar: Snackbar = Snackbar.make(
            activity!!.findViewById(android.R.id.content),
            "" + msg,
            Snackbar.LENGTH_LONG
        )
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color._FF5C47))
        val textView =
            snackBarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(resources.getColor(R.color.white))
        snackbar.show()
    }

    fun TextWatchNormalForCardNumber(appCompatEditText: AppCompatTextView) {
        appCompatEditText.addTextChangedListener(object : TextWatcher {
            val TOTAL_SYMBOLS = 52 // size of pattern 0000-0000-0000-0000
            val TOTAL_DIGITS = 60 // max numbers of digits in pattern: 0000 x 4
            val DIVIDER_MODULO = 5 // means divider position is every 5th symbol beginning with 1
            val DIVIDER_POSITION = DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0
            val DIVIDER = ' '

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // noop
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // noop
            }

            override fun afterTextChanged(s: Editable) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(
                        0,
                        s.length,
                        buildCorrectString(
                            getDigitArray(s, TOTAL_DIGITS),
                            DIVIDER_POSITION,
                            DIVIDER
                        )
                    )
                }
            }

            private fun isInputCorrect(
                s: Editable,
                totalSymbols: Int,
                dividerModulo: Int,
                divider: Char
            ): Boolean {
                var isCorrect = s.length <= totalSymbols // check size of entered string
                for (i in s.indices) { // check that every element is right
                    isCorrect = if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect and (divider == s[i])
                    } else {
                        isCorrect and Character.isDigit(s[i])
                    }
                }
                return isCorrect
            }

            private fun buildCorrectString(
                digits: CharArray,
                dividerPosition: Int,
                divider: Char
            ): String {
                val formatted = StringBuilder()
                for (i in digits.indices) {
                    if (!digits[i].equals(0)) {
                        formatted.append(digits[i])
                        if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                            formatted.append(divider)
                        }
                    }
                }
                return formatted.toString()
            }

            private fun getDigitArray(s: Editable, size: Int): CharArray {
                val digits = CharArray(size)
                var index = 0
                var i = 0
                while (i < s.length && index < size) {
                    val current = s[i]
                    if (Character.isDigit(current)) {
                        digits[index] = current
                        index++
                    }
                    i++
                }
                return digits
            }
        })
    }

    /*remve amount format*/
    fun orignalAmount(amount: String): String {
        var sAmount = amount
        try {
            sAmount = amount.replace(",", "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sAmount
    }

    /*set error in input field if invalid*/
    fun setError(textInputLayout: TextInputLayout, error: String?) {
        textInputLayout.error = error
        textInputLayout.requestFocus()
        if (customAnimationUtil == null) {
            customAnimationUtil = CustomAnimationUtil(requireActivity())
        }
        customAnimationUtil!!.showErrorEditTextAnimation(textInputLayout, R.anim.shake)
    }

    //protected void addObservers() {}
    protected open fun removeObservers() {}
    override fun onDestroyView() {
        ioScope.cancel()
        mainScope.cancel()
        removeObservers()
        super.onDestroyView()
    }

    companion object {
        fun setErrorMessage(
            activity: Activity,
            inputLayouts: TextInputLayout,
            editText: AppCompatEditText,
            errorMsg: String?
        ) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    isValidateField(activity, inputLayouts, editText, errorMsg)
                }
            })
        }

        /**
         * override this method for validations
         */
        fun isValidateField(
            activity: Activity,
            inputLayouts: TextInputLayout,
            editText: AppCompatEditText,
            errorMsg: String?
        ): Boolean {
            return if (editText.text.toString().trim().isEmpty()) {
                inputLayouts.error = errorMsg
                requestFocus(activity, editText)
                makeMeShake(editText, 20, 5)
                false
            } else {
                inputLayouts.isErrorEnabled = false
                true
            }
        }

        /**
         * @param view     view that will be animated
         * @param duration for how long in ms will it shake
         * @param offset   start offset of the animation
         * @return returns the same view with animation properties
         */
        fun makeMeShake(view: View, duration: Int, offset: Int): View {
            val anim: Animation = TranslateAnimation((-offset).toFloat(), offset.toFloat(), 0f, 0f)
            anim.duration = duration.toLong()
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = 5
            view.startAnimation(anim)
            return view
        }

        fun requestFocus(activity: Activity, view: View) {
            if (view.requestFocus()) {
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    fun setCrossClearButton(
        mContext: Context,
        textInputLayout: TextInputLayout?,
        appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView?,
        isEditable: Boolean? = true
    ) {
        if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
            textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
            textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

            textInputLayout?.setStartIconOnClickListener {
                appCompatAutoCompleteTextView?.text = null
                announceForAccessibility(
                    textInputLayout,
                    mContext?.getString(R.string.ada_announcement_text_cleared).nullSafety()
                )
            }

            setAccessibilityForTextInputLayoutCrossButtons(mContext!!, textInputLayout)
        }
    }

    fun setCrossClearButton(
        mContext: Context,
        textInputLayout: TextInputLayout?,
        appCompatEditText: AppCompatEditText?,
        isEditable: Boolean? = true
    ) {
        if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
            textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
            textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

            textInputLayout?.setStartIconOnClickListener {
                appCompatEditText?.text = null
                announceForAccessibility(
                    textInputLayout,
                    mContext?.getString(R.string.ada_announcement_text_cleared).nullSafety()
                )
            }

            setAccessibilityForTextInputLayoutCrossButtons(mContext!!, textInputLayout)
        }
    }

    fun setCrossClearButton(
        mContext: Context,
        textInputLayout: TextInputLayout?,
        textInputEditText: TextInputEditText?,
        isEditable: Boolean? = true
    ) {
        if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
            textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
            textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

            textInputLayout?.setStartIconOnClickListener {
                textInputEditText?.text = null
                announceForAccessibility(
                    textInputLayout,
                    mContext?.getString(R.string.ada_announcement_text_cleared).nullSafety()
                )
            }

            setAccessibilityForTextInputLayoutCrossButtons(mContext!!, textInputLayout)
        }
    }
}