package com.cengiztoru.hmscorekits.ui.push_kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityPushKitBinding
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException

class PushKitActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPushKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateViews()
        obtainPushToken()
    }

    private fun inflateViews() {
        mBinding = ActivityPushKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    private fun obtainPushToken() {
        object : Thread() {
            override fun run() {
                try {
                    // Obtain the app ID from the agconnect-service.json file.
                    val appId = "104846955"

                    // Set tokenScope to HCM.
                    val tokenScope = "HCM"
                    val token =
                        HmsInstanceId.getInstance(this@PushKitActivity).getToken(appId, tokenScope)
                    Log.i("HMSCorePushKit", "get token:$token")

                    printMessage("Token:$token")

                } catch (e: ApiException) {
                    printMessage("Getting token failed ${e.message}")
                    Log.e("HMSCorePushKit", "get token failed, $e")
                }
            }
        }.start()
    }

    private fun printMessage(message: String) {
        runOnUiThread {
            mBinding.tvToken.text = message
        }
    }
}