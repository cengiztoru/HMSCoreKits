package com.cengiztoru.hmscorekits.ui.location

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.databinding.ActivityLocationKitBinding
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*

class LocationKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LocationKitActivity"
        private const val REQUEST_CODE = 11102021
    }

    private lateinit var mBinding: ActivityLocationKitBinding

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val settingsClient by lazy {
        LocationServices.getSettingsClient(this)
    }

    private val mLocationRequest by lazy {
        LocationRequest().apply {
            interval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private val mLocationCallBack by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.let { result ->
                    val locations = result.locations
                    locations?.forEach { location ->
                        Log.d(
                            TAG,
                            "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.longitude + "," + location.latitude + "," + location.accuracy
                        )
                        printMessage("Latitude : ${location.latitude} \nLongitude: ${location.longitude} \nAccuracy: ${location.accuracy} ")
                    }
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability?) {
                super.onLocationAvailability(availability)
                availability?.let {
                    Log.i(
                        TAG,
                        "onLocationAvailability isLocationAvailable:" + availability.isLocationAvailable
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLocationKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setListeners()

        requestLocationPermission()

        checkLocationSettingsAndRequestUpdates()

    }

    private fun checkLocationSettingsAndRequestUpdates() {
        try {

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)

            //CHECK LOCATION SETTINGS
            val locationSettingsRequest = builder.build()
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    Log.i(TAG, "check location settings success")

                    requestLocationUpdates()

                }.addOnFailureListener {
                    Log.e(TAG, "checkLocationSetting onFailure:" + it.message)

                    val statusCode = (it as ApiException).statusCode
                    if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {

                        try {
                            val rae = (it as ResolvableApiException)
                            rae.startResolutionForResult(this, 0)

                        } catch (sie: IntentSender.SendIntentException) {
                            Log.e(TAG, "PendingIntent unable to execute request.")
                        }
                    }

                }

        } catch (e: Exception) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.message)
        }
    }

    private fun requestLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallBack,
            Looper.getMainLooper()
        ).addOnSuccessListener {
            Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess")
        }.addOnFailureListener {
            Log.e(TAG, "requestLocationUpdatesWithCallback onFailure:" + it.message)
        }
    }

    private fun removeLocationUpdatesWithCallback() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallBack)
                .addOnSuccessListener {
                    Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess")
                }
                .addOnFailureListener {
                    Log.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + it.message)
                }
        } catch (e: Exception) {
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.message)
        }
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


    private fun printMessage(message: String) {
        mBinding.tvLogger.append("\n\n$message")
        mBinding.svLogger.apply { post { fullScroll(View.FOCUS_DOWN) } }
    }

    private fun setListeners() {
        mBinding.btnReqLocationUpdates.setOnClickListener {
            printMessage("\n\nLocation Update Starting\n\n")
            checkLocationSettingsAndRequestUpdates()
        }

        mBinding.btnRemoveLocationUpdates.setOnClickListener {
            printMessage("\n\nCancelled Location Update Request\n\n")
            removeLocationUpdatesWithCallback()
        }
    }


}