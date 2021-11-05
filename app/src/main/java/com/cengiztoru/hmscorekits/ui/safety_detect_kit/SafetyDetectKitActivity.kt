package com.cengiztoru.hmscorekits.ui.safety_detect_kit

import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivitySafetyDetectKitBinding
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.api.CommonStatusCodes
import com.huawei.hms.support.api.entity.core.CommonCode
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsData
import com.huawei.hms.support.api.entity.safetydetect.UrlCheckThreat
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient
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

    private lateinit var safeDetectClient: SafetyDetectClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySafetyDetectKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        safeDetectClient = SafetyDetect.getClient(this)

        checkAppId()
        setListeners()
    }

//region  SYSTEM INTEGRITY CHECK

    private fun invokeSysIntegrity() {
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

        safeDetectClient
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

//region MALICIOUS APP DETECTION

    private fun getMaliciousApps() {
        safeDetectClient
            .maliciousAppsList
            .addOnSuccessListener { maliciousAppsListResp ->
                val appsDataList: List<MaliciousAppsData> = maliciousAppsListResp.maliciousAppsList
                if (maliciousAppsListResp.rtnCode == CommonCode.OK) {
                    if (appsDataList.isEmpty()) {
                        printLog("No known potentially malicious apps are installed.")
                    } else {
                        appsDataList.forEach { maliciousApp ->
                            printLog(
                                "Information about a malicious app\n  " +
                                        "APK: ${maliciousApp.apkPackageName} " +
                                        "SHA-256: ${maliciousApp.apkSha256}  " +
                                        "Category: ${maliciousApp.apkCategory}"
                            )
                        }
                    }
                } else {
                    printLog("Get malicious apps list failed! Message: " + maliciousAppsListResp.errorReason)
                }
            }
            .addOnFailureListener { e -> // There was an error communicating with the service.
                val errorMsg: String? = if (e is ApiException) {
                    // An error with the HMS API contains some additional details.
                    SafetyDetectStatusCodes.getStatusCodeString(e.statusCode) + ": " + e.message
                    // You can use the apiException.getStatusCode() method to get the status code.
                } else {
                    // Unknown type of error has occurred.
                    e.message
                }
                printLog("Get malicious apps list failed! Message: $errorMsg")
            }
    }

//endregion

//region URL CHECKING

    private fun urlChecking(url: String) {
        safeDetectClient.initUrlCheck()
        safeDetectClient.urlCheck(
            url,
            APP_ID,
            // Specify url threat type
            UrlCheckThreat.MALWARE,
            UrlCheckThreat.PHISHING
        ).addOnSuccessListener {
            if (it.urlCheckResponse.isEmpty()) {
                // No threat exists.
                printLog("NO ANY THREAT DETECTED")
            } else {
                // Threats exist.
                it.urlCheckResponse.forEach { threat ->
                    printLog(threat.urlCheckResult.toString())
                }
            }
            shutdownTaskListener(safeDetectClient.shutdownUrlCheck(), "shutdownUrlCheck")
        }.addOnFailureListener {
            // An error occurred during communication with the service.
            if (it is ApiException) {
                // HMS Core (APK) error code and corresponding error description.
                val apiException = it
                printLog(
                    "Url Checking Failure. Error: " + CommonStatusCodes.getStatusCodeString(
                        apiException.statusCode
                    )
                )
                // Note: If the status code is SafetyDetectStatusCode.CHECK_WITHOUT_INIT,
                // you did not call the initUrlCheck() method or you have initiated a URL check request before the call is completed.
                // If an internal error occurs during the initialization, you need to call the initUrlCheck() method again to initialize the API.
            } else {
                // An unknown exception occurs.
                printLog("Url Checking Failure. Error: " + it.message)
            }
            shutdownTaskListener(safeDetectClient.shutdownUrlCheck(), "shutdownUrlCheck")
        }
    }

//endregion

//region FAKE USER DETECTION

    private fun initFakeUserDetect() {
        // Replace with your activity or context as a parameter.
        safeDetectClient.initUserDetect().addOnSuccessListener {
            // Indicates communication with the service was successful.
            getUserDetectResponseToken()
        }.addOnFailureListener {
            // There was an error communicating with the service.
            printLog("Fake User Detect initialization failed. Message: ${it.localizedMessage}")
        }
    }


    private fun getUserDetectResponseToken() {
        safeDetectClient.userDetection(APP_ID)
            .addOnSuccessListener { userDetectResponse ->
                // Indicates communication with the service was successful.
                val responseToken = userDetectResponse.responseToken
                if (responseToken.isNullOrBlank().not()) {
                    printLog("FakeUserDetection Token obtained : $responseToken")
                    // Send the response token to your app server, and call the cloud API of HMS Core on your server to obtain the fake user detection result.
                    shutdownTaskListener(
                        safeDetectClient.shutdownUserDetect(),
                        "shutdownUserDetect"
                    )
                }
            }
            .addOnFailureListener {  // There was an error communicating with the service.
                val errorMsg: String? = if (it is ApiException) {
                    // An error with the HMS API contains some additional details.
                    // You can use the apiException.getStatusCode() method to get the status code.
                    (SafetyDetectStatusCodes.getStatusCodeString(it.statusCode) + ": "
                            + it.message)
                } else {
                    // Unknown type of error has occurred.
                    it.message
                }
                printLog("User detection fail. Error info: $errorMsg")
                shutdownTaskListener(safeDetectClient.shutdownUserDetect(), "shutdownUserDetect")
            }
    }

//endregion

    private fun shutdownTaskListener(task: Task<Void>, taskTag: String) {
        task.addOnSuccessListener {
            // Indicates communication with the service was successful.
            printLog("$taskTag task is sucess")
        }.addOnFailureListener {
            // There was an error communicating with the service.
            printLog("$taskTag is failured. Exception message: ${it.message}")
        }
    }

    private fun checkAppId() {
        if (APP_ID.isBlank()) {
            printLog("PLEASE ADD YOUR APP ID")
            return
        }
    }

    private fun setListeners() {
        mBinding.btnSystemIntegrity.setOnClickListener {
            invokeSysIntegrity()
        }

        mBinding.btnMaliciousApps.setOnClickListener {
            getMaliciousApps()
        }

        mBinding.btnUrlChecking.setOnClickListener {
            val url = mBinding.etUrl.text.toString().lowercase()
            if (url.isNullOrBlank()) {
                mBinding.tilUrl.error = "Please enter an url"
                return@setOnClickListener
            }
            urlChecking(url)
        }

        mBinding.btnFakeUserDetection.setOnClickListener {
            initFakeUserDetect()
        }
    }

    private fun printLog(log: String) {
        mBinding.tvLogger.append("$log\n\n")
        Log.e(TAG, log)
    }
}