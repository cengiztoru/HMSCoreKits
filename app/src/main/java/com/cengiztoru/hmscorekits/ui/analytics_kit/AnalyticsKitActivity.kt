package com.cengiztoru.hmscorekits.ui.analytics_kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityAnalyticsBinding
import com.huawei.agconnect.common.network.AccessNetworkManager
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import com.huawei.hms.analytics.type.HAEventType.ADDPRODUCT2CART
import com.huawei.hms.analytics.type.HAEventType.REGISTERACCOUNT
import com.huawei.hms.analytics.type.HAParamType
import kotlin.random.Random

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

    private fun clearCacheData() {
        Log.i(TAG, " Started to Clearing Caching Data")
        analyticsInstance.clearCachedData()
    }

    private fun sendAnalyticEvent(key: String, bundle: Bundle) {
        analyticsInstance.onEvent(key, bundle)
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
        mBinding.btnUserProfileEvent.setOnClickListener {
            setUserProfile("favorite_sport", mBinding.etFavoriteSport.text.toString())
        }

        mBinding.btnAllowNetworkAccess.setOnClickListener {
            allowNetworkAccess()
        }

        mBinding.btnClearData.setOnClickListener {
            clearCacheData()
        }

//region PREDEFINED EVENT EXAMPLE

//      PREDEFINED EVENT TYPE LIST
//      https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/android-api-haeventtype-0000001051318956

//      PREDEFINED PARAMETER TYPE LIST
//      https://developer.huawei.com/consumer/en/doc/development/HMSCore-References/android-api-haparamtype-0000001051319011

        mBinding.btnAddToCart.setOnClickListener {

            val addToCartBundle = Bundle().apply {
                putString(HAParamType.PRODUCTID, "11102021")
                putString(HAParamType.PRODUCTNAME, "HUAWEI MateBook X Pro 2021")
                putString(HAParamType.CATEGORY, "laptops")
                putLong(HAParamType.QUANTITY, 1)
                putDouble(HAParamType.PRICE, 1299.99)
                putString(HAParamType.CURRNAME, "EUR")
                putString(HAParamType.PLACEID, "UK")

            }

            sendAnalyticEvent(ADDPRODUCT2CART, addToCartBundle)

        }

        mBinding.btnRegister.setOnClickListener {

            val registerBundle = Bundle().apply {
                putString(HAParamType.USERID, "11102021")
                putString(HAParamType.NAME, "Cengiz TORU")
                putString(HAParamType.USERTYPE, "blablabla")
                putString(HAParamType.USERGROUPNAME, "blablabla")
            }

            sendAnalyticEvent(REGISTERACCOUNT, registerBundle)

        }

        var score = 0

        mBinding.btnSubmitScore.setOnClickListener {

            score += Random.nextInt(0, 100)

            sendAnalyticEvent(REGISTERACCOUNT,
                Bundle().apply {
                    putInt(HAParamType.SCORE, score)
                }
            )

            mBinding.btnSubmitScore.text = "Submit Score ($score)"
        }

//endregion

    }


//endregion
}