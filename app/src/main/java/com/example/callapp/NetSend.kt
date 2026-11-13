package com.example.callapp

import android.content.Context
import android.content.Intent

object NetSend {
    fun send(ctx: Context, to: String, body: String) {
        ctx.startService(Intent(ctx, NetService::class.java).apply {
            action = "send"
            putExtra("to", to)
            putExtra("body", body)
        })
    }
}
