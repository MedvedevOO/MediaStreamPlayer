package com.bearzwayne.musicplayer.ui.library.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.Album
import com.bearzwayne.musicplayer.domain.model.Artist
import com.bearzwayne.musicplayer.domain.model.Playlist
import com.bumptech.glide.Glide

class LibraryAdapter(
    private val onLibraryItemClick: (Any) -> Unit,
    private val onAddPlaylistClick:() -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<LibraryItem>()

    fun updateItems(newItems: List<LibraryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is LibraryItem.Header -> 0
            is LibraryItem.AddPlaylist -> 2
            is LibraryItem.PlaylistItem, is LibraryItem.ArtistItem, is LibraryItem.AlbumItem -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.library_item_header, parent, false)
            HeaderViewHolder(view)
        } else if (viewType == 2){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_playlist_horizontal_card, parent, false)
            AddPlaylistViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_library_horizontal_card, parent, false)
            LibraryItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is LibraryItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is LibraryItem.AddPlaylist -> (holder as AddPlaylistViewHolder).bind(onAddPlaylistClick)
            is LibraryItem.PlaylistItem -> (holder as LibraryItemViewHolder).bind(item.playlist, onLibraryItemClick)
            is LibraryItem.ArtistItem -> (holder as LibraryItemViewHolder).bind(item.artist, onLibraryItemClick)
            is LibraryItem.AlbumItem -> (holder as LibraryItemViewHolder).bind(item.album, onLibraryItemClick)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle: TextView = itemView.findViewById(R.id.header_title)

        fun bind(title: String) {
            headerTitle.text = title
        }
    }

    class AddPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addButton: ImageView = itemView.findViewById(R.id.add_playlist_image)

        fun bind(onClick: () -> Unit) {
            addButton.setOnClickListener { onClick() }
        }
    }

    class LibraryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        private val topCardText: TextView = itemView.findViewById(R.id.top_card_text)
        private val bottomCardText: TextView = itemView.findViewById(R.id.bottom_card_text)

        fun bind(content: Any, onClick: (Any) -> Unit) {
            itemView.setOnClickListener { onClick(content) }

            when (content) {
                is Playlist -> {
                    loadImage(content.artWork)
                    itemImage.setBackgroundResource(R.drawable.rounded_image_shape)
                    topCardText.text = content.name
                    bottomCardText.text = itemView.context.getString(R.string.tracks, content.songList.size.toString())
                }
                is Artist -> {
                    loadImage(content.photo)
                    itemImage.setBackgroundResource(R.drawable.round_image_shape)
                    topCardText.text = content.name
                    bottomCardText.text = itemView.context.getString(R.string.tracks_from_albums, content.songList.size.toString(), content.albumList.size.toString())
                }
                is Album -> {
                    loadImage(content.albumCover)
                    itemImage.setBackgroundResource(R.drawable.rounded_image_shape)
                    topCardText.text = content.name
                    bottomCardText.text = itemView.context.getString(R.string.by_author, content.artist)
                }
            }
        }

        private fun loadImage(imageUrl: String) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.stocksongcover) // Fallback image
                .into(itemImage)
        }
    }
}
