package com.example.birthdaycard
import kotlinx.serialization.Serializable

@Serializable
data class Birthday(
    val name: String,
    val month: Int,
    val day: Int,
    val tag: String,
    val id: Int
)
