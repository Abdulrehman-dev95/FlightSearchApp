package com.example.flightsearchapp.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object Utils {
    fun stringBuilder(str1: String, str2: String): AnnotatedString {

        val str = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(str1)
            }
            append("-")
            append(str2)
        }
        return str
    }
}