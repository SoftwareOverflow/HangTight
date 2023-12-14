package com.softwareoverflow.hangtight.repository.room.history

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryConverters {

    @TypeConverter
    fun fromDate(date: LocalDate) : String {
        return historyDateFormat.format(date)
    }

    @TypeConverter
    fun fromString(dateString: String) : LocalDate {
        return LocalDate.parse(dateString, historyDateFormat)
    }

    companion object {
        val historyDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}