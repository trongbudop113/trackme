package com.example.trackme.database.modelDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.trackme.model.Tracks
import io.reactivex.Completable

@Dao
interface AllTrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTracks(tracks: Tracks): Completable
}