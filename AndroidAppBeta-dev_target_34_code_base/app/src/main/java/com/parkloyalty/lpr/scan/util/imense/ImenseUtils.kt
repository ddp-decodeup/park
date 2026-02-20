//package com.parkloyalty.lpr.scan.util.imense
//
//import android.util.Base64
//import com.parkloyalty.lpr.scan.extensions.logW
//
//object ImenseUtils {
//    fun decodeString(input: String): String {
//        return try {
//            val out = Base64.decode(input, Base64.DEFAULT)
//            String(out)
//        } catch (e: IllegalArgumentException) {
//            logW("ImenseUtils", "Invalid base64 input: $input - ${e.message}")
//            ""
//        }
//    }
//}