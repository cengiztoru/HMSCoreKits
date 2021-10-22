package com.cengiztoru.hmscorekits.ui.ads_kit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityAdsBannerBinding
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView

class BannerAdsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BannerAdsActivity"
    }

    private lateinit var mBinding: ActivityAdsBannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdsBannerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

//      Initialize Kit
        HwAds.init(this)

        val adParam = AdParam.Builder().build()
        mBinding.hwBannerView.loadAd(adParam)
        mBinding.hwBannerView.setListener()


//      Adding A Banner View Programatically
        val topBannerView = BannerView(this)
        topBannerView.adId = "testw6vs28auh3"    //todo add your ad id here
        topBannerView.bannerAdSize = BannerAdSize.BANNER_SIZE_SMART
        topBannerView.loadAd(adParam)
        //topBannerView.setListener()
        mBinding.rootView.addView(topBannerView)

    }

    private fun BannerView.setListener() {
        adListener = object : AdListener() {

            override fun onAdLoaded() {
                // Called when an ad is loaded successfully.
                Log.i(TAG, "onAdLoaded")
                showToast("Ad Loaded")

            }

            override fun onAdFailed(p0: Int) {
                // Called when an ad fails to be loaded.
                Log.i(TAG, "onAdFailed")
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

}