package com.example.callapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/** Home for blind and sighted: keypad, call log, contacts, settings. */
class MainActivity : Activity() {
    private data class Row(val title: String, val subtitle: String, val number: String)

    private lateinit var numField: EditText
    private lateinit var searchField: EditText
    private lateinit var listView: ListView
    private val tabButtons = mutableListOf<Button>()
    private val panels = mutableListOf<View>()
    private var contacts = listOf<Row>()
    private var recents = listOf<Row>()
    private var currentTab = 0

    private val primary = Color.parseColor("#00796B")
    private val green = Color.parseColor("#2E7D32")
    private val red = Color.parseColor("#C62828")
    private val blue = Color.parseColor("#1565C0")

    private fun styledButton(label: String, desc: String, color: Int = primary, height: Int = 140): Button {
        return Button(this).apply {
            text = label
            contentDescription = desc
            textSize = 18f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = 28f
            }
            isAllCaps = false
            minimumHeight = height
            layoutParams = LinearLayout.LayoutParams(0, height, 1f).apply {
                setMargins(6, 6, 6, 6)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FAFAFA"))
            contentDescription = "صفحه اصلی تماس و پیام. چهار زبانه: صفحه کلید، گزارش تماس، مخاطبین و تنظیمات"
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }

        root.addView(TextView(this).apply {
            text = "تماس و پیام"
            textSize = 22f
            setPadding(24, 24, 24, 8)
            setTextColor(Color.parseColor("#004D40"))
            contentDescription = "تماس و پیام"
        })

        val tabRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 4, 4, 4)
        }
        val tabLabels = listOf(
            "صفحه کلید" to "زبانه صفحه کلید شماره‌گیر",
            "گزارش تماس" to "زبانه گزارش تماس‌های اخیر",
            "مخاطبین" to "زبانه فهرست مخاطبین",
            "تنظیمات" to "زبانه تنظیمات و مجوزها"
        )
        tabLabels.forEachIndexed { i, (label, desc) ->
            val b = styledButton(label, desc, if (i == 0) primary else Color.parseColor("#546E7A"), 120)
            b.setOnClickListener { showTab(i) }
            tabButtons.add(b)
            tabRow.addView(b)
        }
        root.addView(tabRow)

        val keypad = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
        }
        numField = EditText(this).apply {
            hint = "شماره تلفن"
            contentDescription = "فیلد شماره تلفن"
            textSize = 28f
            inputType = InputType.TYPE_CLASS_PHONE
            gravity = Gravity.CENTER
            setPadding(16, 24, 16, 24)
        }
        keypad.addView(numField)

        val grid = GridLayout(this).apply {
            columnCount = 3
            useDefaultMargins = true
        }
        val keys = listOf(
            "1" to "یک", "2" to "دو", "3" to "سه",
            "4" to "چهار", "5" to "پنج", "6" to "شش",
            "7" to "هفت", "8" to "هشت", "9" to "نه",
            "*" to "ستاره", "0" to "صفر", "#" to "مربع"
        )
        keys.forEach { (digit, spoken) ->
            val b = Button(this).apply {
                text = digit
                textSize = 26f
                contentDescription = spoken
                minimumHeight = 130
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(6, 6, 6, 6)
                }
                setOnClickListener {
                    numField.append(digit)
                    try { Sfx.digit(this@MainActivity, digit[0]) } catch (_: Exception) {}
                }
            }
            grid.addView(b)
        }
        keypad.addView(grid)

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val callBtn = styledButton("تماس", "برقراری تماس صوتی", green, 150)
        callBtn.setOnClickListener { placeCall(false) }
        val videoBtn = styledButton("تصویری", "تماس تصویری", blue, 150)
        videoBtn.setOnClickListener { placeCall(true) }
        val delBtn = styledButton("حذف", "پاک کردن آخرین رقم", red, 150)
        delBtn.setOnClickListener {
            val t = numField.text
            if (t.isNotEmpty()) numField.setText(t.dropLast(1))
            numField.setSelection(numField.text.length)
        }
        actions.addView(callBtn)
        actions.addView(videoBtn)
        actions.addView(delBtn)
        keypad.addView(actions)

        val more = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val msgBtn = styledButton("پیام‌ها", "باز کردن صفحه پیام‌ها", blue, 130)
        msgBtn.setOnClickListener { startActivity(Intent(this, MessagesActivity::class.java)) }
        val clearBtn = styledButton("پاک کردن", "پاک کردن کامل شماره", Color.parseColor("#6A1B9A"), 130)
        clearBtn.setOnClickListener { numField.setText("") }
        more.addView(msgBtn)
        more.addView(clearBtn)
        keypad.addView(more)

        // CONTINUED in half2
        PLACEHOLDER_HALF2
    }
}
