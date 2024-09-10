package com.example.musicplayer.ui.sharedresources

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.musicplayer.data.DataProvider
import java.io.InputStream

suspend fun albumCoverImage(image: Uri, context: Context): Bitmap {

    return if (image.scheme == "http" || image.scheme == "https") {
        // Load the image from the web
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(image)
            .allowHardware(false) // Disable hardware bitmaps, since they can't be manipulated.
            .build()

        val result = imageLoader.execute(request)

        (result.drawable as? BitmapDrawable)?.bitmap ?: Bitmap.createBitmap(
            intArrayOf(-16777216, -1),
            1,
            2,
            Bitmap.Config.ARGB_8888
        )
    } else {
        // Load the image from the local storage
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