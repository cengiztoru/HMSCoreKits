package com.cengiztoru.hmscorekits.ui.ml_kit

import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitImageRelatedBinding
import com.cengiztoru.hmscorekits.utils.extensions.getRandomItem
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.dsc.*
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationScene
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting


class ImageRelatedRecognition : AppCompatActivity() {

    companion object {
        private const val TAG = "ImageRelatedRecognition"
    }

    private lateinit var mBinding: ActivityMlkitImageRelatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMlkitImageRelatedBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setListeners()
    }
//region IMAGE SEGMENTATION

    private fun imageSegmentation(imageId: Int) {
        mBinding.imgBeforeImageSegmentation.setImageResource(imageId)
        mBinding.imgAfterImageSegmentation.setImageResource(R.drawable.ic_launcher_foreground)

        val setting = MLImageSegmentationSetting.Factory()
            // Set whether to support fine segmentation. The value true indicates fine segmentation, and the value false indicates fast segmentation.
            .setExact(true)
            // Set the segmentation mode to human body segmentation.
            .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
            // Set returned result types.
            // MLImageSegmentationScene.ALL: All segmentation results are returned (pixel-level label information, human body image with a transparent background, gray-scale image with a white human body and black background, and an original image for segmentation).
            // MLImageSegmentationScene.MASK_ONLY: Only pixel-level label information and an original image for segmentation are returned.
            // MLImageSegmentationScene.FOREGROUND_ONLY: A human body image with a transparent background and an original image for segmentation are returned.
            // MLImageSegmentationScene.GRAYSCALE_ONLY: A gray-scale image with a white human body and black background and an original image for segmentation are returned.
            .setScene(MLImageSegmentationScene.FOREGROUND_ONLY)
            .create()
        val analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting)

//        val analyzer = MLAnalyzerFactory.getInstance().imageSegmentationAnalyzer
        val bitmap = BitmapFactory.decodeResource(resources, imageId)
        val frame = MLFrame.fromBitmap(bitmap)

        // Create a task to process the result returned by the image segmentation analyzer.
        val task: Task<MLImageSegmentation> = analyzer.asyncAnalyseFrame(frame)
        // Asynchronously process the result returned by the image segmentation analyzer.
        task.addOnSuccessListener {
            // Detection success.
            mBinding.imgAfterImageSegmentation.setImageBitmap(it.foreground)
            analyzer.stop()
        }.addOnFailureListener {
            printLog("Detection failure. Message : ${it.localizedMessage}, Cause: ${it.cause}")
            analyzer.stop()
        }
    }

//endregion

//region DOCUMENT SKEW CORRECTION

    private fun startSkewCorrection(resourceId: Int) {
        mBinding.imgBeforeImageSegmentation.setImageResource(resourceId)
        mBinding.imgAfterImageSegmentation.setImageResource(R.drawable.ic_launcher_foreground)

        val setting = MLDocumentSkewCorrectionAnalyzerSetting.Factory().create()
        val analyzer = MLDocumentSkewCorrectionAnalyzerFactory.getInstance()
            .getDocumentSkewCorrectionAnalyzer(setting)

        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        val frame = MLFrame.fromBitmap(bitmap)

        val task: Task<MLDocumentSkewDetectResult> = analyzer.asyncDocumentSkewDetect(frame)
        task.addOnSuccessListener { detectResult ->
            // Coordinates of the document on the picture were determined.
            getCorrectedDocumentImage(analyzer, detectResult, frame)
        }.addOnFailureListener {
            // Coordinates of document detection failure.
            printLog("Coordinates of document detection failure. Message : ${it.localizedMessage}, Cause: ${it.cause}")
        }
    }

    private fun getCorrectedDocumentImage(
        analyzer: MLDocumentSkewCorrectionAnalyzer,
        detectResult: MLDocumentSkewDetectResult,
        frame: MLFrame
    ) {
        val leftTop: Point = detectResult.leftTopPosition
        val rightTop: Point = detectResult.rightTopPosition
        val leftBottom: Point = detectResult.leftBottomPosition
        val rightBottom: Point = detectResult.rightBottomPosition
        val coordinates: MutableList<Point> = mutableListOf()
        coordinates.add(leftTop)
        coordinates.add(rightTop)
        coordinates.add(rightBottom)
        coordinates.add(leftBottom)
        val coordinateData = MLDocumentSkewCorrectionCoordinateInput(coordinates)

        val correctionTask = analyzer.asyncDocumentSkewCorrect(frame, coordinateData)
        correctionTask.addOnSuccessListener {
            // Detection success.
            mBinding.imgAfterImageSegmentation.setImageBitmap(it.corrected)
            analyzer.stop()
        }.addOnFailureListener {
            // Detection failure.
            printLog("asyncDocumentSkewCorrect failure. Message : ${it.localizedMessage}, Cause: ${it.cause}")
            analyzer.stop()
        }
    }

//endregion

    /********** COMMON FUNCTIONS ******** **/
    private fun setListeners() {
        mBinding.btnImageSegmentation.setOnClickListener {
            val resourceId = getRandomItem(
                listOf(
                    R.drawable.image_segmentation_1,
                    R.drawable.image_segmentation_2,
                    R.drawable.image_segmentation_3
                )
            ) ?: R.drawable.image_segmentation_1

            imageSegmentation(resourceId)
        }

        mBinding.btnDocumentSkewCorrection.setOnClickListener {
            val resourceId = getRandomItem(
                listOf(
                    R.drawable.doc_skew_correction_1,
                    R.drawable.doc_skew_correction_2,
                    R.drawable.doc_skew_correction_3
                )
            ) ?: R.drawable.doc_skew_correction_3

            startSkewCorrection(resourceId)
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
        Log.d(TAG, log)
    }
}