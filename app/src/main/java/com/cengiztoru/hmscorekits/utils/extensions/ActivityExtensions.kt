package com.cengiztoru.hmscorekits.utils.extensions

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Created by Cengiz TORU on 18/10/2021.
 * cengiztoru@gmail.com.tr
 */

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    applicationContext.showToast(message, duration)
}

fun Activity.showToast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    applicationContext.showToast(messageRes, duration)
}