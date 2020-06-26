package com.example.myapplication.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.renhuan.okhttplib.utils.Renhuan

/**
 * created by renhuan
 * time : 2020/6/21 20:29
 * describe :字体为font.ttf的TextView
 */
class TextViewHappy(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    init {
        typeface = Typeface.createFromAsset(Renhuan.getContext().assets, "font.ttf")
    }
}
