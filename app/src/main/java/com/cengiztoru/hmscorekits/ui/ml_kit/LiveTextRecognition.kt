package com.cengiztoru.hmscorekits.ui.ml_kit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.forEach
import androidx.core.util.size
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitLiveTextRecognitionBinding
import com.cengiztoru.hmscorekits.utils.extensions.Constants
import com.cengiztoru.hmscorekits.utils.extensions.gone
import com.cengiztoru.hmscorekits.utils.extensions.isAllPermissionsGranted
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.text.MLText
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import java.io.IOException

class LiveTextRecognition : AppCompatActivity() {

    companion object {
        private const val TAG = "LiveTextRecognition"
        private const val PERMISSION_REQUEST_CODE: Int = 1112021
    }

    private lateinit var mBinding: ActivityMlkitLiveTextRecognitionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMlkitLiveTextRecognitionBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //Set Access token for using cloud based services
        MLApplication.getInstance().setAccessToken(Constants.API_KEY)

        setListeners()
    }

    //region LIVE TEXT RECOGNITION
    private fun localLiveStreamTextRecognition() {

        val neededPermissions = arrayOf(Manifest.permission.CAMERA)

        if (isAllPermissionsGranted(neededPermissions).not()) {
            ActivityCompat.requestPermissions(
                this, neededPermissions,
                PERMISSION_REQUEST_CODE
            )
            mBinding.fabStart.show()
            return
        }

        val analyzer = MLTextAnalyzer.Factory(this).create()
        val lensEngine = LensEngine.Creator(applicationContext, analyzer)
            .setLensType(LensEngine.BACK_LENS)
            .applyDisplayDimension(1440, 1080)
            .applyFps(30.0f)
            .enableAutomaticFocus(true)
            .create()


        class OcrDetectorProcessor : MLAnalyzer.MLTransactor<MLText.Block?> {
            override fun transactResult(results: MLAnalyzer.Result<MLText.Block?>) {
                val items = results.analyseList
                // Determine detection result processing as required. Note that only the detection results are processed.
                // Other detection-related APIs provided by ML Kit cannot be called.
                if (items.size > 0) {
                    var message = " "
                    items.forEach { key, value ->
                        message += "\n" + value?.stringValue
                    }
                    printLog(message)
                    Log.e(TAG, "DETECTION RESULTS ==>  $message")
                }
            }

            override fun destroy() {
                // Callback method used to release resources when the detection ends.
                printLog("destroy()")
                analyzer.stop()
                lensEngine.release()
                mBinding.fabStart.show()
            }
        }

        analyzer.setTransactor(OcrDetectorProcessor())

        try {
            lensEngine.run(mBinding.sfvCamera.holder)
        } catch (e: IOException) {
            // Exception handling logic.
            printLog("exception occured ${e.localizedMessage}")
            mBinding.fabStart.show()
        }
    }
//endregion

    //region HANDLING RUNTIME PERMISSIONS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                printLog("onRequestPermissionsResult: CAMERA PERMISSIONS GRANTED")
                localLiveStreamTextRecognition()
            } else {
                printLog("onRequestPermissionsResult:  USER NOT APPROVED PERMISSIONS")
                showToast("Please grant to permissions for we can using your location")
            }
        }

    }
//endregion

    private fun setListeners() {
        mBinding.fabStart.setOnClickListener {
            localLiveStreamTextRecognition()
            it.gone()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("\n\n$log")
        Log.i(TAG, log)
    }
}