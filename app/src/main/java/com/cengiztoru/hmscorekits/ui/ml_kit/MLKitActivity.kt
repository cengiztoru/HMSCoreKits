package com.cengiztoru.hmscorekits.ui.ml_kit

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitBinding
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLText


class MLKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MLKitActivity"
    }

    private lateinit var mBinding: ActivityMlkitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMlkitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setListeners()
    }


//region TEXT RECOGNITION

    private fun localTextRecognition() {
        val analyzer = MLAnalyzerFactory.getInstance().localTextAnalyzer
        // Create an MLFrame object using the bitmap, which is the image data in bitmap format.
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.text_recognition)
        val frame = MLFrame.fromBitmap(bitmap)

        val task: Task<MLText> = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener {
            // Processing for successful recognition.
            printLog("TEXT RECOGNITION SUCCESS \n ${it.stringValue}")

//      After the recognition is complete, stop the analyzer to release recognition resources
            analyzer.stop()
        }.addOnFailureListener {
            // Processing logic for recognition failure.
            printLog("TEXT RECOGNITION FAILURE: ${it.localizedMessage}")
        }
    }

//endregion


    private fun setListeners() {
        mBinding.btnTextRecognition.setOnClickListener {
            localTextRecognition()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("\n\n$log")
    }
}