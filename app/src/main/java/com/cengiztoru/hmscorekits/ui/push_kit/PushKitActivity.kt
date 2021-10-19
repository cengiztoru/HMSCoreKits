package com.cengiztoru.hmscorekits.ui.push_kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityPushKitBinding
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException

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
            mBinding.tvToken.text = message
        }
    }

//endregion
}