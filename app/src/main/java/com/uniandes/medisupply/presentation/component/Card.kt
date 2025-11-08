package com.uniandes.medisupply.presentation.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.uniandes.medisupply.presentation.ui.theme.spaces

@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) = androidx.compose.material3.Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.background
    ),
    elevation = CardDefaults.cardElevation(
        MaterialTheme.spaces.xSmall
    ),
    content = content
)