package com.softwareoverflow.hangtight.ui.util

import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Returns the number duration in mm:ss
 */
fun Long.getFormattedDuration(unit: DurationUnit) : String{
    val duration = this.toDuration(unit)

    return "%02d:%02d".format(
        duration.inWholeMinutes,
        duration.inWholeSeconds % 60
    )
}

/**
 * Returns the number duration in mm:ss
 */
fun Int.getFormattedDuration(unit: DurationUnit) : String{
    val duration = this.toDuration(unit)

    return "%02d:%02d".format(
        duration.inWholeMinutes,
        duration.inWholeSeconds % 60
    )
}