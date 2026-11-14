package com.example.callapp

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object Sfx {
    private fun tone(type: Int) {
        try {
            val tg = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            tg.startTone(type, 120)
        } catch (_: Exception) {}
    }

    private fun vib(ctx: Context, ms: Long = 40) {
        try {
            val v = if (Build.VERSION.SDK_INT >= 31)
                ctx.getSystemService(VibratorManager::class.java)?.defaultVibrator
            else @Suppress("DEPRECATION") ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            v?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Exception) {}
    }

    fun digit(ctx: Context, key: Char) {
        vib(ctx, 30)
        val niko = ctx.getSharedPreferences("s", Context.MODE_PRIVATE).getInt("fx", 0) == 1
        if (niko) tone(ToneGenerator.TONE_DTMF_0 + (key - '0').coerceIn(0, 9))
        else tone(ToneGenerator.TONE_PROP_BEEP)
    }

    fun start(ctx: Context) { vib(ctx, 60); tone(ToneGenerator.TONE_SUP_RINGTONE) }
    fun connect(ctx: Context) { vib(ctx, 80); tone(ToneGenerator.TONE_PROP_ACK) }
    fun end(ctx: Context) { vib(ctx, 50); tone(ToneGenerator.TONE_PROP_NACK) }
}
