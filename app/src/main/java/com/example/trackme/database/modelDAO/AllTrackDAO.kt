package com.example.trackme.database.modelDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trackme.model.Tracks
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface AllTrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTracks(tracks: Tracks): Completable

    @Query("SELECT * FROM Tracks")
    fun getAllTracks() : Observable<List<Tracks>>
}