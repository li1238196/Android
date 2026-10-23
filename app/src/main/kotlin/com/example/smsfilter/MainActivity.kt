package com.example.smsfilter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import com.example.smsfilter.data.FilterRuleRepository
import com.example.smsfilter.ui.FilterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ruleRepository = FilterRuleRepository(applicationContext)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                FilterScreen(ruleRepository)
            }
        }
    }
}
