package com.example.musicplayer.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object PermissionHandler {
    var storage_permissions = listOf<String>(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

        )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storage_permissions_33 = listOf<String>(
        android.Manifest.permission.READ_MEDIA_AUDIO
//    android.Manifest.permission.POST_NOTIFICATIONS
//    android.Manifest.permission.READ_MEDIA_VIDEO
    )


    fun permissions(): List<String> {
        val p = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storage_permissions_33
        } else {
            storage_permissions
        }
        return p
    }

    fun checkPermissions(context: Context): Boolean {
        val permissions = permissions()
        var allPermissionsGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                println("$permission is not granted")
                allPermissionsGranted = false
                break
            }
        }
        return allPermissionsGranted
    }

}