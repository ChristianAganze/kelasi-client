package com.drcmind.kelasisuite.domain.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
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