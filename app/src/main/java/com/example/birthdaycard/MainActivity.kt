package com.example.birthdaycard

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.FrameLayout
import androidx.core.view.isVisible
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Calendar
import kotlinx.serialization.encodeToString

class MainActivity : ComponentActivity() {
    var nowMonth: Int = 1
    var startX: Float = 0f
    val dayButtons: MutableList<Button> = arrayListOf()
    val dayInfo: MutableList<TextView> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val calendar = Calendar.getInstance()
        nowMonth = calendar.get(Calendar.MONTH) + 1
        val monthButton = findViewById<TextView>(R.id.month)
        val calender = findViewById<LinearLayout>(R.id.calender)
        monthButton.text = "${nowMonth}月"
        val editWindow = findViewById<LinearLayout>(R.id.edit)
        val windowMonth = findViewById<TextView>(R.id.monthday)
        val windowName = findViewById<LinearLayout>(R.id.name)
        val windowNewName = findViewById<EditText>(R.id.new_name)
        val windowNewTag = findViewById<EditText>(R.id.new_tag)
        val windowButton = findViewById<Button>(R.id.add_button)
        val windowback = findViewById<Button>(R.id.back_button)
        editWindow.isVisible = false

        for (i in 1..31) {
            val resId = resources.getIdentifier("day$i", "id", packageName)
            dayButtons.add(findViewById<Button>(resId))
            val infoId = resources.getIdentifier("textDay${i}Info", "id", packageName)
            dayInfo.add(findViewById<TextView>(infoId))
        }
        daySetting(nowMonth)
        val jsonFile = File(filesDir, "birthday.json")
        var birthdayData: List<Birthday> = emptyList()
        if (jsonFile.exists()) {
            try {
                birthdayData = Json.decodeFromString<List<Birthday>>(jsonFile.readText())
                var names = ""
                var day = 0
                for (birthday in birthdayData) {
                    if (nowMonth == birthday.month) {
                        if (day != birthday.day) {
                            names = ""
                            day = birthday.day
                        }
                        val birthdayButtonName = dayInfo[birthday.day - 1]
                        names += "${birthday.name} - ${birthday.tag}\n"
                        birthdayButtonName.text = names
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        for (i in 1..31) {
            dayButtons[i - 1].setOnClickListener {
                editWindow.isVisible = true
                calender.isVisible = false
                windowMonth.text = "${nowMonth}月${i}日"
                windowNewName.setText("")
                windowNewTag.setText("")
                windowName.removeAllViews()
                for (birthday in birthdayData) {
                    if (nowMonth == birthday.month && i == birthday.day) {
                        val nameText = TextView(this).apply {
                            text = "${birthday.name} - ${birthday.tag}"
                            textSize = 20f
                            setTextColor(getColorStateList(R.color.black))
                        }
                        val deleteButton = Button(this).apply {
                            text = "削除"
                        }
                        deleteButton.setOnClickListener {
                            editWindow.isVisible = false
                            calender.isVisible = true
                            birthdayData = birthdayData.filterNot {
                                it.month == nowMonth && it.day == i && it.name == "${birthday.name}"
                            }
                            val newJson = Json.encodeToString(birthdayData)
                            jsonFile.writeText(newJson)
                            daySetting(nowMonth)
                            birthdaySetting()
                        }
                        val layoutNameParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        val layoutButtonParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.2f
                        )
                        layoutNameParams.setMargins(0, 10, 0, 0)
                        nameText.layoutParams = layoutNameParams
                        deleteButton.layoutParams = layoutButtonParams
                        val linearLayout = LinearLayout(this).apply {
                            orientation = LinearLayout.HORIZONTAL
                        }
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        linearLayout.layoutParams = layoutParams
                        linearLayout.addView(nameText)
                        linearLayout.addView(deleteButton)
                        windowName.addView(linearLayout)
                    }
                }
            }

            windowButton.setOnClickListener {
                val newName = windowNewName.text.toString()
                val newTag = windowNewTag.text.toString()
                if (newName != "" && newTag != "") {
                    val dayNumber = windowMonth.text.indexOf("月")
                    var day = windowMonth.text.substring(dayNumber + 1)
                    day = day.replace("日", "")
                    val newBirthday = Birthday(newName, nowMonth, day.toInt(), newTag)
                    birthdayData += newBirthday
                    val newJson = Json.encodeToString(birthdayData)
                    jsonFile.writeText(newJson)
                    editWindow.isVisible = false
                    calender.isVisible = true
                    birthdaySetting()
                }
            }
            windowback.setOnClickListener {
                editWindow.isVisible = false
                calender.isVisible = true
            }
        }
    }

        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = ev.x
                }

                MotionEvent.ACTION_UP -> {
                    val diffX = ev.x - startX
                    if (diffX > 50f) {
                        nowMonth--
                        if (nowMonth < 1) {
                            nowMonth = 12
                        }
                        val monthButton = findViewById<TextView>(R.id.month)
                        monthButton.text = "${nowMonth}月"
                        daySetting(nowMonth)
                        birthdaySetting()
                    } else if (diffX < -50f) {
                        nowMonth++
                        if (nowMonth > 12) {
                            nowMonth = 1
                        }
                        val monthButton = findViewById<TextView>(R.id.month)
                        monthButton.text = "${nowMonth}月"
                        daySetting(nowMonth)
                        birthdaySetting()
                    }
                }
            }
            return super.dispatchTouchEvent(ev)
        }

        fun birthdaySetting() {
            val jsonFile = File(filesDir, "birthday.json")
            var birthdayData: List<Birthday> = emptyList()
            if (jsonFile.exists()) {
                try {
                    birthdayData = Json.decodeFromString<List<Birthday>>(jsonFile.readText())
                    var names = ""
                    var day = 0
                    for (birthday in birthdayData) {
                        if (nowMonth == birthday.month) {
                            if (day != birthday.day) {
                                names = ""
                                day = birthday.day
                            }
                            val birthdayButtonName = dayInfo[birthday.day - 1]
                            names += "${birthday.name} - ${birthday.tag}\n"
                            birthdayButtonName.text = names
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun daySetting(month: Int) {
            for (i in 1..31) {
                dayInfo[i - 1].text = ""
            }
            for (i in 29..31) {
                if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                    dayButtons[i - 1].isVisible = true
                } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                    if (i == 31) {
                        dayButtons[i - 1].isVisible = false
                    } else {
                        dayButtons[i - 1].isVisible = true
                    }
                } else if (month == 2) {
                    dayButtons[i - 1].isVisible = false
                }
            }
        }
}
