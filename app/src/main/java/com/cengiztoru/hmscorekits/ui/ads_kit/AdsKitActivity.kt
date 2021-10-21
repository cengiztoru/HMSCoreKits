package com.cengiztoru.hmscorekits.ui.ads_kit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityAdsKitBinding
import com.cengiztoru.hmscorekits.utils.extensions.startActivity

class AdsKitActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAdsKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflateViews()
        setListeners()

    }

    private fun inflateViews() {
        mBinding = ActivityAdsKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    private fun setListeners() {

        mBinding.btnBannerAds.setOnClickListener {
            startActivity<BannerAdsActivity>()
        }

        mBinding.btnRewardedAd.setOnClickListener {
            startActivity<RewardedAdActivity>()
        }

        mBinding.btnInterstitialAd.setOnClickListener {
            startActivity<InterstitialAdActivity>()
        }

    }
}