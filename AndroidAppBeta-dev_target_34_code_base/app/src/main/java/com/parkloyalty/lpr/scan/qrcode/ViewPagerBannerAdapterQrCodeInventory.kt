package com.parkloyalty.lpr.scan.qrcode

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.card.MaterialCardView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.intToBool
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.util.EQUIPMENT_CHECKED_OUT

class ViewPagerBannerAdapterQrCodeInventory(
    private val mContext: Context,
    var listItemSelectListener: ListItemSelectListener
) : PagerAdapter() {
    private var equipmentList: List<InventoryToShowTable?>? = null

    fun setEquipmentList(equipmentList: List<InventoryToShowTable?>?) {
        this.equipmentList = equipmentList
        notifyDataSetChanged()
    }

    override fun getCount(): Int = equipmentList?.takeIf { it.isNotEmpty() }?.size ?: 1

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view === (`object` as? View)

    override fun getPageWidth(position: Int): Float = 0.5f

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(mContext)
            .inflate(R.layout.row_pager_equipment_inventory, container, false)

        val cvEquipmentView: MaterialCardView = itemView.findViewById(R.id.cvEquipmentView)
        val tvEquipmentTitle: AppCompatTextView = itemView.findViewById(R.id.tvEquipmentTitle)
        val tvEquipmentValue: AppCompatTextView = itemView.findViewById(R.id.tvEquipmentValue)
        val ivIsScanned: ImageView = itemView.findViewById(R.id.ivIsScanned)

        val equipmentItem = equipmentList?.getOrNull(position)

        equipmentItem?.let { item ->
            val name = item.equipmentName.nullSafety()
            if (name.isNotEmpty()) {
                tvEquipmentTitle.text = name

                val isRequired = item.required.intToBool() && item.checkedOut != EQUIPMENT_CHECKED_OUT
                val strokeColor = if (isRequired) R.color.deep_red else R.color.white
                cvEquipmentView.strokeColor = ContextCompat.getColor(mContext, strokeColor)

                val adaDesc = if (isRequired) R.string.ada_content_description_required else R.string.pause_in_talkback_short
                tvEquipmentTitle.contentDescription = name + mContext.getString(adaDesc)

                if (item.checkedOut != EQUIPMENT_CHECKED_OUT) {
                    tvEquipmentValue.text = mContext.getString(R.string.lbl_not_scanned)
                    tvEquipmentValue.setTextColor(mContext.getColor(R.color.light_grey_100))
                    ivIsScanned.hideView()
                } else {
                    tvEquipmentValue.text = item.equipmentValue.nullSafety()
                    tvEquipmentValue.setTextColor(mContext.getColor(R.color.black_heading))
                    ivIsScanned.showView()
                }

                itemView.setOnClickListener {
                    listItemSelectListener.onItemClick(position, item.checkedOut.nullSafety())
                }
            }
        }

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView((`object` as? View) ?: return)
    }

    override fun saveState(): Parcelable? = null

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    interface ListItemSelectListener {
        fun onItemClick(position: Int, isCheckedOut: Int)
    }
}