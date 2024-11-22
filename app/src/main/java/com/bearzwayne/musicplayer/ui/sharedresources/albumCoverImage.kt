package com.bearzwayne.musicplayer.ui.sharedresources

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.request.ImageRequest
import com.bearzwayne.musicplayer.data.utils.DataProvider
import java.io.InputStream

suspend fun albumCoverImage(image: Uri, context: Context): Bitmap {
    return if (image.scheme == "http" || image.scheme == "https") {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(image)
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request)

        (result.drawable as? BitmapDrawable)?.bitmap ?: Bitmap.createBitmap(
            intArrayOf(-16777216, -1),
            1,
            2,
            Bitmap.Config.ARGB_8888
        )
    } else {
        val inputStream: InputStream? = try {
            context.contentResolver.openInputStream(image)
        } catch (e: Exception) {
            println("caught an exception while invoking albumCoverImage Function: $e")
            context.contentResolver.openInputStream(DataProvider.getDefaultCover())
        }

        inputStream?.use { BitmapFactory.decodeStream(it) } ?: Bitmap.createBitmap(
            intArrayOf(0xFF454343.toInt()),
            100,
            100,
            Bitmap.Config.ARGB_8888
        )
    }
}