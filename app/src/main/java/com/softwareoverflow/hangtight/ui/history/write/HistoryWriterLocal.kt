package com.softwareoverflow.hangtight.ui.history.write

import com.softwareoverflow.hangtight.data.WorkoutHistory
import com.softwareoverflow.hangtight.repository.room.history.WorkoutHistoryRoomDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryWriterLocal @Inject constructor (private val db: WorkoutHistoryRoomDb) : IHistoryWriter {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun writeHistory(obj: WorkoutHistory) {
        coroutineScope.launch {
            db.createOrUpdate(obj)
        }
    }
}