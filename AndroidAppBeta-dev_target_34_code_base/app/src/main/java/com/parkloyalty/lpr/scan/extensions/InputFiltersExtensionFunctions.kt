package com.parkloyalty.lpr.scan.extensions

import android.text.InputFilter
import java.util.regex.Pattern

var filter = InputFilter { source, start, end, dest, dstart, dend ->
    for (i in start until end) {
        if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*")
                .matcher(
                    source[i].toString()
                ).matches()
        ) {
            return@InputFilter ""
        }
    }
    null
}