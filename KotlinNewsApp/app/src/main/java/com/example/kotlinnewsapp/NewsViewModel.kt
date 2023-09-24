package com.example.kotlinnewsapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class NewsViewModel : ViewModel() {
    val newsList = MutableStateFlow(
        listOf(
        News(1, "Хорошая новость 1", "Мировое число голодных сократилось на 20% благодаря усилиям глобальных организаций по борьбе с голодом."),
        News(2, "Хорошая новость 2", "Студенты по всему миру достигли исторической высокой отметки по результатам международных тестов по образованию."),
        News(3, "Хорошая новость 3", "Рекордное число деревьев было посажено в рамках мировых инициатив по зеленым насаждениям."),
        News(4, "Хорошая новость 4", "Мировая экономика оживает, и уровень безработицы снижается."),
        News(5, "Хорошая новость 5", "Спортсмены по всему миру устанавливают новые рекорды и вдохновляют молодое поколение заниматься спортом и поддерживать активный образ жизни."),
        News(6, "Хорошая новость 6", "Новый прорыв в медицине помог бороться с редкими заболеваниями."),
        News(7, "Хорошая новость 7", "Уровень безработицы снизился до исторических минимумов."),
        News(8, "Хорошая новость 8", "Ученые открыли новый способ борьбы с пластиковым загрязнением океанов."),
        News(9, "Хорошая новость 9", "В местном приюте для животных нашли новые дома для всех бездомных питомцев."),
        News(10, "Хорошая новость 10", "Новый проект по охране окружающей среды спасает уязвимые виды животных.")
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