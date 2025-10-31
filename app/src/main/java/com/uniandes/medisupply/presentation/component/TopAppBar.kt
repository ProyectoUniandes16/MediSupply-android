package com.uniandes.medisupply.presentation.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.uniandes.medisupply.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String = "",
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null
) = androidx.compose.material3.TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors().copy(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    title = {
        Text(text = title)
    },
    navigationIcon = navigationIcon,
    actions = actions,
    modifier = modifier,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    scrollBehavior = scrollBehavior
)

@Composable
fun BackNavigation(onBackClicked: () -> Unit): @Composable () -> Unit = {
    IconButton(onClick = onBackClicked) {
        androidx.compose.material3.Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
    }
}