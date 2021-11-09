package com.cengiztoru.hmscorekits.ui.ads_kit

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityAdsSplashBinding
import com.cengiztoru.hmscorekits.utils.extensions.hideStatusBar
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.AudioFocusType
import com.huawei.hms.ads.splash.SplashAdDisplayListener
import com.huawei.hms.ads.splash.SplashView
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SplashAdActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashAdActivity"
    }

    private lateinit var mBinding: ActivityAdsSplashBinding

    private val IMAGE_AD_ID = "testq6zq98hecj"
    private val VIDEO_AD_ID = "testd7c5cewoj6"
    private val AD_ID = if (Random.nextInt(0, 100) % 2 == 0) IMAGE_AD_ID else VIDEO_AD_ID

    private val AD_TIMEOUT = 10L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdsSplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        hideStatusBar()

        setAdListener()
        initSplashAd()
        loadAd()

    }

    private fun initSplashAd() {
        // Obtain SplashView.
        // Set the default slogan.
        mBinding.hwSplashView.setSloganResId(R.mipmap.ic_launcher)

        // Set the audio focus type for a video splash ad.
        mBinding.hwSplashView.audioFocusType = AudioFocusType.NOT_GAIN_AUDIO_FOCUS_WHEN_MUTE

    }

    private fun setAdListener() {
        val adDisplayListener: SplashAdDisplayListener = object : SplashAdDisplayListener() {
            override fun onAdShowed() {
                // Called when an ad is displayed.
                Log.i(TAG, "onAdShowed")
            }

            override fun onAdClick() {
                // Called when an ad is clicked.
                Log.i(TAG, "onAdClick")
            }
        }
        mBinding.hwSplashView.setAdDisplayListener(adDisplayListener)
    }

    private fun loadAd() {

        val splashAdLoadListener: SplashView.SplashAdLoadListener =
            object : SplashView.SplashAdLoadListener() {
                override fun onAdLoaded() {
                    // Called when an ad is loaded successfully.
                    Log.i(TAG, "onAdLoaded")
                }


                override fun onAdFailedToLoad(errorCode: Int) {
                    // Called when an ad fails to be loaded. The app home screen is then displayed.
                    Log.i(TAG, "onAdFailedToLoad")
                    showToast("Ad Cant Load")
                    goNextScreen()
                }

                override fun onAdDismissed() {
                    // Called when the display of an ad is complete. The app home screen is then displayed.
                    Log.i(TAG, "onAdDismissed")
                    goNextScreen()
                }
            }

        val orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val adParam = AdParam.Builder().build()
        // Load the ad. AD_ID indicates the ad unit ID.
        mBinding.hwSplashView.load(AD_ID, orientation, adParam, splashAdLoadListener)

        setTimeOut()

    }

    private fun setTimeOut() {
        // Create an executor that executes tasks in a background thread.
        val timeOutExecutor: ScheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor()

        timeOutExecutor.schedule({
            if (hasWindowFocus()) {
                goNextScreen()
            }
        }, AD_TIMEOUT, TimeUnit.SECONDS)

    }

    private fun goNextScreen() {
        // You should show your next / home screen
        // startActivity<MainActivity>()

        onBackPressed()     //it is just for this repo
    }
}