package com.example.callapp

import java.util.Calendar
import java.util.Locale

object DateFx {
    private val months = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    fun jalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val g_d_m = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        var jy: Int
        if (gy > 1600) { jy = 979; var gy2 = gy - 1600 } else { jy = 0; var gy2 = gy - 621 }
        val gy2 = if (gy > 1600) gy - 1600 else gy - 621
        val days = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 - 80 + gd + g_d_m[gm - 1]
        jy += 33 * (days / 12053); var d = days % 12053
        jy += 4 * (d / 1461); d %= 1461
        if (d > 365) { jy += (d - 1) / 365; d = (d - 1) % 365 }
        val jm: Int; val jd: Int
        if (d < 186) { jm = 1 + d / 31; jd = 1 + d % 31 } else { jm = 7 + (d - 186) / 30; jd = 1 + (d - 186) % 30 }
        return Triple(jy, jm, jd)
    }

    fun format(ms: Long): String {
        val c = Calendar.getInstance().apply { timeInMillis = ms }
        val (jy, jm, jd) = jalali(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        return String.format(Locale.US, "%d %s %d  %02d:%02d", jd, months.getOrElse(jm - 1) { "" }, jy, h, m)
    }
}
