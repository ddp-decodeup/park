package com.parkloyalty.lpr.scan.util

import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.parkloyalty.lpr.scan.R

class DynamicLayoutUtil : AppCompatActivity() {
    /* <androidx.appcompat.widget.LinearLayoutCompat
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginLeft="@dimen/_16dp"
    android:layout_marginRight="@dimen/_16dp"
    android:gravity="center"
    android:orientation="horizontal"
    android:weightSum="2">

                <androidx.appcompat.widget.LinearLayoutCompat
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:orientation="vertical">

                    <androidx.appcompat.widget.AppCompatTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="left"
    android:layout_marginTop="@dimen/_6dp"
    android:fontFamily="@font/sf_pro_display_regular"
    android:text="@string/scr_lbl_ticket_number"
    android:textColor="@color/light_grey_100"
    android:textSize="@dimen/_14sp" />
                    <!--TAG Text Field-->
                    <androidx.appcompat.widget.AppCompatTextView
    android:tag="text"
    android:id="@+id/tvTicketNumber"
    android:layout_width="match_parent"
    android:layout_height="@dimen/_50dp"
    android:layout_marginTop="@dimen/_8dp"
    android:background="@drawable/round_corner_shape_without_fill_gray"
    android:fontFamily="@font/sf_pro_display_regular"
    android:gravity="left|center"
    android:imeOptions="actionDone"
    android:inputType="text"
    android:paddingHorizontal="@dimen/_20dp"
    android:text="0123856DNBG"
    android:textColor="@color/black_sub_heading"
    android:textSize="@dimen/_14sp" />

                </androidx.appcompat.widget.LinearLayoutCompat>
                <androidx.appcompat.widget.LinearLayoutCompat
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:orientation="vertical">

                    <androidx.appcompat.widget.AppCompatTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="left"
    android:layout_marginTop="@dimen/_6dp"
    android:layout_marginLeft="@dimen/_20dp"
    android:fontFamily="@font/sf_pro_display_regular"
    android:text="@string/scr_lbl_ticket_date"
    android:textColor="@color/light_grey_100"
    android:textSize="@dimen/_14sp" />
                    <!--TAG Text Field-->
                    <androidx.appcompat.widget.AppCompatTextView
    android:id="@+id/tvTicketDate"
    android:layout_width="match_parent"
    android:layout_height="@dimen/_50dp"
    android:layout_marginTop="@dimen/_8dp"
    android:background="@android:color/transparent"
    android:fontFamily="@font/sf_pro_display_regular"
    android:gravity="left|center"
    android:imeOptions="actionDone"
    android:inputType="text"
    android:paddingLeft="@dimen/_20dp"
    android:text="17 Mar,2021 10:00 Am"
    android:textColor="@color/black_sub_heading"
    android:textSize="@dimen/_14sp" />


                </androidx.appcompat.widget.LinearLayoutCompat>
            </androidx.appcompat.widget.LinearLayoutCompat>
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
        // This will create the LinearLayout
        val layTicketDetails = LinearLayout(this)
        layTicketDetails.orientation = LinearLayout.VERTICAL

        // Configuring the width and height of the linear layout.
        val llLP = LinearLayout.LayoutParams( //android:layout_width='match_parent' an in xml
            LinearLayout.LayoutParams.MATCH_PARENT,  //android:layout_height='wrap_content'
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layTicketDetails.layoutParams = llLP
        val tvTicketNumber = AppCompatTextView(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tvTicketNumber.layoutParams = lp
        tvTicketNumber.gravity = Gravity.LEFT
        tvTicketNumber.setTextColor(resources.getColor(R.color.light_grey_100))
        //android:text='@string/c4r'
        tvTicketNumber.setText(R.string.scr_lbl_ticket_number)
        //android:padding='@dimen/padding_medium'
        tvTicketNumber.textSize = 14f
        tvTicketNumber.typeface = typeface
        lp.setMargins(20, 6, 0, 8)
        layTicketDetails.addView(tvTicketNumber)
        val tvNew = AppCompatTextView(this)
        //tvNew.setLayoutParams(lpNew);
        tvNew.gravity = Gravity.LEFT or Gravity.CENTER
        tvNew.setTextColor(resources.getColor(R.color.black_sub_heading))
        //android:text='@string/c4r'
        tvNew.background =
            resources.getDrawable(R.drawable.dynamic_round_corner_shape_without_fill_gray)
        tvNew.text = "17 Mar,2021 10:00 Am"
        //android:padding='@dimen/padding_medium'
        tvNew.textSize = 14f
        tvNew.typeface = typeface
        tvNew.imeOptions = EditorInfo.IME_ACTION_DONE
        //lpNew.setMargins(0,8,0,0);
        layTicketDetails.addView(tvNew)
        val bt = Button(this)
        bt.setText(R.string.common_signin_button_text)
        bt.setPadding(8, 8, 8, 8)
        layTicketDetails.addView(bt)
        //Now finally attach the Linear layout to the current Activity.
        setContentView(layTicketDetails)
        bt.setOnClickListener { LogUtil.printToastMSG(applicationContext, "fjknj") }
    }
}