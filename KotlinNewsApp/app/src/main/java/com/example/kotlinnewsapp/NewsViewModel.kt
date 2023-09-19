package com.example.kotlinnewsapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class NewsViewModel : ViewModel() {
    val newsList = MutableStateFlow(
        listOf(
        News(1, "Заголовок 1", "Содержание 1", 10),
        News(2, "Заголовок 2", "Содержание 2", 5),
        News(3, "Заголовок 3", "Содержание 3", 4),
        News(4, "Заголовок 4", "Содержание 4", 9),
        News(5, "Заголовок 5", "Содержание 5"),
        News(6, "Заголовок 6", "Содержание 6"),
        News(7, "Заголовок 7", "Содержание 7"),
        News(8, "Заголовок 8", "Содержание 8"),
        News(9, "Заголовок 9", "Содержание 9"),
        News(10, "Заголовок 10", "Содержание 10")
    )
    )

    fun incrementLikes(new: News) {
        val news = newsList.value.toMutableList()
        val index =news.indexOfFirst { it == new }
        if (index != -1) {
            val updatedNew = news[index].copy(likes = new.likes + 1)
            news[index] = updatedNew
            newsList.value = news
        }
    }

    fun changeNews() {
        newsList.value = newsList.value.shuffled()
    }

    fun getNews(): List<News> {
        return newsList.value
    }
}