package com.kappstudio.trainschedule.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.kappstudio.trainschedule.R

@Composable
fun TrainIcon(modifier:Modifier=Modifier){
    Icon(
        modifier=modifier,
        painter = painterResource(id = R.drawable.ic_train),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary
    )
}