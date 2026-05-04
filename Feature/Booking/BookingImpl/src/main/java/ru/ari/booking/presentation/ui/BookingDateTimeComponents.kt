package ru.ari.booking.presentation.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDatePickerDialog(
    value: String,
    minDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = value.toLocalDateOrNull()?.toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis.toLocalDate().isBefore(minDate).not()
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis
                        ?.toLocalDate()
                        ?.format(DATE_FORMATTER)
                        ?.let(onDateSelected)
                },
                enabled = pickerState.selectedDateMillis != null
            ) {
                Text("Готово")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    ) {
        DatePicker(
            state = pickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingTimePickerDialog(
    title: String,
    value: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val currentTime = value.toLocalTimeOrNull() ?: LocalTime.now()
    val pickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            TimePicker(state = pickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(
                        LocalTime.of(pickerState.hour, pickerState.minute)
                            .format(TIME_FORMATTER)
                    )
                }
            ) {
                Text("Готово")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

fun String.toLocalDateOrNull(): LocalDate? =
    try {
        LocalDate.parse(this, DATE_FORMATTER)
    } catch (_: DateTimeParseException) {
        null
    }

private fun String.toLocalTimeOrNull(): LocalTime? =
    try {
        LocalTime.parse(this, TIME_FORMATTER)
    } catch (_: DateTimeParseException) {
        null
    }

private fun LocalDate.toUtcMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toLocalDate()

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
