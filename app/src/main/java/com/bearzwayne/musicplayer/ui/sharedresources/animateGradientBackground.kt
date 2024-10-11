package com.bearzwayne.musicplayer.ui.sharedresources

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import com.bearzwayne.musicplayer.R
import com.bearzwayne.musicplayer.data.DataProvider
import com.bearzwayne.musicplayer.domain.model.Song
import com.bearzwayne.musicplayer.ui.theme.extensions.generateDominantColorState

fun animateGradientBackground(dominantColor: Int, resources: Resources, theme: Theme): GradientDrawable {
    val surfaceColor = resources.getColor(R.color.bestOrange, theme)
    var gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(dominantColor, surfaceColor)
    )
    val animator = ValueAnimator.ofObject(ArgbEvaluator(), dominantColor, dominantColor)
    animator.duration = 3000 // 1 second animation
    animator.addUpdateListener { animation ->
        val color = animation.animatedValue as Int

        // Create a gradient drawable with animated color
        gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(color, surfaceColor)
        )
    }
    animator.start()
    return gradientDrawable
}

suspend fun loadAlbumCoverAndSetGradient(imageUrl: Uri?, context: Context, view: View) {
    var bitmap = albumCoverImage(
        imageUrl ?: DataProvider.getDefaultCover(),
        context
    )
    var swatch = try {
        bitmap.generateDominantColorState()
    } catch (e: Exception) {
        null
    }
    if (swatch == null) {
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
            eraseColor(0xFF454343.toInt())
        }
        bitmap.let { image ->
            swatch = image.generateDominantColorState()
        }
    }

    swatch?.let { view.background = animateGradientBackground(it.rgb,view.resources,context.theme) }
}