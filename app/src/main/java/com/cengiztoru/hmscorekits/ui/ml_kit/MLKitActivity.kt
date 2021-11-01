package com.cengiztoru.hmscorekits.ui.ml_kit

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitBinding
import com.cengiztoru.hmscorekits.utils.extensions.Constants
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting
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

        //Set Access token for using cloud based services
        MLApplication.getInstance().setAccessToken(Constants.API_KEY)


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

//      After the recognition is complete, stop the analyzer to release recognition resources
            analyzer.stop()
        }
    }

    private fun cloudTextRecognition() {
        // Create a language set.
        val languageList: MutableList<String> = ArrayList()
        languageList.add("tr")
        languageList.add("en")

        // Set parameters.
        val setting = MLRemoteTextSetting.Factory()

            // Set the on-cloud text detection mode.
            // MLRemoteTextSetting.OCR_COMPACT_SCENE: dense text recognition
            // MLRemoteTextSetting.OCR_LOOSE_SCENE: sparse text recognition
            .setTextDensityScene(MLRemoteTextSetting.OCR_LOOSE_SCENE)

            // Specify the languages that can be recognized, which should comply with ISO 639-1.
            .setLanguageList(languageList)

            // Set the format of the returned text border box.
            // MLRemoteTextSetting.NGON: Return the coordinates of the four corner points of the quadrilateral.
            // MLRemoteTextSetting.ARC: Return the corner points of a polygon border in an arc. The coordinates of up to 72 corner points can be returned.
            .setBorderType(MLRemoteTextSetting.ARC)
            .create()

        val analyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(setting)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.text_recognition)
        val frame = MLFrame.fromBitmap(bitmap)
        val task = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener {
            // Recognition success.
            printLog("TEXT RECOGNITION ON CLOUD SUCCESS \n ${it.stringValue}")

            //After the recognition is complete, stop the analyzer to release recognition resources
            analyzer.stop()
        }.addOnFailureListener { e ->
            // If the recognition fails, obtain related exception information.
            try {
                // Recognition failure.
                val mlException = e as MLException
                printLog("TEXT RECOGNITION ON CLOUD FAILURE. Message: ${mlException.message} Error Code : ${mlException.errCode}")

                //After the recognition is complete, stop the analyzer to release recognition resources
                analyzer.stop()
            } catch (error: Exception) {
                // Handle the conversion error.
            }
        }

    }

//endregion


    private fun setListeners() {
        mBinding.btnTextRecognition.setOnClickListener {
//            localTextRecognition()
            cloudTextRecognition()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("\n\n$log")
    }
}