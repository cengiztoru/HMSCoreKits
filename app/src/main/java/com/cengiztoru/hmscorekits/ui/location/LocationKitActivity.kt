package com.cengiztoru.hmscorekits.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.databinding.ActivityLocationKitBinding
import com.cengiztoru.hmscorekits.utils.extensions.showToast

class LocationKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LocationKitActivity"
        private const val REQUEST_CODE = 11102021
    }

    private lateinit var mBinding: ActivityLocationKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLocationKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        requestLocationPermission()

    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val strings = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, strings, REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: LOCATION PERMISSIONS GRANTED")
            } else {
                Log.i(TAG, "onRequestPermissionsResult:  USER NOT APPROVED PERMISSIONS")
                showToast("Please grant to permissions for we can using your location")
            }
        }

    }


    private fun requestPermissionsWithBackgroundLocation() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                )

                ActivityCompat.requestPermissions(this, strings, 2)
            }
        }
    }

}