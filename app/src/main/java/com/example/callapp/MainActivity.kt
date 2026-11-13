package com.example.callapp

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#004D40"))
            setPadding(48, 96, 48, 48)
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val title = TextView(this).apply {
            text = "تماس و پیام \nCallApp Niko v1.2"
            textSize = 26f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }
        val info = TextView(this).apply {
            text = "نسخه عمومی\nminSdk 24 (اندروید 7+)\n\nسورس کامل در گوگل درایو:\nCallApp-Niko-v1.2-Public"
            textSize = 16f
            setTextColor(Color.parseColor("#B2DFDB"))
            gravity = Gravity.CENTER
        }
        root.addView(title)
        root.addView(info)
        setContentView(root)
    }
}
