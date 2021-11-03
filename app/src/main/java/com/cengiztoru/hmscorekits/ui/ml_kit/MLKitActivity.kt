package com.cengiztoru.hmscorekits.ui.ml_kit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitBinding
import com.cengiztoru.hmscorekits.utils.extensions.Constants
import com.cengiztoru.hmscorekits.utils.extensions.startActivity
import com.huawei.hms.mlsdk.common.MLApplication

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


}