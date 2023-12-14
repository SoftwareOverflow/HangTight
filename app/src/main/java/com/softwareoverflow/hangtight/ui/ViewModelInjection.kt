package com.softwareoverflow.hangtight.ui

import com.softwareoverflow.hangtight.repository.room.history.WorkoutHistoryRoomDb
import com.softwareoverflow.hangtight.ui.history.write.HistorySaverLocal
import com.softwareoverflow.hangtight.ui.history.write.HistoryWriterLocal
import com.softwareoverflow.hangtight.ui.history.write.IHistorySaver
import com.softwareoverflow.hangtight.ui.history.write.IHistoryWriter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelInjection {

    @Provides
    @ViewModelScoped
    fun providesHistoryWriter(db: WorkoutHistoryRoomDb) : IHistoryWriter {
        return HistoryWriterLocal(db)
    }

    @Provides
    @ViewModelScoped
    fun providesHistorySaver(writer: IHistoryWriter) : IHistorySaver {
        return HistorySaverLocal(writer)
    }
}