package com.cengiztoru.hmscorekits.ui.custom_view

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView

/**
 * Created by Cengiz TORU on 23/10/2021.
 * cengiztoru@gmail.com.tr
 */

class LoggerTextView : MaterialTextView {
    private val TAG = "LoggerTextView"

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attr: AttributeSet? = null) : super(context, attr) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        isVerticalScrollBarEnabled = true
        movementMethod = ScrollingMovementMethod()
    }

    override fun append(text: CharSequence?, start: Int, end: Int) {
        super.append(text, start, end)
        while (canScrollVertically(1)) {       //Negative to check scrolling up, positive to check scrolling down.
            scrollBy(0, 10)
        }
    }

}