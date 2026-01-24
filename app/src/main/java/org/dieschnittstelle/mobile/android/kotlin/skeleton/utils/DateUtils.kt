package org.dieschnittstelle.mobile.android.kotlin.skeleton.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
            .withLocale(Locale.GERMANY)
            .withZone(ZoneId.systemDefault())

    fun formatDate(timestamp: Long): String =
        formatter.format(Instant.ofEpochMilli(timestamp))
}
