package com.bearzwayne.musicplayer.ui.addsongstoplaylist.components

import com.bearzwayne.musicplayer.domain.model.Song
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bumptech.glide.Glide

class AddSongItemAdapter(
    private var songs: List<Song>,
    private val onAddSongButtonClick: (Song) -> Unit,
) : RecyclerView.Adapter<AddSongItemAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.song_title)
        val artist: TextView = itemView.findViewById(R.id.song_artist)
        val songImage: ImageView = itemView.findViewById(R.id.song_image)
        val addButton: ImageView = itemView.findViewById(R.id.add_button)

        fun bind(song: Song) {
            title.text = song.title
            artist.text = song.artist
            Glide.with(songImage.context)
                .load(song.imageUrl)
                .placeholder(R.drawable.stocksongcover)
                .error(R.drawable.stocksongcover)
                .into(songImage)
            addButton.setOnClickListener { onAddSongButtonClick(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_add_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs: List<Song>) {

        this.songs = newSongs

            notifyDataSetChanged()

    }
}
