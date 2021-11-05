package com.cengiztoru.hmscorekits.ui.ml_kit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitBinding
import com.cengiztoru.hmscorekits.utils.extensions.Constants
import com.cengiztoru.hmscorekits.utils.extensions.isAllPermissionsGranted
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.cengiztoru.hmscorekits.utils.extensions.startActivity
import com.huawei.hms.mlsdk.common.MLApplication

class MLKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MLKitActivity"
        private const val PERMISSION_REQUEST_CODE = 5112021
    }

    private lateinit var mBinding: ActivityMlkitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMlkitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //Set Access token for using cloud based services
        MLApplication.getInstance().setAccessToken(Constants.API_KEY)
        permissionOperations()
        setListeners()
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
            showToast("Please grant to permissions for using services")
        }

    }
//endregion

    private fun setListeners() {
        mBinding.btnTextRelated.setOnClickListener {
            startActivity<TextRecognitionActivity>()
        }

        mBinding.btnLanguageVoiceRelated.setOnClickListener {
            startActivity<LanguageVoiceRecognitionActivity>()
        }

        mBinding.btnImageRelated.setOnClickListener {
            startActivity<ImageRelatedRecognition>()
        }

        mBinding.btnFaceBodyRelated.setOnClickListener {
            startActivity<FaceBodyRelatedActivity>()
        }
    }

    private fun printLog(log: String) {
        Log.i(TAG, log)
    }


}