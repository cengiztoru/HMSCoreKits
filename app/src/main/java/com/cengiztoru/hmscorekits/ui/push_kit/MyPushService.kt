package com.cengiztoru.hmscorekits.ui.push_kit

import android.os.Bundle
import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

private const val TAG = "HMSCorePushKit"

class MyPushService : HmsMessageService() {
    override fun onNewToken(token: String?, bundle: Bundle?) {
        Log.i(TAG, "have received refresh token:$token")
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        Log.i(TAG, "onMessageReceived")
    }
}