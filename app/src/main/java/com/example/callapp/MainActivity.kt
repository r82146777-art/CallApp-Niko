package com.example.callapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*

/** Accessible home: keypad, recents, contacts, settings. */
class MainActivity : Activity() {
    private data class Row(val title: String, val sub: String, val number: String)
    private lateinit var numField: EditText
    private lateinit var searchField: EditText
    private lateinit var listView: ListView
    private lateinit var recentList: ListView
    private val tabButtons = mutableListOf<Button>()
    private val panels = mutableListOf<View>()
    private var contacts = listOf<Row>()
    private var recents = listOf<Row>()
    private val primary = Color.parseColor("#00796B")
    private val green = Color.parseColor("#2E7D32")
    private val red = Color.parseColor("#C62828")
    private val blue = Color.parseColor("#1565C0")

    private fun btn(label: String, desc: String, color: Int, h: Int = 140): Button =
        Button(this).apply {
            text = label; contentDescription = desc; textSize = 17f; setTextColor(Color.WHITE)
            isAllCaps = false; minimumHeight = h
            background = GradientDrawable().apply { setColor(color); cornerRadius = 28f }
            layoutParams = LinearLayout.LayoutParams(0, h, 1f).apply { setMargins(6, 6, 6, 6) }
        }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FAFAFA"))
            contentDescription = "صفحه اصلی تماس و پیام. چهار زبانه"
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
        root.addView(TextView(this).apply {
            text = "تماس و پیام"; textSize = 22f; setPadding(24, 20, 24, 8)
            setTextColor(Color.parseColor("#004D40")); contentDescription = "تماس و پیام"
        })
        val tabRow = LinearLayout(this)
        listOf(
            "صفحه کلید" to "زبانه صفحه کلید",
            "گزارش تماس" to "زبانه گزارش تماس",
            "مخاطبین" to "زبانه مخاطبین",
            "تنظیمات" to "زبانه تنظیمات"
        ).forEachIndexed { i, (t, d) ->
            val bb = btn(t, d, if (i == 0) primary else Color.parseColor("#546E7A"), 115)
            bb.setOnClickListener { showTab(i) }; tabButtons.add(bb); tabRow.addView(bb)
        }
        root.addView(tabRow)

        val keypad = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(10, 10, 10, 10) }
        numField = EditText(this).apply {
            hint = "شماره تلفن"; contentDescription = "فیلد شماره تلفن"; textSize = 28f
            inputType = InputType.TYPE_CLASS_PHONE; gravity = Gravity.CENTER; setPadding(12, 20, 12, 20)
        }
        keypad.addView(numField)
        val grid = GridLayout(this).apply { columnCount = 3; useDefaultMargins = true }
        listOf("1" to "یک", "2" to "دو", "3" to "سه", "4" to "چهار", "5" to "پنج", "6" to "شش",
            "7" to "هفت", "8" to "هشت", "9" to "نه", "*" to "ستاره", "0" to "صفر", "#" to "مربع"
        ).forEach { (d, s) ->
            grid.addView(Button(this).apply {
                text = d; textSize = 26f; contentDescription = s; minimumHeight = 125
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0; height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); setMargins(5, 5, 5, 5)
                }
                setOnClickListener {
                    numField.append(d)
                    try { Sfx.digit(this@MainActivity, d[0]) } catch (_: Exception) {}
                }
            })
        }
        keypad.addView(grid)
        val row1 = LinearLayout(this)
        row1.addView(btn("تماس", "برقراری تماس صوتی", green, 145).apply { setOnClickListener { placeCall(false) } })
        row1.addView(btn("تصویری", "تماس تصویری", blue, 145).apply { setOnClickListener { placeCall(true) } })
        row1.addView(btn("حذف", "پاک کردن آخرین رقم", red, 145).apply {
            setOnClickListener {
                if (numField.text.isNotEmpty()) numField.setText(numField.text.dropLast(1))
                numField.setSelection(numField.text.length)
            }
        })
        keypad.addView(row1)
        val row2 = LinearLayout(this)
        row2.addView(btn("پیام‌ها", "باز کردن پیام‌ها", blue, 130).apply {
            setOnClickListener { startActivity(Intent(this@MainActivity, MessagesActivity::class.java)) }
        })
        row2.addView(btn("پاک‌کردن", "پاک کردن کامل شماره", Color.parseColor("#6A1B9A"), 130).apply {
            setOnClickListener { numField.setText("") }
        })
        keypad.addView(row2)

        val recentPanel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(12, 12, 12, 12) }
        recentPanel.addView(TextView(this).apply {
            text = "گزارش تماس‌های اخیر"; textSize = 18f; contentDescription = "گزارش تماس"; setPadding(8, 8, 8, 12)
        })
        recentList = ListView(this).apply { contentDescription = "فهرست تماس‌های اخیر" }
        recentPanel.addView(recentList, LinearLayout.LayoutParams(-1, 0, 1f))
        recentList.setOnItemClickListener { _, _, pos, _ ->
            if (pos < recents.size) { numField.setText(recents[pos].number); showTab(0) }
        }

        val contactPanel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(12, 12, 12, 12) }
        searchField = EditText(this).apply {
            hint = "جستجو در مخاطبین"; contentDescription = "جستجو در مخاطبین"; textSize = 18f
        }
        contactPanel.addView(searchField)
        listView = ListView(this).apply { contentDescription = "فهرست مخاطبین" }
        contactPanel.addView(listView, LinearLayout.LayoutParams(-1, 0, 1f))
        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterContacts(s?.toString() ?: "") }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })
        listView.setOnItemClickListener { _, _, pos, _ ->
            val item = (listView.adapter as? ArrayAdapter<*>)?.getItem(pos) as? String ?: return@setOnItemClickListener
            numField.setText(item.substringAfterLast("\u2014").trim()); showTab(0)
        }

        val settings = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 24, 24, 24) }
        settings.addView(TextView(this).apply {
            text = "تنظیمات و مجوزها"; textSize = 20f; contentDescription = "تنظیمات"; setPadding(0, 0, 0, 16)
        })
        settings.addView(TextView(this).apply {
            text = "برای کارکرد کامل، مجوز تماس، مخاطبین و گزارش تماس را بدهید. همه دکمه‌ها برچسب صوتی دارند."
            textSize = 16f; setPadding(0, 0, 0, 20)
        })
        settings.addView(btn("درخواست مجوزها", "درخواست مجوزهای تماس و مخاطبین", primary, 140).apply {
            setOnClickListener { requestPerms() }
        })
        settings.addView(btn("حالت نیکو روشن/خاموش", "تغییر حالت صدای نیکو", Color.parseColor("#6A1B9A"), 140).apply {
            setOnClickListener {
                val p = getSharedPreferences("s", MODE_PRIVATE)
                val n = if (p.getInt("fx", 0) == 1) 0 else 1
                p.edit().putInt("fx", n).apply()
                Toast.makeText(this@MainActivity, if (n == 1) "حالت نیکو روشن" else "حالت عادی", Toast.LENGTH_SHORT).show()
            }
        })

        panels.addAll(listOf(keypad, recentPanel, contactPanel, settings))
        panels.forEach { root.addView(it, LinearLayout.LayoutParams(-1, 0, 1f)) }
        setContentView(root)
        showTab(0); requestPerms(); reloadData()
    }

    private fun showTab(i: Int) {
        panels.forEachIndexed { idx, v ->
            v.visibility = if (idx == i) View.VISIBLE else View.GONE
            v.importantForAccessibility = if (idx == i) View.IMPORTANT_FOR_ACCESSIBILITY_YES
            else View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        }
        tabButtons.forEachIndexed { idx, b ->
            b.background = GradientDrawable().apply {
                setColor(if (idx == i) primary else Color.parseColor("#546E7A")); cornerRadius = 28f
            }
        }
        Toast.makeText(this, listOf("صفحه کلید", "گزارش تماس", "مخاطبین", "تنظیمات")[i], Toast.LENGTH_SHORT).show()
        if (i == 1 || i == 2) reloadData()
    }

    private fun requestPerms() {
        val need = arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE)
            .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (need.isNotEmpty()) requestPermissions(need.toTypedArray(), 100)
    }

    override fun onRequestPermissionsResult(c: Int, p: Array<out String>, r: IntArray) {
        super.onRequestPermissionsResult(c, p, r); reloadData()
    }
    override fun onResume() { super.onResume(); reloadData() }

    private fun reloadData() {
        loadContacts(); loadRecents(); filterContacts(searchField.text?.toString() ?: "")
        recentList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1,
            recents.map { r -> val t = if (r.title.isNotBlank()) r.title else r.number; "$t\n${r.sub}" })
    }

    private fun loadContacts() {
        val out = mutableListOf<Row>()
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            contacts = emptyList(); return
        }
        try {
            contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
                null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")?.use { c ->
                while (c.moveToNext()) {
                    val n = c.getString(0) ?: ""; val num = c.getString(1) ?: ""
                    if (num.isNotBlank()) out.add(Row(n, num, num.filter { it.isDigit() || it == '+' }))
                }
            }
        } catch (_: Exception) {}
        contacts = out.distinctBy { it.number.takeLast(10) + it.title }
    }

    private fun loadRecents() {
        val out = mutableListOf<Row>()
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            recents = emptyList(); return
        }
        try {
            contentResolver.query(CallLog.Calls.CONTENT_URI,
                arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE),
                null, null, CallLog.Calls.DATE + " DESC")?.use { c ->
                while (c.moveToNext() && out.size < 80) {
                    val num = c.getString(0) ?: ""; val name = c.getString(1) ?: ""
                    val type = when (c.getInt(2)) { 1 -> "ورودی"; 2 -> "خروجی"; 3 -> "بی‌پاسخ"; else -> "تماس" }
                    out.add(Row(if (name.isNotBlank()) name else num, type, num))
                }
            }
        } catch (_: Exception) {}
        recents = out
    }

    private fun filterContacts(q: String) {
        val nq = q.filter { it.isLetterOrDigit() }
        val f = if (nq.isBlank()) contacts else contacts.filter { it.title.contains(q, true) || it.number.contains(nq) }
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            f.map { r -> "${if (r.title.isNotBlank()) r.title else "بدون نام"} \u2014 ${r.number}" })
        listView.contentDescription = "فهرست مخاطبین، ${f.size} مورد"
    }

    private fun placeCall(video: Boolean) {
        val raw = numField.text.toString().filter { it.isDigit() || it == '+' || it == '*' || it == '#' }
        if (raw.isBlank()) { Toast.makeText(this, "شماره را وارد کنید", Toast.LENGTH_SHORT).show(); return }
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPerms(); Toast.makeText(this, "مجوز تماس لازم است", Toast.LENGTH_LONG).show(); return
        }
        try {
            try { Sfx.start(this) } catch (_: Exception) {}
            val uri = Uri.fromParts("tel", raw, null)
            if (video) {
                val extras = Bundle()
                extras.putInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, 3)
                getSystemService(TelecomManager::class.java).placeCall(uri, extras)
            } else startActivity(Intent(Intent.ACTION_CALL, uri))
        } catch (e: Exception) {
            Toast.makeText(this, "تماس برقرار نشد: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
