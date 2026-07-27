package com.example.birthdaycard

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val calendar = Calendar.getInstance()
        var nowMonth = calendar.get(Calendar.MONTH) + 1

        val monthButton = findViewById<Button>(R.id.month)
        monthButton.text = "${nowMonth}月"

        val jsonFile = File(filesDir, "birthday.json")
        if (jsonFile.exists()) {
            try {
                val birthdayData = Json.decodeFromString<birthday>(jsonFile.readText())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var totalMove: Float = 0f
        Modifier.pointerInput(Unit){
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (totalMove > 50f){
                        nowMonth += 1
                        if (nowMonth > 12){
                            nowMonth = 1
                        }
                        monthButton.text = "${nowMonth}月"
                    }
                    else if (totalMove < -50f){
                        nowMonth -= 1
                        if (nowMonth < 0){
                            nowMonth = 12
                        }
                        monthButton.text = "${nowMonth}月"
                    }
                    totalMove = 0f
                },
                onHorizontalDrag = { change, dragAmount ->
                    change.consume()
                    totalMove += dragAmount
                }
            )
        }


    }
}
