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
import com.huawei.hms.mlsdk.tts.*
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

    //region TEXT TRANSLATION
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
//endregion

    //region LANGUAGE OF TEXT DETECTION
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
//endregion

    //region TEXT TO SPEECH
    private var mlTtsEngine: MLTtsEngine? = null
    private fun textToSpeech(sourceText: String) {
        closeMLTtsEngine()
        printLog("START READING TO ''$sourceText''")
        // Use customized parameter settings to create a TTS engine.
        val mlTtsConfig: MLTtsConfig =
            MLTtsConfig() // Set the text converted from speech to Chinese.
                .setLanguage(MLTtsConstants.TTS_EN_US)
                // Set the English timbre.
                .setPerson(MLTtsConstants.TTS_SPEAKER_MALE_EN)
                // Set the speech speed. The range is (0,5.0]. 1.0 indicates a normal speed.
                .setSpeed(1.0f)
                // Set the volume. The range is (0,2). 1.0 indicates a normal volume.
                .setVolume(1.0f)

        mlTtsEngine = MLTtsEngine(mlTtsConfig)
        // Set the volume of the built-in player, in dBs. The value is in the range of [0, 100].
        mlTtsEngine?.setPlayerVolume(30)

        // Update the configuration when the engine is running.
        mlTtsEngine?.updateConfig(mlTtsConfig)

        val callback: MLTtsCallback = object : MLTtsCallback {
            override fun onError(taskId: String, err: MLTtsError) {
                // Processing logic for TTS failure.
                printLog("onError ${err.errorMsg}")
            }

            override fun onWarn(taskId: String, warn: MLTtsWarn) {
                // Alarm handling without affecting service logic.
                printLog("onWarn")
            }

            // Return the mapping between the currently played segment and text. start: start position of the audio segment in the input text; end (excluded): end position of the audio segment in the input text.
            override fun onRangeStart(taskId: String, start: Int, end: Int) {
                // Process the mapping between the currently played segment and text.
                printLog("onRangeStart")
            }

            override fun onAudioAvailable(
                p0: String?,
                p1: MLTtsAudioFragment?,
                p2: Int,
                p3: android.util.Pair<Int, Int>?,
                p4: Bundle?
            ) {
                printLog("onAudioAvailable")
            }

            override fun onEvent(taskId: String, eventId: Int, bundle: Bundle?) {
                // Callback method of a TTS event. eventId indicates the event name.
                when (eventId) {
                    MLTtsConstants.EVENT_PLAY_START -> {
                        printLog("EVENT_PLAY_START")
                    }
                    MLTtsConstants.EVENT_PLAY_STOP -> {             // Called when playback stops.
                        printLog("EVENT_PLAY_STOP")
                        var isInterrupted =
                            bundle?.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED)
                    }
                    MLTtsConstants.EVENT_PLAY_RESUME -> {
                        printLog("EVENT_PLAY_RESUME")
                    }
                    MLTtsConstants.EVENT_PLAY_PAUSE -> {
                        printLog("EVENT_PLAY_PAUSE")
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_START -> {
                        printLog("EVENT_SYNTHESIS_START")
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_END -> {
                        printLog("EVENT_SYNTHESIS_END")
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_COMPLETE -> {
                        // TTS is complete. All synthesized audio streams are passed to the app.
                        var isInterrupted =
                            bundle?.getBoolean(MLTtsConstants.EVENT_SYNTHESIS_INTERRUPTED)
                        printLog("EVENT_SYNTHESIS_COMPLETE")
                        closeMLTtsEngine()
                    }
                    else -> {
                        printLog("else")
                    }
                }
            }
        }

        mlTtsEngine?.setTtsCallback(callback)
        val taskId = mlTtsEngine?.speak(sourceText, MLTtsEngine.QUEUE_APPEND)

    }

    private fun closeMLTtsEngine() {
        mlTtsEngine?.stop()
        mlTtsEngine?.shutdown()
    }

    override fun onResume() {
        super.onResume()
        mlTtsEngine?.resume()
    }

    override fun onPause() {
        super.onPause()
        mlTtsEngine?.pause()
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

        mBinding.btnTts.setOnClickListener {
            val text =
                "Text to speech can convert text information into audio output in real time. Rich timbres are provided and the volume and speed can be adjusted (5x adjustment is supported for Chinese and English), thereby natural voices can be produced. This service uses the deep neural network (DNN) synthesis mode and can be quickly integrated through the on-device SDK to generate audio data in real time. It supports the download of offline models."
            textToSpeech(text)
        }

    }


}