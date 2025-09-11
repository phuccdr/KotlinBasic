package com.wd.kotlin_basic.migratetokotlin.test

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class TestViewWithoutOverloads(
    context: Context,
    attrs: AttributeSet?=null,
    defStyleAttr: Int=0,
) : RelativeLayout(context, attrs, defStyleAttr)

class TestViewWithOverloads @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr)