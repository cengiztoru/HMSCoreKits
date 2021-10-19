package com.cengiztoru.hmscorekits.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityMainBinding
import com.cengiztoru.hmscorekits.ui.account_kit.AccountKitActivity
import com.cengiztoru.hmscorekits.ui.push_kit.PushKitActivity
import com.cengiztoru.hmscorekits.utils.extensions.startActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        setListeners()

    }

    private fun initViews() {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    private fun setListeners() {
        mBinding.btnAccountKit.setOnClickListener {
            startActivity<AccountKitActivity>()
        }

        mBinding.btnPushKit.setOnClickListener {
            startActivity<PushKitActivity>()
        }
    }
}