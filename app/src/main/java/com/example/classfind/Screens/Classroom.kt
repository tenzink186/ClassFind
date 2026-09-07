package com.example.classfind.screens

data class Classroom(
    val roomNumber: String,
    val building: String,
    val floor: String,
    val capacity: Int,
    val type: String,
    val distance: String? = null
)