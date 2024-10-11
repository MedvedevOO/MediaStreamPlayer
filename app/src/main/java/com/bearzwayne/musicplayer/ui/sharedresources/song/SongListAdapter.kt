package com.bearzwayne.musicplayer.ui.sharedresources.song

import com.bearzwayne.musicplayer.domain.model.Song
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.other.PlayerState
import com.bumptech.glide.Glide

class SongListAdapter(
    private var allSongsList: List<Song>,
    private var playerState: PlayerState?,
    private var currentSong: Song?,
    private var favoriteSongs: List<Song>,
    private var songs: List<Song>,
    private val onSongClick: (Song) -> Unit,
    private val onSongLikeClick: (Song) -> Unit,
    private val onSongSettingsClick: (Song) -> Unit
) : RecyclerView.Adapter<SongListAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.song_title)
        val artist: TextView = itemView.findViewById(R.id.song_artist)
        val songImage: ImageView = itemView.findViewById(R.id.song_image)
        val gifImage: ImageView = itemView.findViewById(R.id.gif_image)
        val likeButton: ImageView = itemView.findViewById(R.id.like_button)
        val settingsButton: ImageButton = itemView.findViewById(R.id.settings_button)

        fun bind(song: Song) {
            title.text = song.title
            artist.text = song.artist

            Glide.with(songImage.context)
                .load(song.imageUrl)
                .placeholder(R.drawable.stocksongcover)
                .error(R.drawable.stocksongcover)
                .into(songImage)

            Glide.with(songImage.context)
                .asGif()
                .load(R.drawable.currently_playing)
                .into(gifImage)

            if (favoriteSongs.contains(song)) {
                likeButton.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                likeButton.setImageResource(R.drawable.ic_favorite_border)
            }

            if (playerState == PlayerState.PLAYING && song.songUrl == currentSong?.songUrl) {
                gifImage.visibility = View.VISIBLE
            } else {
                gifImage.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener { onSongClick(song) }
            likeButton.setOnClickListener { onSongLikeClick(song) }
            settingsButton.setOnClickListener { onSongSettingsClick(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs: List<Song>, newFavoriteSongs: List<Song>, playerState: PlayerState?, currentSong: Song?) {
        val oldSize = songs.size
        this.songs = newSongs.toList()
        this.playerState = playerState
        this.currentSong = currentSong
        if (oldSize < newSongs.size) {
            notifyItemRangeInserted(oldSize, newSongs.size - oldSize)
        } else {

            notifyDataSetChanged()
        }
        val oldFavoritesSize = favoriteSongs.size
        this.favoriteSongs = newFavoriteSongs
        if (oldFavoritesSize < newFavoriteSongs.size) {
            notifyItemRangeInserted(oldFavoritesSize, newFavoriteSongs.size - oldFavoritesSize)
        } else {
            notifyDataSetChanged()
        }

    }
}
