package net.kibotu.berlingeiger.features

import kotlinx.datetime.Instant

data class Measurement(
    val date: Instant?,
    val cpm: Int,
    val usvh: Double
)
