package com.cengiztoru.hmscorekits.ui.map_kit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMapKitBinding
import com.cengiztoru.hmscorekits.utils.Constants
import com.cengiztoru.hmscorekits.utils.extensions.hideToolBarSetStatusBarTransparent
import com.cengiztoru.hmscorekits.utils.extensions.isPermissionGranted
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.LatLng

class MapKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MapKitActivity"
        private const val PERMISSION_REQUEST_CODE = 9112021
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

        initUi()
        initMapByPermissionStatus()

    }

//region MAP KIT FUNCTIONS

    private fun onMapReady(huaweiMap: HuaweiMap?) {
        setMenuListener()
        mHuaweiMap = huaweiMap
        mHuaweiMap?.isMyLocationEnabled = true
        mHuaweiMap?.uiSettings?.isMyLocationButtonEnabled = true
        mHuaweiMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(41.1126131, 29.0073562),
                10f
            )
        )
    }

    private fun setMapType(type: Int) {
        mHuaweiMap?.mapType = if (type < 0 || type > 4) HuaweiMap.MAP_TYPE_NORMAL else type
    }

//endregion

//region RUNTIME PERMISSIONS

    private fun initMapByPermissionStatus() {
        val permissionGranted =
            isPermissionGranted(neededPermissions.first()) || isPermissionGranted(neededPermissions.last())
        if (permissionGranted) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(this, neededPermissions, PERMISSION_REQUEST_CODE)
        }
    }

    private fun initMap() {
        val mSupportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?)
        mSupportMapFragment?.getMapAsync(::onMapReady)
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
            initMap()
        } else {
            printLog("onRequestPermissionsResult:  USER NOT APPROVED PERMISSIONS")
            showToast("Please grant to permissions for using services")
            finish()
        }
    }

//endregion

    private fun initUi() {
        hideToolBarSetStatusBarTransparent()
        mBinding = ActivityMapKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }


    private fun setMenuListener() {
        mBinding.fabMenu.setOnItemClickListener { index ->
            val mapType = when (index) {
                1 -> {
                    HuaweiMap.MAP_TYPE_TERRAIN
                }
                else -> {
                    HuaweiMap.MAP_TYPE_NORMAL
                }
            }
            setMapType(mapType)
        }
    }

    private fun printLog(log: String) {
        Log.i(TAG, log)
    }

}