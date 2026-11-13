package com.example.callapp

// Full source is in Drive zip CallApp-Niko-v1.2-complete-source.zip
// Placeholder to allow structure; replace with full file from the zip for a complete build.
import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "CallApp Niko v1.2\n\u0633\u0648\u0631\u0633 \u06a9\u0627\u0645\u0644 \u0631\u0627 \u0627\u0632 \u06af\u0648\u06af\u0644 \u062f\u0631\u0627\u06cc\u0648 \u062f\u0627\u0646\u0644\u0648\u062f \u06a9\u0646\u06cc\u062f"
        tv.textSize = 18f
        tv.setPadding(48, 48, 48, 48)
        setContentView(tv)
    }
}
