package yayuki.heartrate.android.patient

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

@Suppress("MemberVisibilityCanBePrivate")
object PermissionUtils {
    fun sdkCheck(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun checkResult(
        grantResults: IntArray
    ): Boolean {
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    fun requestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ): Boolean {
        if (!sdkCheck()) return true
        for (permission in permissions) {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                activity, permission
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity, permissions, requestCode
                )
                return false
            }
        }
        return true
    }

    fun requestLocationPermissions(activity: Activity, requestCode: Int): Boolean {
        return requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), requestCode
        )
    }

    fun checkPermission(ctx: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                ctx, permission
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun Fragment.requestLocationPermissions(requestCode: Int): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return if (checkPermission(requireContext(), permissions)) {
            true
        } else {
            requestPermissions(
                permissions, requestCode
            )
            false
        }
    }
}