package com.example.kotlinnewsapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val newsViewModel: NewsViewModel = NewsViewModel()

@Composable
fun NewsCard(news: News) {
    // Общая колонка
    Column(
        modifier = Modifier
            .fillMaxSize(1.0f)
            .padding(6.dp)
            .border(1.dp, Color.Black)
            .background(Color.White)
    ){
        // Строка для новости
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.9f)
        ) {
            // Зачем-то оборачиваем это в колонку, иначе обе строки находятся в одной
            Column(
                modifier = Modifier
                    .fillMaxSize(1.0f)
                    .border(1.dp, Color.Black)
                    .background(Color.White)
            ) {
                // Строка для заголовка
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.2f)
                        .background(Color.DarkGray),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = news.title,
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                // Строка для содержимого
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.8f)
                        .padding(6.dp)
                ) {
                    Text(text = news.content,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        // Строка для лайков
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .background(Color(13, 190, 112)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            //var likes by remember { mutableStateOf(0) } // Счетчик лайков

            Text(
                text = news.likes.toString(),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            print("Before: " + news.likes)

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable {
                    newsViewModel.incrementLikes(news)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}


@Composable
fun NewsScreen() {
    val newsList by rememberUpdatedState(newsViewModel.newsList.collectAsState())

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            newsViewModel.changeNews()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .weight(0.5f)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                NewsCard(news = newsList.value[0])
            }
            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                NewsCard(news = newsList.value[1])
            }
        }
        //Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .weight(0.5f)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                NewsCard(news = newsList.value[2])
            }
            Column(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                NewsCard(news = newsList.value[3])
            }
        }
    }
}


