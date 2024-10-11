package com.bearzwayne.musicplayer.ui.sharedresources.song

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.menu.MenuItemData

class MenuItemsAdapter(
    private val items: List<MenuItemData>,
    private val onItemClick: (MenuItemData) -> Unit
) : RecyclerView.Adapter<MenuItemsAdapter.MenuItemViewHolder>() {

    inner class MenuItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val title: TextView = view.findViewById(R.id.title)

        fun bind(item: MenuItemData) {
            icon.setImageResource(item.icon)
            title.text = item.title
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_menu_item_layout, parent, false)
        return MenuItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}