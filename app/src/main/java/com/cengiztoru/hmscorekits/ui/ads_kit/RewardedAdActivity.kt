package com.cengiztoru.hmscorekits.ui.ads_kit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityAdsRewardedBinding
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener

class RewardedAdActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RewardedAdActivity"
    }

    private lateinit var mBinding: ActivityAdsRewardedBinding
    private var score = 0
    private val defaultAmount = 0

    private val AD_ID = "testx9dtjwj8hp"    //todo add your ad id here
    private var rewardAd: RewardAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdsRewardedBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setClickListeners()
        loadRewardAd()


    }

//region Rewarded Ad Functions

    private fun showRewardedAd() {
        if (rewardAd?.isLoaded == true) {
            rewardAd?.show(this, object : RewardAdStatusListener() {
                override fun onRewardAdOpened() {
                    // Rewarded ad opened.
                    Log.i(TAG, "onRewardAdOpened")

                }

                override fun onRewardAdFailedToShow(errorCode: Int) {
                    // Failed to display the rewarded ad.
                    Log.i(TAG, "onRewardAdFailedToShow")
                }

                override fun onRewardAdClosed() {
                    loadRewardAd()
                    // Rewarded ad closed.
                    Log.i(TAG, "onRewardAdClosed")
                }

                override fun onRewarded(reward: Reward) {
                    // Provide a reward when reward conditions are met.
                    Log.i(TAG, "onRewarded")
                    // You are advised to grant a reward immediately, and check whether the reward takes effect on the server. If no reward information is configured, grant a reward based on the actual scenario.
                    loadRewardAd()
                    val amount = if (reward.amount == 0) defaultAmount else reward.amount
                    score += amount
                    showToast("Congratulations you earned $amount score")
                    updateScore()
                }
            })
        }
    }

    private fun loadRewardAd() {
        if (rewardAd == null) {
            rewardAd = RewardAd(this, AD_ID)
        }
        val rewardAdLoadListener: RewardAdLoadListener = object : RewardAdLoadListener() {
            override fun onRewardAdFailedToLoad(errorCode: Int) {
                Toast.makeText(
                    this@RewardedAdActivity,
                    "onRewardAdFailedToLoad errorCode is :$errorCode", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onRewardedLoaded() {
                Toast.makeText(this@RewardedAdActivity, "onRewardedLoaded", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        rewardAd?.loadAd(AdParam.Builder().build(), rewardAdLoadListener)
    }

//endregion

    private fun setClickListeners() {
        mBinding.btnShowRewardedAd.setOnClickListener {
            showRewardedAd()
        }
    }

    private fun updateScore() {
        mBinding.tvScore.text = "Score : $score"
    }


}