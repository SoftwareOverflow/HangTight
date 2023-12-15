package com.softwareoverflow.hangtight.repository.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.softwareoverflow.hangtight.repository.room.dao.WorkoutHistoryDao
import com.softwareoverflow.hangtight.repository.room.history.WorkoutHistoryDatabase
import com.softwareoverflow.hangtight.repository.room.history.WorkoutHistoryEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class WorkoutHistoryDaoTest {
    private lateinit var dao: WorkoutHistoryDao
    private lateinit var db: WorkoutHistoryDatabase

    private val mockData = listOf(
        WorkoutHistoryEntity(1, 290, 3135623, LocalDate.of(2022, 9, 1)),
        WorkoutHistoryEntity(123, 2923, 346, LocalDate.of(2022, 9, 6)),
        WorkoutHistoryEntity(12, 2976, 3236, LocalDate.of(2022, 9, 17)),
        WorkoutHistoryEntity(5, 2769, 33, LocalDate.of(2022, 10, 25)),
        WorkoutHistoryEntity(7651, 246, 386, LocalDate.of(2023, 2, 13)),
        WorkoutHistoryEntity(168, 2654, 331, LocalDate.of(2023, 2, 27)),
        WorkoutHistoryEntity(153, 23532, 311, LocalDate.of(2023, 3, 10))
    )

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WorkoutHistoryDatabase::class.java
        ).build()

        dao = db.workoutHistoryDao
    }

    @After
    fun closeDb() {
        db.close()
    }

    private fun insertData() = runBlocking {
        mockData.forEach {
            dao.createOrUpdate(it)
        }
    }

    @Test
    fun createOrUpdate_New() = runTest {

        val dataToInsert = mockData[0]

        dao.createOrUpdate(dataToInsert)

        val data = dao.getAllHistory().single()

        assertEquals(dataToInsert, data)
    }

    @Test
    fun createOrUpdate_Update() = runTest {
        val dataToInsert = mockData[0]

        dao.createOrUpdate(dataToInsert)

        val updatedValue = dataToInsert.copy(7, 5, 2, dataToInsert.date)

        dao.createOrUpdate(updatedValue)

        val data = dao.getAllHistory().single()

        assertEquals(updatedValue, data)

    }

    @Test
    fun getAllHistory() = runTest {
        insertData()

        val result = dao.getAllHistory()
        assertEquals(mockData, result)
    }

    @Test
    fun getAllHistory_SortedByDate() = runTest {

        val first = WorkoutHistoryEntity(1, 1, 1, LocalDate.of(2023, 1, 1))
        val second = WorkoutHistoryEntity(1, 1, 1, LocalDate.of(2023, 1, 2))
        val third = WorkoutHistoryEntity(1, 1, 1, LocalDate.of(2023, 1, 3))
        val fourth = WorkoutHistoryEntity(1, 1, 1, LocalDate.of(2023, 1, 4))

        dao.createOrUpdate(fourth)
        dao.createOrUpdate(second)
        dao.createOrUpdate(first)
        dao.createOrUpdate(third)


        val result = dao.getAllHistory()
        assertEquals(first, result[0])
        assertEquals(second, result[1])
        assertEquals(third, result[2])
        assertEquals(fourth, result[3])
    }

    @Test
    fun getHistoryBetweenDates() = runTest{
        insertData()

        val result = dao.getHistoryBetweenDates("20220917", "20230220")

        val expected = mockData.subList(2, 5)
        assertEquals(expected, result)
    }

    @Test
    fun getHistoryBetweenDates_SpecificDay() = runTest{
        insertData()

        val result = dao.getHistoryBetweenDates("20220917", "20220917").single()

        assertEquals(mockData[2], result)
    }
}

