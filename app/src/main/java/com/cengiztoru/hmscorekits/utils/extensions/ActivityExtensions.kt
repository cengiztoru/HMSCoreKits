package com.cengiztoru.hmscorekits.utils.extensions

import android.app.Activity
import android.content.Intent
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

inline fun <reified T : Activity> Activity.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}