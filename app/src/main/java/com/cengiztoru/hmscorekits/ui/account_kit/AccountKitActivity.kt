package com.cengiztoru.hmscorekits.ui.account_kit

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
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
                    printMessage("SignIn with Huawei Id Sucess \nName: ${authAccount.displayName} \nAccessToken:${authAccount.accessToken}")
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

//region signInAuthorizationCode
    /**** For security reasons, the operation of changing the code to an AT must be performed on your server. The code is only an example and cannot be run.  */
    /**Based on the authorization code, your app obtains the access token, refresh token, and ID token from the Account Kit server.*/
    private var singInAuthorizationCodeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(result.data)
                if (authAccountTask.isSuccessful) {
                    val authAccount = authAccountTask.result
                    printMessage("SignIn & get Auth code success \nServerAuthCode:  ${authAccount.authorizationCode}")
                    Log.i(TAG, "signIn get code success.")
                    Log.i(TAG, "ServerAuthCode: " + authAccount.authorizationCode)
                } else {
                    Log.e(
                        TAG,
                        "signIn get code failed: " + (authAccountTask.exception as ApiException).statusCode
                    )
                }
            }
        }

    private fun signInWithAuthorizationCode() {
        mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setProfile()
            .setAuthorizationCode()
            .createParams()
        mAuthManager = AccountAuthManager.getService(this@AccountKitActivity, mAuthParam)
        singInAuthorizationCodeResultLauncher.launch(mAuthManager?.signInIntent)
    }
//endregion

//region Silently SingIn for specific scenarios

    /** In state as User Not Signed In, Not Authorized or Not Signed In, Authorized an exception will be throw.
     * So you should use normal sign on failure state. After user signed you can silent signin method*/
    private fun silentlySignIn() {
        mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken()
            .setAccessToken()
            .createParams()
        //OR
        /** mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
        .setProfile()
        .setAuthorizationCode()
        .createParams()
         */
        mAuthManager = AccountAuthManager.getService(this@AccountKitActivity, mAuthParam)
        val task = mAuthManager?.silentSignIn()
        task?.addOnSuccessListener { account ->
            val method =
                if (account.accountFlag == 0) "Huawei ID" else " AppTouch ID"  // // Obtain the **0**D type (0: HU**1**WEI ID; 1: AppTouch ID).
            printMessage("Silently SignIn Success 😉 via \"$method\" \nName: ${account.displayName}")
            Log.i(TAG, "silentSignIn success")
        }
        task?.addOnFailureListener { e ->
            showToast("Silently SignIn is Failure. Normal SingIn is starting")
            if (e is ApiException) {
                signIn()
            }
        }
    }

//endregion

//region SingOut

    private fun signOut() {
        mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setProfile()
            .setAuthorizationCode()
            .createParams()
        mAuthManager = AccountAuthManager.getService(this@AccountKitActivity, mAuthParam)
        val signOutTask = mAuthManager?.signOut()
        signOutTask?.addOnSuccessListener {
            showToast("SignOut Success")
            printMessage("SignOut Success")
        }?.addOnFailureListener {
            showToast("SignOut Failed")
            printMessage("SignOut Failed")
        }
    }

//endregion

//region Revoke Authorization

    private fun revokeAuthorization() {
        mAuthParam = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setProfile()
            .setAuthorizationCode()
            .createParams()
        mAuthManager = AccountAuthManager.getService(this@AccountKitActivity, mAuthParam)
        val task = mAuthManager?.cancelAuthorization()
        task?.addOnSuccessListener {
            showToast("Authorization Revoked")
            printMessage("Authorization Revoked")
        }?.addOnFailureListener {
            showToast("Revoking Authorization Failed")
            printMessage("Revoking Authorization Failed")
        }
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

        mBinding.btnAccountSigninAuthorizationCode.setOnClickListener {
            signInWithAuthorizationCode()
        }

        mBinding.btnSilentSignin.setOnClickListener {
            silentlySignIn()
        }

        mBinding.btnAccountSignout.setOnClickListener {
            signOut()
        }

        mBinding.btnRevokeAuthorization.setOnClickListener {
            revokeAuthorization()
        }
    }

    private fun printMessage(text: String) {
        val beforeText = mBinding.tvLogger.text
        mBinding.tvLogger.text =
            (if (beforeText.isNotBlank()) "$beforeText\n---------------------------------- \n\n" else "") + text
        mBinding.svLogger.apply { post { fullScroll(View.FOCUS_DOWN) } }
    }

//endregion

}