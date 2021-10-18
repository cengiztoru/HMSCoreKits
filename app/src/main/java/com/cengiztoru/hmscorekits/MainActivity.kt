package com.cengiztoru.hmscorekits

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityMainBinding
import com.cengiztoru.hmscorekits.utils.extensions.showToast

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
        mBinding.btnMainTest.setOnClickListener {
            showToast("Main Activity Test Button Clicked")
        }
    }
}