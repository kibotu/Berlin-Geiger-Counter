package net.kibotu.berlingeiger.features

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.LocalDate

data class MeasurementsStateHolder(
    val allMeasurements: MutableState<Map<LocalDate, Measurement>> = mutableStateOf(emptyMap()),
    val measurements: MutableState<List<Measurement>> = mutableStateOf(emptyList())
)