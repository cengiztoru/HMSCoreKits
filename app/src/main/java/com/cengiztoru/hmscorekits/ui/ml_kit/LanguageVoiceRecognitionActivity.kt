package com.cengiztoru.hmscorekits.ui.ml_kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityMlkitLanguageVoiceRecognitionBinding
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator
import java.util.*
import kotlin.random.Random


class LanguageVoiceRecognitionActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LanguageVoiceRecog"
    }

    private lateinit var mBinding: ActivityMlkitLanguageVoiceRecognitionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMlkitLanguageVoiceRecognitionBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setListeners()
    }

    private fun translateText(sourceLanguage: String, targetLanguage: String, text: String) {
        // Create a text translator using custom parameter settings.
        val setting: MLRemoteTranslateSetting =
            MLRemoteTranslateSetting.Factory()
                // Set the source language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages. This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(sourceLanguage)
                // Set the target language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages.
                .setTargetLangCode(targetLanguage)
                .create()
        val mlRemoteTranslator: MLRemoteTranslator =
            MLTranslatorFactory.getInstance().getRemoteTranslator(setting)

        // sourceText: text to be translated, with up to 5000 characters.
        val task: Task<String> = mlRemoteTranslator.asyncTranslate(text)
        task.addOnSuccessListener {
            // Processing logic for recognition success.
            mBinding.tvTranslatedMessage.text = it
            mlRemoteTranslator.stop()
        }.addOnFailureListener { e ->
            // Processing logic for recognition failure.
            try {
                val mlException = e as MLException
                printLog("TEXT TRANSLATE ON CLOUD FAILURE. Message: ${mlException.message} Error Code : ${mlException.errCode}")
            } catch (error: Exception) {
                // Handle the conversion error.
                printLog("TEXT TRANSLATE ON CLOUD FAILURE. Message: ${error.localizedMessage}")
            }
        }
    }


    private fun detectLanguage(text: String) {
        printLog("DETECTION STARTED FOR ''$text'' ")
        val factory: MLLangDetectorFactory = MLLangDetectorFactory.getInstance()
        val setting: MLLocalLangDetectorSetting = MLLocalLangDetectorSetting.Factory()
            // Set the minimum confidence threshold for language detection.
            .setTrustedThreshold(0.01f)
            .create()
        val mlLocalLangDetector: MLLocalLangDetector = factory.getLocalLangDetector(setting)
        // Method 1: Return multiple language detection results, including the language codes and confidences. sourceText indicates the text (which is a string) to be detected, with up to 5000 characters.
        val probabilityDetectTask = mlLocalLangDetector.probabilityDetect(text)
        probabilityDetectTask.addOnSuccessListener {
            // Called when language detection is successful.
            var langsAndProbabilities = ""
            it.forEach { detectedLang ->
                langsAndProbabilities += "${detectedLang.langCode.uppercase()} : ${detectedLang.probability}\n"
            }
            printLog("$langsAndProbabilities")
            mlLocalLangDetector.stop()
        }.addOnFailureListener {
            // Called when language detection fails.
            printLog("DETECTING LANGUAGE FAILED : ${it.localizedMessage}")
            mlLocalLangDetector.stop()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
        Log.d(TAG, log)
    }


    private fun setListeners() {
        mBinding.btnTranslateText.setOnClickListener {
            val text = mBinding.etMessage.text.toString()

            if (text.trim().isBlank()) {
                mBinding.tilMessage.error = "Please fill in the box"
                return@setOnClickListener
            }
            mBinding.tilMessage.error = null
            translateText("tr", "en", text)
        }

        mBinding.btnDetectLang.setOnClickListener {
            val text = when (Random.nextInt(100) % 4) {
                0 -> {
                    "亡羊补牢，为时未晚"
                }
                1 -> {
                    "Sabrın sonu selamettir."
                }
                2 -> {
                    "LESS IS MORE"
                }
                else -> {
                    "Голь на вы́думку хитра́"
                }
            }
            detectLanguage(text)
        }
    }


}