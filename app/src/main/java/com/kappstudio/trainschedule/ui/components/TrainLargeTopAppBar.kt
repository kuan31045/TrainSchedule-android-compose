package com.kappstudio.trainschedule.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.theme.isLight
import com.kappstudio.trainschedule.ui.theme.setStatueBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainLargeTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navigateUp: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {

    val collapsedColor = if(MaterialTheme.colorScheme.isLight()){
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
    }else{
        MaterialTheme.colorScheme.onSecondary
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val view = LocalView.current

    /**
     * Change status bar color when collapsed
     */
    LaunchedEffect(key1 = scrollBehavior.state.collapsedFraction) {
        setStatueBarColor(
            view = view,
            color = if (scrollBehavior.state.collapsedFraction > 0.8f) {
                collapsedColor
            } else {
                backgroundColor
            }
        )
    }

    LargeTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            scrolledContainerColor = collapsedColor
        ),
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_desc),
                )
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior
    )
}