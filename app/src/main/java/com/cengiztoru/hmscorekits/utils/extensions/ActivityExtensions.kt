package com.cengiztoru.hmscorekits.utils.extensions

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

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


fun Activity.fullscreen() {
    with(WindowInsetsControllerCompat(window, window.decorView)) {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.systemBars())
    }
}

fun Activity.exitFullscreen() {
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}