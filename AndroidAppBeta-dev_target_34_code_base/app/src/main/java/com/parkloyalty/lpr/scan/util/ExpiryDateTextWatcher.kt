package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.getMonth
import com.parkloyalty.lpr.scan.extensions.getYear

//Usage : EditText?.addTextChangedListener(context, ExpiryDateTextWatcher())
//To Validate : ExpiryDateTextWatcher.validateExpiryDate(Expiry Date)
class ExpiryDateTextWatcher(val context : Context, private val mTextInputLayout: TextInputLayout?=null) : TextWatcher {

    private val CARD_DATE_TOTAL_DIGITS: Int = 4 // max numbers of digits in pattern: MM + YY
    private val CARD_DATE_DIVIDER_MODULO: Int = 3 // means divider position is every 3rd symbol beginning with 1
    private val CARD_DATE_DIVIDER_POSITION: Int = CARD_DATE_DIVIDER_MODULO - 1 // means divider position is every 2nd symbol beginning with 0

    companion object{
        const val CARD_DATE_DIVIDER: Char = '/'
        const val CARD_DATE_TOTAL_SYMBOLS: Int = 5 // size of pattern MM/YY

        //Function used to validate, whether the entered date is valid or not
        fun validateExpiryDate(expiryDate: String):Boolean{
            val splitedMM = expiryDate.getMonth()

            if (expiryDate.length == CARD_DATE_TOTAL_SYMBOLS && splitedMM.toInt() <= 12){
                return true
            }else{
                return false
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        if (!isInputCorrect(
                s,
                CARD_DATE_TOTAL_SYMBOLS,
                CARD_DATE_DIVIDER_MODULO,
                CARD_DATE_DIVIDER
            )
        ) {
            s.replace(
                0,
                s.length,
                concatString(
                    getDigitArray(s, CARD_DATE_TOTAL_DIGITS),
                    CARD_DATE_DIVIDER_POSITION,
                    CARD_DATE_DIVIDER
                )
            );
        }

        validateExpiryDate(context, s, mTextInputLayout)
    }

    private fun validateExpiryDate(context:Context, s: Editable, mTextInputEmail: TextInputLayout?) {
//        if (s.length == CARD_DATE_TOTAL_SYMBOLS){
//            val splitMMYY = s.split(CARD_DATE_DIVIDER)
//            val splitedMM = splitMMYY.first()
//            val splitedYY = splitMMYY.last()
//
//            if (splitedMM.toInt() > 12){
//                mTextInputEmail?.error = "Please enter valid month"
//            }else{
//                mTextInputEmail?.error = null
//            }
//        }
        if (s.length >= 4) {
            val splitedMM = s.toString().getMonth()
            val splitedYY = s.toString().getYear()

            if (splitedMM.toInt() > 12) {
                mTextInputEmail?.error = context.getString(R.string.val_msg_please_enter_valid_month)
            } else {
                mTextInputEmail?.error = null
            }
        } else {
            mTextInputEmail?.error = null
        }
    }

    private fun isInputCorrect(
        s: Editable,
        size: Int,
        dividerPosition: Int,
        divider: Char
    ): Boolean {
        var isCorrect = s.length <= size
        for (i in 0..<s.length) {
            isCorrect = if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect and (divider == s[i])
            } else {
                isCorrect and Character.isDigit(s[i])
            }
        }
        return isCorrect
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

    private fun concatString(digits: CharArray, dividerPosition: Int, divider: Char): String {
        val formatted = StringBuilder()

        for (i in digits.indices) {
            if (digits[i].code != 0) {
                formatted.append(digits[i])
                if ((i > 0) && (i < (digits.size - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider)
                }
            }
        }

        return formatted.toString()
    }
}
