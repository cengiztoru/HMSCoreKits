package com.cengiztoru.hmscorekits.ui.account_kit

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cengiztoru.hmscorekits.databinding.ActivityAccountKitBinding
import com.cengiztoru.hmscorekits.utils.extensions.showToast
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.service.AccountAuthService

const val TAG = "AccountKitActivity"

class AccountKitActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAccountKitBinding

    private var mAuthManager: AccountAuthService? = null
    private var mAuthParam: AccountAuthParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflateViews()
        setListeners()

    }

//region signIn with HuaweiID

    private var singInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(result.data)
                if (authAccountTask.isSuccessful) {
                    val authAccount = authAccountTask.result
                    showToast("Welcome ${authAccount.displayName}")
                    printMessage("Name: ${authAccount.displayName} \nAccessToken:${authAccount.accessToken}")
                } else {
                    Log.e(
                        TAG,
                        "signIn failed: " + (authAccountTask.exception as ApiException).statusCode
                    )
                }
            }
        }

    private fun signIn() {
        mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken()
            .setAccessToken()
            .createParams()
        mAuthManager = AccountAuthManager.getService(this@AccountKitActivity, mAuthParam)
        singInResultLauncher.launch(mAuthManager?.signInIntent)
    }

//endregion

//region common functions

    private fun inflateViews() {
        mBinding = ActivityAccountKitBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    private fun setListeners() {
        mBinding.btnAccountSignin.setOnClickListener {
            signIn()
        }
    }

    private fun printMessage(text: String) {
        val beforeText = mBinding.tvLogger.text
        mBinding.tvLogger.text = (if (beforeText.isNotBlank()) "$beforeText \n\n" else "") + text
    }

//endregion

}