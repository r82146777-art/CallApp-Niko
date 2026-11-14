package com.example.callapp

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class GroupCallActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#FAFAFA"))
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        root.addView(TextView(this).apply {
            text = "تماس گروهی"
            textSize = 24f
            contentDescription = "صفحه تماس گروهی"
            setPadding(0, 0, 0, 24)
        })
        root.addView(TextView(this).apply {
            text = "از صفحه کلید اصلی شماره را وارد کنید و تماس بگیرید."
            textSize = 16f
            setPadding(0, 0, 0, 32)
        })
        root.addView(Button(this).apply {
            text = "بازگشت"
            contentDescription = "بازگشت به صفحه اصلی"
            textSize = 18f
            setOnClickListener { finish() }
        })
        setContentView(root)
    }
}
