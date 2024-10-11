package com.bearzwayne.musicplayer.ui.songscreen.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bumptech.glide.Glide

class SongImagePagerAdapter(private var images: List<String?>) :
    RecyclerView.Adapter<SongImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view_pager_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pager_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        // Load the image using Glide or another image loading library
        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .fitCenter()
                .placeholder(R.drawable.stocksongcover)
                .into(holder.imageView)
        }

    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateImages(newImages: List<String?>) {
        this.images = newImages

        notifyDataSetChanged()
    }
}
