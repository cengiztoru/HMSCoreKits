package com.cengiztoru.hmscorekits.ui.map_kit

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
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
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.LatLngBounds

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

    private fun cameraFactoryFunctions() {
        return  //don't call whole function 😊
        //Increase the camera zoom level by 1 and retain other attribute settings.
        CameraUpdateFactory.zoomIn()

        // Decrease the camera zoom level by 1 and retain other attribute settings.
        CameraUpdateFactory.zoomOut()

        // Set the camera zoom level to a specified value and retain other attribute settings.
        CameraUpdateFactory.zoomTo(8.0f)

        //Increase or decrease the camera zoom level by a specified value.
        CameraUpdateFactory.zoomBy(2.0f)
        CameraUpdateFactory.zoomBy(-4.0f)

        // Move the camera to the specified center point and increase or decrease the camera zoom level
        val point = Point(31, 118)
        val amount = 2.0f
        CameraUpdateFactory.zoomBy(amount, point)

        // Set the latitude and longitude of the camera and retain other attribute settings.
        CameraUpdateFactory.newLatLng(LatLng(31.5, 118.9))

        // Set the visible region and padding.
        val padding = 100
        val visibleRegion1 = LatLng(31.5, 118.9)
        val visibleRegion2 = LatLng(32.5, 119.9)
        val latLngBounds = LatLngBounds(visibleRegion1, visibleRegion2)
        CameraUpdateFactory.newLatLngBounds(latLngBounds, padding)

        // Set the center point and zoom level of the camera.
        val zoom = 3.0f
        val latLng2 = LatLng(32.5, 119.9)
        CameraUpdateFactory.newLatLngZoom(latLng2, zoom)

        // Scroll the camera by specified number of pixels.
        val x = 100.0f
        val y = 100.0f
        CameraUpdateFactory.scrollBy(x, y)

        //Specify the camera position.
        // Set the tilt.
        val tilt = 2.2f
        // Set the bearing.
        val bearing = 31.5f
        val cameraPosition = CameraPosition(visibleRegion1, zoom, tilt, bearing)
        CameraUpdateFactory.newCameraPosition(cameraPosition)
    }

    private fun cameraMovementFunctions(cameraUpdate: CameraUpdate) {
        val cancelableCallback = object : HuaweiMap.CancelableCallback {
            override fun onFinish() {}

            override fun onCancel() {}
        }

        // Move the map camera in animation mode.
        mHuaweiMap?.animateCamera(cameraUpdate)
        // Move the map camera in animation mode, and set the API to be called back when the animation stops.
        mHuaweiMap?.animateCamera(cameraUpdate, cancelableCallback)
        // Move the map camera in animation mode, and set the animation duration and API to be called back when the animation stops.
        mHuaweiMap?.animateCamera(cameraUpdate, 250, cancelableCallback)

        // Move the map camera in non-animation mode.
        mHuaweiMap?.moveCamera(cameraUpdate)
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