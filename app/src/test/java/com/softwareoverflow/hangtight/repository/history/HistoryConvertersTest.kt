package com.softwareoverflow.hangtight.repository.history

import com.softwareoverflow.hangtight.repository.room.history.HistoryConverters
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HistoryConvertersTest {

    private val converter = HistoryConverters()

    @Test
    fun historyConverters_DateToStringAndBack(){
        val dateObj = LocalDate.of(2022, 8,13) // 13/08/2022

        val dateString = converter.fromDate(dateObj)

        assertEquals("Date should match!", "20220813", dateString)

        val revertedDate = converter.fromString(dateString)
        assertEquals(dateObj, revertedDate)
    }

    @Test
    fun historyConverters_StringToDateAndBack(){
        val string = "20231217" // 17/12/2023

        val date = converter.fromString(string)

        assertEquals(LocalDate.of(2023, 12, 17), date)

        val stringReverted = converter.fromDate(date)
        assertEquals(string, stringReverted)
    }
}