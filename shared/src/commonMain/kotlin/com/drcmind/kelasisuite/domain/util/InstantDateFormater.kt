package com.drcmind.kelasisuite.domain.util

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@OptIn(FormatStringsInDatetimeFormats::class)
private val dateTimeFormatter = LocalDateTime.Format {
    byUnicodePattern("dd/MM/yyyy HH:mm")
}

@OptIn(FormatStringsInDatetimeFormats::class)
private val dateFormatter = LocalDate.Format {
    byUnicodePattern("dd/MM/yyyy")
}

fun Instant.toDdMmYyyyWithTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    return this
        .toLocalDateTime(timeZone)
        .format(dateTimeFormatter)
}



fun Instant.toDdMmYyyy(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    return this
        .toLocalDateTime(timeZone)
        .date
        .format(dateFormatter)
}

fun DayOfWeek.toFrench(): String = when (this) {
    DayOfWeek.MONDAY -> "Lundi"
    DayOfWeek.TUESDAY -> "Mardi"
    DayOfWeek.WEDNESDAY -> "Mercredi"
    DayOfWeek.THURSDAY -> "Jeudi"
    DayOfWeek.FRIDAY -> "Vendredi"
    DayOfWeek.SATURDAY -> "Samedi"
    DayOfWeek.SUNDAY -> "Dimanche"
}

fun kotlinx.datetime.Month.toFrench(): String = when (this) {
    kotlinx.datetime.Month.JANUARY -> "Janvier"
    kotlinx.datetime.Month.FEBRUARY -> "Février"
    kotlinx.datetime.Month.MARCH -> "Mars"
    kotlinx.datetime.Month.APRIL -> "Avril"
    kotlinx.datetime.Month.MAY -> "Mai"
    kotlinx.datetime.Month.JUNE -> "Juin"
    kotlinx.datetime.Month.JULY -> "Juillet"
    kotlinx.datetime.Month.AUGUST -> "Août"
    kotlinx.datetime.Month.SEPTEMBER -> "Septembre"
    kotlinx.datetime.Month.OCTOBER -> "Octobre"
    kotlinx.datetime.Month.NOVEMBER -> "Novembre"
    kotlinx.datetime.Month.DECEMBER -> "Décembre"
    else -> this.name
}

val dateFormatterOnlyDay by lazy {
    LocalDate.Format {
        day(padding = Padding.ZERO)
    }
}