package com.parkloyalty.lpr.scan.ui.unuploadimages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.HistoryAdapter.ListItemSelectListener

// Adapter for showing Headers (ticket + plate) and Images
class UnUploadImagesAdapter(
    private var items: List<UnUploadImageSealedItem>,
    private val onItemClick: (UnUploadImageSealedItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_IMAGE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is UnUploadImageSealedItem.Header -> VIEW_TYPE_HEADER
            is UnUploadImageSealedItem.Image -> VIEW_TYPE_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_un_upload_images_view_1, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_un_upload_images_view_2, parent, false)
            ImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is UnUploadImageSealedItem.Header ->
                (holder as HeaderViewHolder).bind(item)

            is UnUploadImageSealedItem.Image ->
                (holder as ImageViewHolder).bind(item, onItemClick)
        }
    }

    override fun getItemCount(): Int = items.size

    // Method to update the list and notify the adapter of data changes
    fun submitList(newItems: List<UnUploadImageSealedItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    // ---------------- ViewHolders ----------------

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ticketText: TextView = itemView.findViewById(R.id.tvTicketNumber)
        private val plateText: TextView = itemView.findViewById(R.id.tvLpNumber)

        fun bind(header: UnUploadImageSealedItem.Header) {
            ticketText.text = header.ticketNumber
            plateText.text = header.lpNumber
        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivImage)
        private val statusView: ImageView = itemView.findViewById(R.id.ivStatus)

        fun bind(image: UnUploadImageSealedItem.Image, onClick: (UnUploadImageSealedItem) -> Unit) {
            Glide.with(itemView.context)
                .load(image.path)
                .centerCrop()
                .into(imageView)

            if (image.isUploaded) {
                statusView.setImageResource(R.drawable.ic_upload_done) // ✅ uploaded icon
            } else {
                statusView.setImageResource(R.drawable.ic_upload_fail) // ❌ not uploaded
            }
            // Click listener
            itemView.setOnClickListener {
                onClick(image)
            }
        }
    }

}
