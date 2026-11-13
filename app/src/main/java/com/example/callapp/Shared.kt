package com.example.callapp

object Shared {
    var contacts: List<Pair<String, String>> = emptyList()

    fun name(addr: String): String {
        val k = addr.filter { it.isDigit() }.takeLast(10)
        if (k.isEmpty()) return ""
        return contacts.firstOrNull { it.second.filter { c -> c.isDigit() }.takeLast(10) == k }?.first ?: ""
    }
}
