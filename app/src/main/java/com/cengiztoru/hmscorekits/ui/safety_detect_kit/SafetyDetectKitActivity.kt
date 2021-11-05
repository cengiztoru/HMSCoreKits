package com.cengiztoru.hmscorekits.ui.safety_detect_kit

import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivitySafetyDetectKitBinding
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

class SafetyDetectKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SafetyDetectKit"
        private const val APP_ID = ""   //todo add your app id to here

    }

    private lateinit var mBinding: ActivitySafetyDetectKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySafetyDetectKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setListeners()
    }

//region  SYSTEM INTEGRITY CHECK

    private fun invokeSysIntegrity() {
        if (APP_ID.isNullOrBlank()) {
            printLog("PLEASE ADD YOUR APP ID")
            return
        }
        val nonce = ByteArray(24)
        try {
            val random: SecureRandom = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SecureRandom.getInstanceStrong()
            } else {
                SecureRandom.getInstance("SHA1PRNG")
            }
            random.nextBytes(nonce)
        } catch (e: NoSuchAlgorithmException) {
            printLog("NoSuchAlgorithm. Message:${e.message}")
        }

        SafetyDetect.getClient(this)
            .sysIntegrity(nonce, APP_ID)
            .addOnSuccessListener { response -> // Indicates communication with the service was successful.
                // Use response.getResult() to get the result data.
                val jwsStr = response.result
                // Process the result data here
                val jwsSplit = jwsStr.split(".").toTypedArray()
                val jwsPayloadStr = jwsSplit[1]
                val payloadDetail = String(
                    Base64.decode(
                        jwsPayloadStr.toByteArray(StandardCharsets.UTF_8),
                        Base64.URL_SAFE
                    ), StandardCharsets.UTF_8
                )
                try {
                    val jsonObject = JSONObject(payloadDetail)
                    printLog("JSON OBJECT $jsonObject")

                    val isBasicIntegrity = jsonObject.getBoolean("basicIntegrity")
                    printLog("Basic Integrity: $isBasicIntegrity")
                    if (!isBasicIntegrity) {
                        val advice = "Advice: " + jsonObject.getString("advice")
                        printLog(advice)
                    }

                    val apkCertificateDigestSha256 =
                        jsonObject.getString("apkCertificateDigestSha256")
                    val apkDigestSha256 = jsonObject.getString("apkDigestSha256")
                    val apkPackageName = jsonObject.getString("apkPackageName")
                    val appId = jsonObject.getString("appId")
                    val timestampMs = jsonObject.getString("timestampMs")
                    val resultNonce = jsonObject.getString("nonce")


                } catch (e: JSONException) {
                    val errorMsg = e.message
                    printLog(errorMsg ?: "unknown error")
                }
            }
            .addOnFailureListener { e -> // There was an error communicating with the service.
                val errorMsg: String? = if (e is ApiException) {
                    // An error with the HMS API contains some additional details.
                    SafetyDetectStatusCodes.getStatusCodeString(e.statusCode) + ": " + e.message
                    // You can use the apiException.getStatusCode() method to get the status code.
                } else {
                    // unknown type of error has occurred.
                    e.message
                }
                printLog(errorMsg ?: "")
            }
    }

//endregion


    private fun setListeners() {
        mBinding.btnSystemIntegrity.setOnClickListener {
            invokeSysIntegrity()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
        Log.e(TAG, log)
    }
}