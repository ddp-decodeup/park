package com.parkloyalty.lpr.scan.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.parkloyalty.lpr.scan.R

object ViewPagerUtils {

    /**
     * Initialize dot indicators for ViewPager (classic)
     */
    fun setupViewPagerDots(
        context: Context,
        viewPager: ViewPager,
        dotsContainer: LinearLayout,
        totalCount: Int,
        activeColorRes: Int = R.color._FF5C47,
        inactiveColorRes: Int = R.color.gray
    ) {
        val dots = arrayOfNulls<ImageView>(totalCount)
        dotsContainer.removeAllViews()

        for (i in 0 until totalCount) {
            dots[i] = ImageView(context).apply {
                setImageDrawable(
                    createDotDrawable(context, inactiveColorRes)
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                dotsContainer.addView(this, params)
            }
        }

        dots[0]?.setImageDrawable(createDotDrawable(context, activeColorRes))

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                for (i in 0 until totalCount) {
                    dots[i]?.setImageDrawable(
                        createDotDrawable(
                            context,
                            if (i == position) activeColorRes else inactiveColorRes
                        )
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    fun setupViewPagerDots(
        context: Context,
        viewPager: ViewPager,
        dotsContainer: LinearLayoutCompat,
        totalCount: Int,
        activeColorRes: Int = R.color.black,
        inactiveColorRes: Int = R.color.gray
    ) {
        val dots = arrayOfNulls<ImageView>(totalCount)
        dotsContainer.removeAllViews()

        for (i in 0 until totalCount) {
            dots[i] = ImageView(context).apply {
                setImageDrawable(
                    createDotDrawable(context, inactiveColorRes)
                )
                val params = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                dotsContainer.addView(this, params)
            }
        }

        dots[0]?.setImageDrawable(createDotDrawable(context, activeColorRes))

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                for (i in 0 until totalCount) {
                    dots[i]?.setImageDrawable(
                        createDotDrawable(
                            context,
                            if (i == position) activeColorRes else inactiveColorRes
                        )
                    )
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    /**
     * Initialize dot indicators for ViewPager2
     */
    fun setupViewPager2Dots(
        context: Context,
        viewPager: ViewPager2,
        dotsContainer: LinearLayout,
        totalCount: Int,
        activeColorRes: Int = R.color.black,
        inactiveColorRes: Int = R.color.gray
    ) {
        val dots = arrayOfNulls<ImageView>(totalCount)
        dotsContainer.removeAllViews()

        for (i in 0 until totalCount) {
            dots[i] = ImageView(context).apply {
                setImageDrawable(
                    createDotDrawable(context, inactiveColorRes)
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                dotsContainer.addView(this, params)
            }
        }

        dots[0]?.setImageDrawable(createDotDrawable(context, activeColorRes))

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in 0 until totalCount) {
                    dots[i]?.setImageDrawable(
                        createDotDrawable(
                            context,
                            if (i == position) activeColorRes else inactiveColorRes
                        )
                    )
                }
            }
        })
    }

    private fun createDotDrawable(context: Context, colorRes: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ContextCompat.getColor(context, colorRes))
            setSize(20, 20)
        }
    }
}
