package com.cengiztoru.hmscorekits.utils.extensions

import android.os.CountDownTimer

fun timer(delay: Long, interval: Long = 1000, onTick: (() -> Unit?)? = null, onFinish: () -> Unit) {
    val timer = object : CountDownTimer(delay, interval) {
        override fun onTick(millisUntilFinished: Long) {
            onTick?.invoke()
        }

        override fun onFinish() {
            onFinish.invoke()
        }
    }
    timer.start()
}