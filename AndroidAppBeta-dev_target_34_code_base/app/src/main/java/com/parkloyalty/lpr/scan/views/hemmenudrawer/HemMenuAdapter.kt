package com.parkloyalty.lpr.scan.views.hemmenudrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parkloyalty.lpr.scan.R

class HemMenuAdapter(
    private val items: MutableList<HemMenuItem>,
    private val listener: HemMenuListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface HemMenuListener {
        fun onOptionClicked(option: HemMenuItem.Option)
        fun onExpandableClicked(option: HemMenuItem.ExpandableOption)
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is HemMenuItem.Option -> 0
        is HemMenuItem.ExpandableOption -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hem_menu_option, parent, false)
            OptionViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hem_menu_expandable, parent, false)
            ExpandableViewHolder(view)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HemMenuItem.Option -> (holder as OptionViewHolder).bind(item)
            is HemMenuItem.ExpandableOption -> (holder as ExpandableViewHolder).bind(item)
        }
    }

    inner class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.imgIcon)
        private val title: TextView = view.findViewById(R.id.tvTitle)
        fun bind(option: HemMenuItem.Option) {
            icon.setImageResource(option.iconRes)
            title.text = option.title
            itemView.setOnClickListener { listener.onOptionClicked(option) }
        }
    }

    inner class ExpandableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.imgIcon)
        private val title: TextView = view.findViewById(R.id.tvTitle)
        private val expandIcon: ImageView = view.findViewById(R.id.imgExpand)
        private val subOptionsLayout: LinearLayout = view.findViewById(R.id.layoutSubOptions)
        fun bind(option: HemMenuItem.ExpandableOption) {
            icon.setImageResource(option.iconRes)
            title.text = option.title
            expandIcon.rotation = if (option.expanded) 180f else 0f
//            expandIcon.setImageResource(
//                if (option.expanded) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float
//            )
            subOptionsLayout.removeAllViews()
            if (option.expanded) {
                subOptionsLayout.visibility = View.VISIBLE
                for (sub in option.subOptions) {
                    val subView = LayoutInflater.from(itemView.context)
                        .inflate(R.layout.item_hem_menu_option, subOptionsLayout, false)
                    val subIcon = subView.findViewById<ImageView>(R.id.imgIcon)
                    val subTitle = subView.findViewById<TextView>(R.id.tvTitle)
                    subIcon.setImageResource(sub.iconRes)
                    subTitle.text = sub.title
                    subView.setOnClickListener { listener.onOptionClicked(sub) }
                    subOptionsLayout.addView(subView)
                }
            } else {
                subOptionsLayout.visibility = View.GONE
            }
            itemView.setOnClickListener {
                option.expanded = !option.expanded
                listener.onExpandableClicked(option)
                notifyItemChanged(adapterPosition)
            }
        }
    }
}

