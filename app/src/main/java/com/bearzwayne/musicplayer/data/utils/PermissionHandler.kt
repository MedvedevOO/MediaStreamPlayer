package com.bearzwayne.musicplayer.data.utils

import android.os.Build
import androidx.annotation.RequiresApi

object PermissionHandler {
    private var storage_permissions = listOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

        )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storage_permissions_33 = listOf(
        android.Manifest.permission.READ_MEDIA_AUDIO
    )


    fun permissions(): List<String> {
        val p = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storage_permissions_33
        } else {
            storage_permissions
        }
        return p
    }

}