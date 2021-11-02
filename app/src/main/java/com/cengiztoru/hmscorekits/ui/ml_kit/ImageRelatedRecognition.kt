package com.cengiztoru.hmscorekits.ui.ml_kit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitImageRelatedBinding
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationScene
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting
import kotlin.random.Random


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

    private fun setListeners() {
        mBinding.btnImageSegmentation.setOnClickListener {
            val imageId = when (Random.nextInt(100) % 3) {
                1 -> {
                    R.drawable.image_segmentation_1
                }
                2 -> {
                    R.drawable.image_segmentation_2
                }
                else -> {
                    R.drawable.image_segmentation_3
                }
            }
            imageSegmentation(imageId)
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
        Log.d(TAG, log)
    }
}