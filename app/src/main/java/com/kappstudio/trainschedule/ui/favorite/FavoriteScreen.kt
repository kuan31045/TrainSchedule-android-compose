package com.kappstudio.trainschedule.ui.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.ui.TrainTopAppBar
import com.kappstudio.trainschedule.util.localize

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TrainTopAppBar(
                title = stringResource(id = R.string.favorite_title),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.value.paths.isNotEmpty()) {
                FavoriteColumn(
                    modifier= Modifier.fillMaxSize(),
                    paths = uiState.value.paths,
                    onDeleteButtonClicked = { viewModel.deletePath(it) },
                    onPathClicked = {
                        viewModel.saveCurrentPath(it)
                        navigateBack()
                    }
                )
            } else {
                Text(text = stringResource(id = R.string.no_favorite))
            }
        }
    }
}

@Composable
fun FavoriteColumn(
    modifier: Modifier = Modifier,
    paths: List<Path>,
    onDeleteButtonClicked: (path: Path) -> Unit,
    onPathClicked: (path: Path) -> Unit,
) {

    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = paths,
            key = { it.arrivalStation.id + it.departureStation.id }
        ) { path ->
            FavoriteItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPathClicked(path) },
                path = path,
                onDeleteButtonClicked = { onDeleteButtonClicked(it) },
            )
        }
    }


}

@Composable
fun FavoriteItem(
    modifier: Modifier = Modifier,
    path: Path,
    onDeleteButtonClicked: (path: Path) -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp),
            text = "${path.departureStation.name.localize()}   ➔   ${path.arrivalStation.name.localize()}",
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(
            modifier = Modifier.padding(end = 4.dp),
            onClick = { onDeleteButtonClicked(path) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_desc),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    Divider(thickness = 0.5.dp)
}

@Preview
@Composable
fun FavoriteItemPreview() {
    FavoriteItem(
        path = Path(
            departureStation = Station(
                id = "1000",
                name = Name("Taipei", "臺北"),
                county = Name("Taipei", "臺北")
            ),
            arrivalStation = Station(
                id = "1210",
                name = Name("Hsinchu", "新竹"),
                county = Name("Hsinchu", "新竹")
            ),
        ),
        onDeleteButtonClicked = {}
    )
}