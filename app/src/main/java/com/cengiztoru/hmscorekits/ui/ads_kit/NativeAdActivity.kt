package com.cengiztoru.hmscorekits.ui.ads_kit

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivityAdsNativeBinding
import com.cengiztoru.hmscorekits.utils.extensions.invisible
import com.cengiztoru.hmscorekits.utils.extensions.show
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.VideoOperator.VideoLifecycleListener
import com.huawei.hms.ads.nativead.*


class NativeAdActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAdsNativeBinding

    private var layoutId: Int = R.layout.template_ad_native_video

    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdsNativeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this)

        setListeners()
        loadAd(getAdId())

    }

    private fun getAdId() = when {
        mBinding.radioButtonSmall.isChecked -> {
            layoutId = R.layout.template_ad_native_small
            getString(R.string.ad_id_native_small)
        }
        mBinding.radioButtonVideo.isChecked -> {
            layoutId = R.layout.template_ad_native_video
            getString(R.string.ad_id_native_video)
        }
        else -> {
            layoutId = R.layout.template_ad_native_video
            getString(R.string.ad_id_native)
        }
    }

    private fun loadAd(adId: String) {
        updateStatus(false)

        val builder = NativeAdLoader.Builder(this, adId)

        builder.setNativeAdLoadedListener { nativeAd ->
            // Called when an ad is successfully loaded.
            updateStatus(true, getString(R.string.status_load_ad_success))

            // Display a native ad.
            showNativeAd(nativeAd)
            nativeAd.setDislikeAdListener { // Called when an ad is closed.
                updateStatus(true, getString(R.string.ad_is_closed))
            }
        }.setAdListener(object : AdListener() {
            override fun onAdFailed(errorCode: Int) {
                // Called when an ad fails to be loaded.
                updateStatus(true, getString(R.string.status_load_ad_fail) + errorCode)
            }
        })

        val adConfiguration = NativeAdConfiguration.Builder() // Set custom attributes.
            .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT)
            .build()

        val nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build()

        nativeAdLoader.loadAd(AdParam.Builder().build())

        updateStatus(false, getString(R.string.status_ad_loading))
    }


    private fun showNativeAd(nativeAdInstance: NativeAd) {
        // Destroy the original native ad.
        nativeAd?.apply { destroy() }
        nativeAd = nativeAdInstance

        // Create NativeView.
        val nativeView = layoutInflater.inflate(layoutId, null) as NativeView

        // Populate NativeView.
        initNativeAdView(nativeAd ?: nativeAdInstance, nativeView)

        // Add NativeView to the app UI.
        mBinding.svAdContent.removeAllViews()
        mBinding.svAdContent.addView(nativeView)
    }

    private fun initNativeAdView(nativeAd: NativeAd, nativeView: NativeView) {
        // Register a native ad asset view.
        nativeView.titleView = nativeView.findViewById(R.id.ad_title)
        nativeView.mediaView = nativeView.findViewById<View>(R.id.ad_media) as MediaView
        nativeView.adSourceView = nativeView.findViewById(R.id.ad_source)
        nativeView.callToActionView = nativeView.findViewById(R.id.ad_call_to_action)

        // Populate the native ad asset view. The native ad must contain the title and media assets.
        (nativeView.titleView as TextView).text = nativeAd.title
        nativeView.mediaView.setMediaContent(nativeAd.mediaContent)

        nativeAd.adSource?.let {
            (nativeView.adSourceView as TextView).text = nativeAd.adSource
            nativeView.adSourceView.show()
        } ?: run {
            nativeView.adSourceView.invisible()
        }

        nativeAd.callToAction?.let {
            (nativeView.callToActionView as Button).text = nativeAd.callToAction
            nativeView.callToActionView.show()
        } ?: run {
            nativeView.callToActionView.invisible()
        }

        // Obtain a video controller.
        val videoOperator = nativeAd.videoOperator

        // Check whether a native ad contains video assets.
        if (videoOperator.hasVideo()) {
            // Add a video lifecycle event listener.
            videoOperator.videoLifecycleListener = getViewLifeCycleListener()
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd)
    }

    private fun getViewLifeCycleListener() = object : VideoLifecycleListener() {
        override fun onVideoStart() {
            updateStatus(false, getString(R.string.status_play_start))
        }

        override fun onVideoPlay() {
            updateStatus(false, getString(R.string.status_playing))
        }

        override fun onVideoEnd() {
            // If a video exists, load a new native ad only after video playback is complete.
            updateStatus(true, getString(R.string.status_play_end))
        }
    }

    private fun updateStatus(loadBtnEnable: Boolean, text: String? = null) {
        mBinding.tvStatus.text = if (text.isNullOrBlank()) "" else "Status ==>  $text"
        mBinding.btnLoad.isEnabled = loadBtnEnable
    }

    private fun setListeners() {
        mBinding.btnLoad.setOnClickListener {
            loadAd(getAdId())
        }
    }

    override fun onDestroy() {
        nativeAd?.apply { destroy() }
        super.onDestroy()
    }


}