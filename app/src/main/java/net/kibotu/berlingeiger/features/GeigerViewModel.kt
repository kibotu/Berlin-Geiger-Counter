package net.kibotu.berlingeiger.features

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDate
import timber.log.Timber


class GeigerViewModel : ViewModel() {

    val allMeasurements: MutableState<Map<LocalDate, Measurement>> = mutableStateOf(emptyMap())

    val measurements: MutableState<List<Measurement>> = mutableStateOf(emptyList())

    fun loadAll() {
        val database = Firebase.database
        val counter = database
            .getReference("counter")
            .child("2022-03-12")

        counter.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val measurements =
                    dataSnapshot.getValue<Map<String, Map<String, Map<String, String>>>>()
                        ?.map {
                            it.key.toLocalDate() to it.value
                                .values
                                .map {
                                    Measurement(
                                        // "2022-02-27T22:21:32.091689"
                                        date = it["date"]?.substring(0, 20)?.plus("Z")?.toInstant(),
                                        cpm = it["cpm"]?.toIntOrNull() ?: 0,
                                        usvh = it["usvh"]?.toDoubleOrNull() ?: 0.0
                                    )
                                }
                        }?.associate {
                            it.first to it.second.sortedBy { it.date }
                        }.orEmpty()

                measurements.forEach {
                    Timber.d(
                        "${it.key} ${it.value.last()} avg=${
                            it.value.sumOf { it.usvh }.div(it.value.size)
                        } size=${it.value.size}"
                    )
                }

                allMeasurements.value = measurements.map { it.key to it.value.last() }
                    .associate { it.first to it.second }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.w("Failed to read value.${error.toException()}")
            }
        })
    }

    fun loadDay(day: String) {
        Timber.v("load $day")

        val database = Firebase.database
        val counter = database
            .getReference("counter")
            .child(day)

        counter.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val measurement = dataSnapshot.getValue<Map<String, Map<String, String>>>()
                    ?.map {
                        Measurement(
                            // "2022-02-27T22:21:32.091689"
                            date = it.value["date"]?.substring(0, 20)?.plus("Z")?.toInstant(),
                            cpm = it.value["cpm"]?.toIntOrNull() ?: 0,
                            usvh = it.value["usvh"]?.toDoubleOrNull() ?: 0.0
                        )
                    }?.sortedBy { it.date }

                this@GeigerViewModel.measurements.value = measurement.orEmpty()
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.w("Failed to read value.${error.toException()}")
            }
        })
    }
}

data class Measurement(
    val date: Instant?,
    val cpm: Int,
    val usvh: Double
)
