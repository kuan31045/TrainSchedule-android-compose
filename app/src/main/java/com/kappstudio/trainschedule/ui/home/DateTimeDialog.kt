package com.kappstudio.trainschedule.ui.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.commandiron.wheel_picker_compose.core.WheelTextPicker
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.ui.components.SegmentedControl
import com.kappstudio.trainschedule.util.dateFormatter
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DateTimeDialog(
    modifier: Modifier = Modifier,
    defaultDate: LocalDate,
    defaultTime: LocalTime,
    defaultSelectedIndex: Int,
    closeDialog: () -> Unit,
    confirmTime: (LocalDate, LocalTime, Int) -> Unit
) {
    var selectedIndex by rememberSaveable { mutableStateOf(defaultSelectedIndex) }
    var date by rememberSaveable { mutableStateOf(defaultDate) }
    var time by rememberSaveable { mutableStateOf(defaultTime) }

    val timeList = (0L..50L).map { LocalDate.now().plusDays(it) }

    var isResetFinish by rememberSaveable { mutableStateOf(true) }
    val localDensity = LocalDensity.current
    var heightIs by remember { mutableStateOf(0.dp) }

    AlertDialog(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onDismissRequest = closeDialog,
        title = {
            SegmentedControl(
                modifier = Modifier.padding(horizontal = 8.dp),
                items = listOf(
                    stringResource(id = R.string.departure),
                    stringResource(id = R.string.arrival)
                ),
                onItemSelection = { selectedIndex = it },
                selectedIndex = selectedIndex
            )
        },
        text = {
            Row(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        heightIs = with(localDensity) { coordinates.size.height.toDp() }
                    }
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = heightIs),
            ) {
                if (isResetFinish) {
                    WheelTextPicker(
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_size))
                        ),
                        startIndex = timeList.indexOf(date),
                        modifier = Modifier.weight(1f),
                        rowCount = 3,
                        texts = timeList.map { it.format(dateFormatter) }
                    ) { index ->
                        date = timeList[index]
                        null
                    }
                    WheelTimePicker(
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner_size))
                        ),
                        startTime = time,
                        modifier = Modifier.weight(1f),
                    ) { snappedDate ->
                        time = snappedDate
                    }
                } else {
                    isResetFinish = true
                }
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = {
                    selectedIndex = 0
                    date = LocalDate.now()
                    time = LocalTime.now()
                    isResetFinish = false
                }) {
                    Text(stringResource(id = R.string.current_time))
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = closeDialog) {
                    Text(stringResource(id = R.string.cancel))
                }
                TextButton(onClick = {
                    confirmTime(date, time, selectedIndex)
                    closeDialog()
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun DateDialogPreview() {
    DateTimeDialog(
        closeDialog = {},
        defaultDate = LocalDate.now(),
        defaultTime = LocalTime.now(),
        defaultSelectedIndex = 0,
        confirmTime = { p1, p2, p3 -> })
}