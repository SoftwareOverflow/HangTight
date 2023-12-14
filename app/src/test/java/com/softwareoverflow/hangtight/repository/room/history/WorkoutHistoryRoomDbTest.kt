package com.softwareoverflow.hangtight.repository.room.history

import com.softwareoverflow.hangtight.data.WorkoutHistory
import com.softwareoverflow.hangtight.repository.room.dao.WorkoutHistoryDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.LocalDate


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WorkoutHistoryRoomDbTest {

    @Test
    fun createOrUpdate_New() {
        val mockDao = mock<WorkoutHistoryDao> {
            onBlocking {
                getHistoryBetweenDates(
                    any(),
                    any()
                )
            } doReturn listOf(WorkoutHistoryEntity())
        }

        val classUnderTest = WorkoutHistoryRoomDb(mockDao)

        runTest {
            classUnderTest.createOrUpdate(WorkoutHistory(5, 3, 2, LocalDate.of(2023, 7, 25)))

            val expectedHistory = WorkoutHistoryEntity(5, 3, 2, LocalDate.of(2023, 7, 25))
            verify(mockDao).createOrUpdate(expectedHistory)
        }
    }

    @Test
    fun createOrUpdate_Update() {
        val mockDao = mock<WorkoutHistoryDao> {
            onBlocking { getHistoryBetweenDates(any(), any()) } doReturn listOf(
                WorkoutHistoryEntity(
                    3, 5, 7, LocalDate.of(2023, 7, 25)
                )
            )
        }

        val classUnderTest = WorkoutHistoryRoomDb(mockDao)

        runTest {
            classUnderTest.createOrUpdate(WorkoutHistory(1, 2, 3, LocalDate.of(2023, 7, 25)))

            val expectedHistory = WorkoutHistoryEntity(4, 7, 10, LocalDate.of(2023, 7, 25))
            verify(mockDao).createOrUpdate(expectedHistory)
        }
    }

    @Test
    fun getAllHistory() {
        val entityList = listOf(
            WorkoutHistoryEntity(
                3, 5, 7, LocalDate.of(2023, 7, 25)
            ),
            WorkoutHistoryEntity(
                1, 3, 157, LocalDate.of(2023, 6, 18)
            ),
            WorkoutHistoryEntity(
                100, 35, 200, LocalDate.of(2022, 9, 1)
            ),
            WorkoutHistoryEntity(
                17202, 355423, 203151, LocalDate.of(2022, 8, 11)
            ),
            WorkoutHistoryEntity(
                10460, 35546, 206530, LocalDate.of(2022, 8, 3)
            ),
        )

        val mockDao = mock<WorkoutHistoryDao> {
            onBlocking { getAllHistory() } doReturn entityList
        }

        runTest {
            val classUnderTest = WorkoutHistoryRoomDb(mockDao)


            val expected = entityList.toDto()

            val result = classUnderTest.getAllHistory()
            result.toList().forEachIndexed() { index, value ->
                assert(expected[index] == value)
            }
        }
    }

    @Test
    fun getHistoryBetweenDates() = runTest {
        // All we can test with this is that it calls toDto correctly
        val entityList = listOf(
            WorkoutHistoryEntity(
                3, 5, 7, LocalDate.of(2023, 7, 25)
            ),
            WorkoutHistoryEntity(
                1, 3, 157, LocalDate.of(2023, 6, 18)
            ),
            WorkoutHistoryEntity(
                100, 35, 200, LocalDate.of(2022, 9, 1)
            ),
            WorkoutHistoryEntity(
                17202, 355423, 203151, LocalDate.of(2022, 8, 11)
            ),
            WorkoutHistoryEntity(
                10460, 35546, 206530, LocalDate.of(2022, 8, 3)
            ),
        )

        val mockDao = mock<WorkoutHistoryDao> {
            onBlocking { getHistoryBetweenDates(any(), any()) } doReturn entityList
        }

        val classUnderTest = WorkoutHistoryRoomDb(mockDao)


        val expected = entityList.toDto()

        val result = classUnderTest.getHistoryBetweenDates(
            LocalDate.of(2023, 12, 1),
            LocalDate.of(2023, 12, 1)
        )
        assertEquals(expected, result)
    }
}