package com.drcmind.kelasisuite.domain.util

import kotlinx.datetime.LocalTime

fun durationBetween(start: LocalTime, end: LocalTime): Pair<Int, Int> {
    val startMinutes = start.hour * 60 + start.minute
    val endMinutes = end.hour * 60 + end.minute

    val diffMinutes = if (endMinutes >= startMinutes) {
        endMinutes - startMinutes
    } else {
        (24 * 60 - startMinutes) + endMinutes
    }

    return Pair(
        diffMinutes / 60,
        diffMinutes % 60
    )
}