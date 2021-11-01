package com.cengiztoru.hmscorekits.ui.ml_kit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitTextRecognitionBinding
import com.cengiztoru.hmscorekits.utils.extensions.startActivity
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.document.MLDocumentSetting
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting
import com.huawei.hms.mlsdk.text.MLText
import java.io.IOException
import kotlin.random.Random


class TextRecognitionActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TextRecognitionActivity"
    }

    private lateinit var mBinding: ActivityMlkitTextRecognitionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMlkitTextRecognitionBinding.inflate(layoutInflater)
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
            printLog(it.stringValue)

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
            printLog(it.stringValue)

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

//region DOCUMENT RECOGNITION

    private fun documentRecognition() {
        // Create a language set.
        val languageList: MutableList<String> = ArrayList()
        languageList.add("tr")
        languageList.add("en")

        // Set parameters.
        val setting = MLDocumentSetting.Factory()
            // Specify the languages that can be recognized, which should comply with ISO 639-1.
            .setLanguageList(languageList)

            // Set the format of the returned text border box.
            // MLRemoteTextSetting.NGON: Return the coordinates of the four corner points of the quadrilateral.
            // MLRemoteTextSetting.ARC: Return the corner points of a polygon border in an arc. The coordinates of up to 72 corner points can be returned.
            .setBorderType(MLRemoteTextSetting.ARC)
            .create()

        val analyzer = MLAnalyzerFactory.getInstance().getRemoteDocumentAnalyzer(setting)

        // Create an MLFrame object using the bitmap, which is the image data in bitmap format.
        val documentId =
            if (Random.nextBoolean()) R.drawable.document_recognition else R.drawable.einstein
        mBinding.imgDocumentRecognition.setImageResource(documentId)
        val bitmap = BitmapFactory.decodeResource(resources, documentId)
        val frame = MLFrame.fromBitmap(bitmap)
        val task = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener {
            // Recognition success.
            printLog(it.stringValue)

            //After the recognition is complete, stop the analyzer to release recognition resources
            try {
                analyzer.stop()
            } catch (e: IOException) {
                // Exception handling.
                printLog("addOnSuccessListener,analyzer.stop() CATCH  \n ${e.localizedMessage}")
            }
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
                printLog("addOnFailureListener, analyzer.stop() CATCH  \n ${e.localizedMessage}")
            }
        }
    }

//endregion

    private fun setListeners() {
        mBinding.btnTextRecognition.setOnClickListener {
//            localTextRecognition()
            cloudTextRecognition()
        }

        mBinding.btnLiveRecognition.setOnClickListener {
            startActivity<LiveTextRecognition>()
        }

        mBinding.btnDocumentRecognition.setOnClickListener {
            documentRecognition()
        }

    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("\n\n$log")
        Log.i(TAG, log)
    }
}