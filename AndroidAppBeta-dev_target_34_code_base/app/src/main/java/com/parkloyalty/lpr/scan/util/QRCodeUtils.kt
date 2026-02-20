package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity.WINDOW_SERVICE
import androidx.core.graphics.drawable.toBitmap
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.QrVectorOptions
import com.google.zxing.WriterException
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG

object QRCodeUtils {

    fun getFinalQRCodeValue(
        qrUrl: String,
        citationNumberId: String? = null,
        lprNumber: String? = null,
        state: String? = null
    ): String {
        val finalQR0 = qrUrl.nullSafety().replace("[ticket_no]", citationNumberId.nullSafety())
        val finalQR1 = finalQR0.nullSafety().replace(
            "[lpr_number]",
            lprNumber.nullSafety()
        )
        return finalQR1.nullSafety().replace("[state]", state.nullSafety())
    }

    fun generateQRCodeForPrint(
        context: Context,
        finalQRCode: String
    ): Bitmap? {
        //https://learningprogramming.net/mobile/android/create-and-scan-barcode-in-android/
        try {

            // below line is for getting
            // the windowmanager service.
            val manager = context.getSystemService(WINDOW_SERVICE) as WindowManager

            // initializing a variable for default display.
            val display = manager.defaultDisplay

            // creating a variable for point which
            // is to be displayed in QR Code.
            val point = Point()
            display.getSize(point)

            // getting width and
            // height of a point
            val width = point.x
            val height = point.y

            // generating dimension from width and height.
            var dimen = if (width < height) width else height
            if (
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)
                ) {
                dimen = dimen * 2 / 8
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_KALAMAZOO,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                    ignoreCase = true
                ) ||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DANVILLE_VA,
                    ignoreCase = true
                ) ||BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CAMDEN,
                    ignoreCase = true
                ) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
            ) {
                if ((AppUtils.getDeviceTypePhoneOrTablet(context)).equals("TABLET")) {
                    dimen = dimen * 2 / 20
                } else {
                    dimen = dimen * 2 / 12
                }
            } else if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LEAVENWORTH,
                    ignoreCase = true
                )
            ) {
                dimen = dimen * 2 / 15
            } else {
                dimen = dimen * 2 / 10
            }

//                hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
//                hints.put(EncodeHintType.MARGIN, 0) /* default = 4 */

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            //val qrgEncoder = QRGEncoder(finalQR, null, QRGContents.Type.TEXT, dimen)
            try {
                // getting our qrcode in the form of bitmap
                // val bitmapQrCode = qrgEncoder.encodeAsBitmap()
                val bitmapQrCode = if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_LEAVENWORTH,
                        ignoreCase = true
                    )
                ) {
                    QRCodeUtils.getQRCode(finalQRCode, dimen, .0f)
                } else {
                    QRCodeUtils.getQRCode(finalQRCode, dimen, .3f)
                }

                return bitmapQrCode

            } catch (e: WriterException) {
                // this method is called for
                // exception handling.
                e.printStackTrace()
                return null
            }

        } catch (e: Exception) {
            printToastMSG(context, e.message)
            return null
        }
    }

    fun getQRCode(value: String, dimen: Int, padding: Float? = .3f): Bitmap {
        LogUtil.printLog("==>QRCodeValue:", value)
        val options = QrVectorOptions.Builder()
            .setPadding(padding.nullSafety())
//                    .setLogo(
//                        QrVectorLogo(
//                            drawable = ContextCompat
//                                .getDrawable(context, R.drawable.logo),
//                            size = .25f,
//                            padding = QrVectorLogoPadding.Natural(.2f),
//                            shape = QrVectorLogoShape
//                                .Circle
//                        )
//                    )
//                    .setBackground(
//                        QrVectorBackground(
//                            drawable = ContextCompat
//                                .getDrawable(context, R.drawable.frame),
//                        )
//                    )
//                    .setColors(
//                        QrVectorColors(
//                            dark = QrVectorColor
//                                .Solid(Color(0xff345288)),
//                            ball = QrVectorColor.Solid(
//                                ContextCompat.getColor(context, R.color.your_color)
//                            ),
//                            frame = QrVectorColor.LinearGradient(
//                                colors = listOf(
//                                    0f to android.graphics.Color.RED,
//                                    1f to android.graphics.Color.BLUE,
//                                ),
//                                orientation = QrVectorColor.LinearGradient
//                                    .Orientation.LeftDiagonal
//                            )
//                        )
//                    )
//                    .setShapes(
//                        QrVectorShapes(
//                            darkPixel = QrVectorPixelShape
//                                .RoundCorners(.5f),
//                            ball = QrVectorBallShape
//                                .RoundCorners(.25f),
//                            frame = QrVectorFrameShape
//                                .RoundCorners(.25f),
//                        )
//                    )
            .build()

        val drawable: Drawable = QrCodeDrawable(QrData { value }, options)
        val bitmapQrCode = drawable.toBitmap(width = dimen, height = dimen)
        return bitmapQrCode;
    }
}