package com.cengiztoru.hmscorekits.ui.ads_kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityAdsInterstitialBinding
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.InterstitialAd

class InterstitialAdActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAdsInterstitialBinding

    private var interstitialAd: InterstitialAd? = null

    companion object {
        private const val TAG = "InterstitialAdActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdsInterstitialBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        interstitialAd = InterstitialAd(this)

        setListeners()
//        loadInterstitialAd()      //Of course you can load ad at first moment on activity opened

    }

    private fun loadInterstitialAd(loadImageAd: Boolean = true) {
        interstitialAd?.adId = if (loadImageAd) "teste9ih9j0rc3" else "testb4znbuh3n2"

        val adParam = AdParam.Builder().build()
        interstitialAd?.loadAd(adParam)

        //Listener
        interstitialAd?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Called when an ad is loaded successfully.
                Log.i(TAG, "onAdLoaded")
                showInterstitialAd(loadImageAd)
            }

            override fun onAdFailed(errorCode: Int) {
                // Called when an ad fails to be loaded.
                Log.i(TAG, "onAdFailed errorCode : $errorCode")
                showToast("Loading Add Failed")
            }

            override fun onAdOpened() {
                // Called when an ad is opened.
                Log.i(TAG, "onAdOpened")

            }

            override fun onAdClicked() {
                // Called when an ad is clicked.
                Log.i(TAG, "onAdClicked")
            }

            override fun onAdLeave() {
                // Called when an ad leaves an app.
                Log.i(TAG, "onAdLeave")
            }

            override fun onAdClosed() {
                // Called when an ad is closed.
                Log.i(TAG, "onAdClosed")
            }
        }

    }

    private fun showInterstitialAd(loadImageAd: Boolean = true) {
        if (interstitialAd != null && interstitialAd?.isLoaded == true) {
            interstitialAd?.show()
        } else {
            loadInterstitialAd(loadImageAd)
        }
    }

    private fun setListeners() {
        mBinding.btnImageAd.setOnClickListener {
            showInterstitialAd()
        }

        mBinding.btnVideoAd.setOnClickListener {
            showInterstitialAd(false)
        }

    }

}