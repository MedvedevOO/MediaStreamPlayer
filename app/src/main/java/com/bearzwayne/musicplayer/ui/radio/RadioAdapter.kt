package com.bearzwayne.musicplayer.ui.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.domain.model.RadioStation
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.other.PlayerState
import com.bumptech.glide.Glide

class RadioAdapter(
    private var radioStations: List<RadioStation>,
    private var favoriteStations: List<RadioStation>,
    private val onItemClick: (RadioStation) -> Unit,
    private val onLikeClick: (RadioStation, likeButton: ImageButton) -> Unit,
    private val currentSong: Song?,
    private val playerState: PlayerState?
) : RecyclerView.Adapter<RadioAdapter.RadioViewHolder>() {
    inner class RadioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stationImage: ImageView = itemView.findViewById(R.id.radio_station_image)
        val stationName: TextView = itemView.findViewById(R.id.radio_station_name)
        val stationCountry: TextView = itemView.findViewById(R.id.radio_station_country)
        val likeButton: ImageButton = itemView.findViewById(R.id.like_button)
        val playingGif: ImageView = itemView.findViewById(R.id.currently_playing_gif)

        fun bind(radioStation: RadioStation, currentSong: Song?, playerState: PlayerState?) {
            stationName.text = radioStation.name
            stationCountry.text = radioStation.country

            // Load image using Glide (or any other image loading library)
            Glide.with(stationImage.context)
                .load(radioStation.favicon)
                .placeholder(R.drawable.stocksongcover)
                .error(R.drawable.stocksongcover)
                .into(stationImage)

            // Show/hide currently playing GIF
            if (radioStation.url == currentSong?.songUrl && playerState == PlayerState.PLAYING) {
                playingGif.visibility = View.VISIBLE
            } else {
                playingGif.visibility = View.GONE
            }

            if (favoriteStations.contains(radioStation)) {
                likeButton.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                likeButton.setImageResource(R.drawable.ic_favorite_border)
            }

            // Handle click on like button
            likeButton.setOnClickListener {
                onLikeClick(radioStation, it as ImageButton)

            }

            // Handle click on the entire item
            itemView.setOnClickListener {
                onItemClick(radioStation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio_station, parent, false)
        return RadioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        val radioStation = radioStations[position]
        holder.bind(radioStation, currentSong, playerState)
    }

    override fun getItemCount(): Int = radioStations.size

    fun updateItems(newStations: List<RadioStation>, newFavoriteStations: List<RadioStation>) {
        radioStations = newStations
        favoriteStations = newFavoriteStations
        notifyDataSetChanged()
    }
}
