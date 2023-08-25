package com.kappstudio.trainschedule.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.navigation.TrainNavGraph

@Composable
fun TrainApp(navController: NavHostController = rememberNavController()) {
    TrainNavGraph(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    Surface(shadowElevation = dimensionResource(R.dimen.surface_shadow_elevation_4)) {

        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                Text(text = title)
            },
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_desc),
                        )
                    }
                }
            },
            actions = actions
        )
    }
}

@Preview
@Composable
fun TrainAppBarPreview() {
    TrainTopAppBar(
        title = "台鐵時刻表",
        canNavigateBack = true,
        navigateUp = {},
    )
}