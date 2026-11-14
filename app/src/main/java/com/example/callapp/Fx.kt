package com.example.callapp

object Fx {
    fun norm(c: Char): Char = when (c) {
        in '\u06F0'..'\u06F9' -> '0' + (c - '\u06F0')
        in '\u0660'..'\u0669' -> '0' + (c - '\u0660')
        else -> c
    }
    fun strip(t: String): String =
        t.filter { it.isLetterOrDigit() || it.isWhitespace() || it in "،.:()+/-*#" }.trim()
}
