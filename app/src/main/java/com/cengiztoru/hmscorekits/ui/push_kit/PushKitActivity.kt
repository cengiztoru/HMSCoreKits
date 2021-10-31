package com.cengiztoru.hmscorekits.ui.push_kit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityPushKitBinding
import com.cengiztoru.hmscorekits.utils.extensions.startActivity
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.push.HmsMessaging

// Obtain the app ID from the agconnect-service.json file.
private const val APP_ID = "104846955"
private const val TAG = "PushKitActivity"

class PushKitActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPushKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateViews()
        setListeners()
        obtainPushToken()
        getIntentData(intent)
        displayingPushOperations()
    }


    private fun obtainPushToken() {
        object : Thread() {
            override fun run() {
                try {
                    // Set tokenScope to HCM.
                    val tokenScope = "HCM"
                    val token =
                        HmsInstanceId.getInstance(this@PushKitActivity).getToken(APP_ID, tokenScope)
                    Log.i(TAG, "get token:$token")

                    printMessage("Token:$token")

                } catch (e: ApiException) {
                    printMessage("Getting token failed ${e.message}")
                    Log.e(TAG, "get token failed, $e")
                }
            }
        }.start()
    }

    private fun turnOffNotifications() {
        object : Thread() {
            override fun run() {
                try {
                    // Set tokenScope to HCM.
                    val tokenScope = "HCM"

                    // Delete the token.
                    HmsInstanceId.getInstance(this@PushKitActivity).deleteToken(APP_ID, tokenScope)
                    printMessage("token deleted successfully")
                    Log.i(TAG, "token deleted successfully")
                } catch (e: ApiException) {
                    printMessage("delete token failed, ${e.message}")
                    Log.e(TAG, "delete token failed, $e")
                }
            }
        }.start()
    }


//region DEEPLINK

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        getIntentData(intent)
    }

    private fun getIntentData(intent: Intent?) {
        intent?.extras?.let { extras ->
            printMessage("CHECKING BUNDLE EXTRAS")
            extras.getString("page")?.apply {
                when (this) {
                    "product_detail" -> {
                        extras.getString("link")?.let { link ->
                            startActivity<ProductDetailActivity> {
                                putExtra("link", link)
                                putExtra("title", extras.getString("title"))

//                              .....
//                              .....
//                              .....
                            }
                        }
                    }
                    "cart" -> {
                        printMessage("OPEN CART SCREEN")
                    }
                    "profile" -> {
                        printMessage("OPEN PROFILE SCREEN")
                    }
                    "product_list" -> {
                        printMessage("OPEN PRODUCT LIST SCREEN")
                    }
                    "notifications" -> {
                        printMessage("OPEN NOTIFICATIONS SCREEN")
                    }
                    "info" -> {
                        printMessage("OPEN WEBVIEW SCREEN FOR INFORMATION")
                    }
                    else -> {
                        printMessage("UNEXPECTED PAGE VALUE")
                    }
                }
            }
        } ?: run {
            printMessage("INTENT NULL. NO ANY DEEPLINK")
        }
    }

//endregion


//region STATUS OF DISPLAYING NOTIFICATIONS

    private fun displayingPushOperations() {
        if (isMessagesDisplaying().not()) {
            turnOnMessageDisplaying()
        }
    }

    private fun isMessagesDisplaying(): Boolean {
        val isDisplaying = HmsMessaging.getInstance(this).isAutoInitEnabled
        printMessage("IS NOTIFICATION MESSAGES DISPLAYING $isDisplaying")
        return isDisplaying
    }

    private fun turnOffMessageDisplaying() {
        // Disable displaying notification messages.
        HmsMessaging.getInstance(this).turnOffPush().addOnCompleteListener { task ->
            // Obtain the result.
            if (task.isSuccessful) {
                printMessage("TurnOffPush successfully.")
                isMessagesDisplaying()
            } else {
                printMessage("IurnOffPush failed.")
            }
        }
    }

    private fun turnOnMessageDisplaying() {
        // Enable displaying notification messages.
        HmsMessaging.getInstance(this).turnOnPush().addOnCompleteListener { task ->
            // Obtain the result.
            if (task.isSuccessful) {
                printMessage("TurnOnPush successfully.")
            } else {
                printMessage("IurnOnPush failed.")
            }
        }
    }

//


//region common functions

    private fun setListeners() {
        mBinding.btnTurnOffNotifications.setOnClickListener {
            turnOffNotifications()
        }
    }

    private fun inflateViews() {
        mBinding = ActivityPushKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    private fun printMessage(message: String) {
        runOnUiThread {
            mBinding.tvToken.append("\n\n$message")
        }
    }

//endregion
}