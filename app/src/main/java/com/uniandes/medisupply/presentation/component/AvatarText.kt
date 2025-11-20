package com.uniandes.medisupply.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.uniandes.medisupply.presentation.ui.theme.spaces

@Composable
fun AvatarText(
    initial: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Box(
        modifier = modifier
            .background(
                color = color,
                shape = CircleShape
            )
            .size(MaterialTheme.spaces.xxxLarge),
        contentAlignment = Alignment.Center
    ) {
        val bgL = luminance(color)
        fun contrast(l1: Double, l2: Double) = (maxOf(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
        val contrastWithWhite = contrast(bgL, 1.0)
        val contrastWithBlack = contrast(bgL, 0.0)
        val textColor = if (contrastWithWhite >= contrastWithBlack)
            MaterialTheme.colorScheme.background
        else
            MaterialTheme.colorScheme.onPrimaryContainer

        Text(
            text = if (initial.isEmpty()) "?" else initial.first().toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}

fun luminance(c: Color): Double {
    fun transform(channel: Float): Double {
        val v = channel.toDouble()
        return if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
    }
    val r = transform(c.red)
    val g = transform(c.green)
    val b = transform(c.blue)
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
}