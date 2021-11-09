package com.cengiztoru.hmscorekits.ui.map_kit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.databinding.ActivityMapKitBinding
import com.cengiztoru.hmscorekits.utils.Constants
import com.cengiztoru.hmscorekits.utils.extensions.hideToolBarSetStatusBarTransparent
import com.cengiztoru.hmscorekits.utils.extensions.isAllPermissionsGranted
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng

class MapKitActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "MapKitActivity"
        private const val PERMISSION_REQUEST_CODE = 9112021
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        private val neededPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private lateinit var mBinding: ActivityMapKitBinding
    private var mHuaweiMap: HuaweiMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.setApiKey(Constants.API_KEY)

        permissionOperations()
        initUi(savedInstanceState)

    }

//region MAP KIT FUNCTIONS

    override fun onMapReady(huaweiMap: HuaweiMap?) {
        mHuaweiMap = huaweiMap
        mHuaweiMap?.isMyLocationEnabled = true
        mHuaweiMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(41.1126131, 29.0073562),
                10f
            )
        )
    }

//endregion

//region LIFECYCLE FUNCTIONS

    override fun onStart() {
        super.onStart()
        mBinding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mBinding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding.mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBinding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.mapView.onSaveInstanceState(outState)
    }

//endregion

//region RUNTIME PERMISSIONS

    private fun permissionOperations() {
        if (isAllPermissionsGranted(neededPermissions).not()) {
            ActivityCompat.requestPermissions(
                this, neededPermissions,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE
            && grantResults.size == neededPermissions.size
            && grantResults.firstOrNull { it == PackageManager.PERMISSION_GRANTED } != null
        ) {
            printLog("onRequestPermissionsResult: PERMISSION GRANTED")
        } else {
            printLog("onRequestPermissionsResult:  USER NOT APPROVED PERMISSIONS")
            showToast("Please grant to permissions for using services")
        }
    }

//endregion

    private fun initUi(savedInstanceState: Bundle?) {
        hideToolBarSetStatusBarTransparent()
        mBinding = ActivityMapKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.mapView.apply {
            onCreate(savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY))
            getMapAsync(this@MapKitActivity)
        }
    }

    private fun printLog(log: String) {
        Log.i(TAG, log)
    }

}