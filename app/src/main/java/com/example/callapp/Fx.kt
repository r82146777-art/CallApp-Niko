package com.example.callapp

object Fx {
    fun norm(c: Char): Char = when (c) {
        '۰', '٠' -> '0'
        '۱', '١' -> '1'
        '۲', '٢' -> '2'
        '۳', '٣' -> '3'
        '۴', '٤' -> '4'
        '۵', '٥' -> '5'
        '۶', '٦' -> '6'
        '۷', '٧' -> '7'
        '۸', '٨' -> '8'
        '۹', '٩' -> '9'
        else -> c
    }

    fun digits(s: String): String = s.map { norm(it) }.filter { it.isDigit() || it == '+' }.joinToString("")

    fun last10(s: String): String {
        val d = digits(s).filter { it.isDigit() }
        return if (d.length <= 10) d else d.takeLast(10)
    }
}
