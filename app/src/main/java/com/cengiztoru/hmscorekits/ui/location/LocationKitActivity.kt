package com.cengiztoru.hmscorekits.ui.location

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityLocationKitBinding

class LocationKitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LocationKitActivity"
    }

    private lateinit var mBinding: ActivityLocationKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLocationKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

}