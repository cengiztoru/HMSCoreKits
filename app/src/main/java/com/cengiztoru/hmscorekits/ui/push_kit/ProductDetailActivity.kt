package com.cengiztoru.hmscorekits.ui.push_kit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.R
import com.cengiztoru.hmscorekits.databinding.ActivitPushProoductDetailBinding

class ProductDetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ProductDetailActivity"
    }

    private lateinit var mBinding: ActivitPushProoductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitPushProoductDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        intent?.extras?.getString("link")?.let { link ->
            mBinding.tvLogger.append(
                "Title:" + (intent?.extras?.getString("title") ?: getString(R.string.app_name))
            )
            mBinding.tvLogger.append("\n\n\n")
            mBinding.tvLogger.append("PRODUCT LINK: $link")
        }
    }

}