package com.cengiztoru.hmscorekits.ui.analytics_kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityAnalyticsBinding
import com.huawei.agconnect.common.network.AccessNetworkManager
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools

private const val TAG = "AnalyticsKitActivity"

class AnalyticsKitActivity : AppCompatActivity() {

    private lateinit var analyticsInstance: HiAnalyticsInstance
    private lateinit var mBinding: ActivityAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        setListeners()

    }

//region HMS Analytics Kit Functions

    private fun setUserProfile(key: String, value: String) {
        Log.i(TAG, "set user profile $key : #$value")
        analyticsInstance.setUserProfile(key, value)
    }


    private fun allowNetworkAccess() {
        Log.i(TAG, "Allowing Internet Access Started")
        AccessNetworkManager.getInstance().setAccessNetwork(true)
    }

//endregion

    //region common functions
    private fun init() {
        mBinding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // During SDK initialization, a network request will be sent. You are advised to initialize the SDK after users agree to the privacy agreement.
        allowNetworkAccess()

        // Enable Analytics Kit logging.
        // Initialize Analytics Kit in the main thread by using the onCreate() method of the first app activity.
        HiAnalyticsTools.enableLog()
        // Generate an Analytics Kit instance.
        analyticsInstance = HiAnalytics.getInstance(this)
    }

    private fun setListeners() {
        mBinding.btnPushEvent.setOnClickListener {
            setUserProfile("favorite_sport", mBinding.etFavoriteSport.text.toString())
        }

        mBinding.btnAllowNetworkAccess.setOnClickListener {
            allowNetworkAccess()
        }
    }


//endregion
}