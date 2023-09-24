package com.example.kotlinnewsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.kotlinnewsapp.ui.theme.KotlinNewsAppTheme

class MainActivity : ComponentActivity() {
    // private val newsViewModel: NewsViewModel by viewModels()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinNewsAppTheme() {
                NewsScreen()
            }
        }
    }
}