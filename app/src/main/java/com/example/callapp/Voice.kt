package com.example.callapp

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent

object Voice {
    fun recognize(ctx: Context, prompt: String = "بگویید"): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
    }
}
