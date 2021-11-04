package com.cengiztoru.hmscorekits.ui.ml_kit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMllkitFaceBodyServicesBinding
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult

class FaceBodyRelatedActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMllkitFaceBodyServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMllkitFaceBodyServicesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
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


    private fun setListeners() {
        mBinding.btnLivenessDetection.setOnClickListener {
            livenessDetection()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
    }
}