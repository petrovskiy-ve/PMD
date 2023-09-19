package com.example.kotlinnewsapp

data class News(
    val id: Int,
    val title: String,
    val content: String,
    var likes: Int = 0
)
