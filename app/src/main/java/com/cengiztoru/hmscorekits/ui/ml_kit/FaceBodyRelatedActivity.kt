package com.cengiztoru.hmscorekits.ui.ml_kit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMllkitFaceBodyServicesBinding
import com.cengiztoru.hmscorekits.utils.extensions.isAllPermissionsGranted
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult

class FaceBodyRelatedActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 51120212
    private lateinit var mBinding: ActivityMllkitFaceBodyServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMllkitFaceBodyServicesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        permissionOperations()
        setListeners()
    }


    private fun livenessDetection() {
        mBinding.imgImage.setImageResource(R.drawable.ic_launcher_foreground)
        val callback: MLLivenessCapture.Callback = object : MLLivenessCapture.Callback {
            override fun onSuccess(result: MLLivenessCaptureResult) {
                // Processing logic when the detection is successful. The detection result indicates whether the face is of a real person.
                printLog("LIVENESS DETECTION SUCCESS. \n\n isLive : ${result.isLive}")
                mBinding.imgImage.setImageBitmap(result.bitmap)
            }

            override fun onFailure(errorCode: Int) {
                // Processing logic when the detection fails. For example, the camera is abnormal (CAMERA_ERROR).
                printLog("LIVENESS DETECTION  FAILURE $errorCode")
            }
        }
        val capture = MLLivenessCapture.getInstance()
        capture.startDetect(this, callback)
    }

//region HANDLING RUNTIME PERMISSIONS

    private fun permissionOperations() {
        val neededPermissions = arrayOf(Manifest.permission.CAMERA)

        if (isAllPermissionsGranted(neededPermissions).not()) {
            ActivityCompat.requestPermissions(
                this, neededPermissions,
                PERMISSION_REQUEST_CODE
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            printLog("onRequestPermissionsResult: CAMERA PERMISSIONS GRANTED")
        } else {
            printLog("onRequestPermissionsResult:  USER NOT APPROVED PERMISSIONS")
            showToast("Please grant to permissions for using service")
        }

    }

//endregion


    private fun setListeners() {
        mBinding.btnLivenessDetection.setOnClickListener {
            livenessDetection()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
    }
}