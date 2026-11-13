package com.example.callapp

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony

object LocalMsgs {
    data class Thread(val id: Long, val address: String, val body: String, val date: Long, val unread: Int)

    fun threads(ctx: Context): List<Thread> {
        val out = mutableListOf<Thread>()
        val uri = Uri.parse("content://mms-sms/conversations?simple=true")
        try {
            ctx.contentResolver.query(uri, null, null, null, "date DESC")?.use { c ->
                val idIdx = c.getColumnIndex("_id")
                val addrIdx = c.getColumnIndex("address")
                val bodyIdx = c.getColumnIndex("body")
                val dateIdx = c.getColumnIndex("date")
                val unreadIdx = c.getColumnIndex("unread_count")
                while (c.moveToNext()) {
                    val id = if (idIdx >= 0) c.getLong(idIdx) else 0L
                    val addr = if (addrIdx >= 0) c.getString(addrIdx) ?: "" else ""
                    val body = if (bodyIdx >= 0) c.getString(bodyIdx) ?: "" else ""
                    val date = if (dateIdx >= 0) c.getLong(dateIdx) else 0L
                    val unread = if (unreadIdx >= 0) c.getInt(unreadIdx) else 0
                    out.add(Thread(id, addr, body, date, unread))
                }
            }
        } catch (_: Exception) {}
        return out
    }
}
