package com.example.birthdaycard
import kotlinx.serialization.Serializable

@Serializable
data class birthday(
    val name: String,
    val month: Int,
    val day: Int
)
