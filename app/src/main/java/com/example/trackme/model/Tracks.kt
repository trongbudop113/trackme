package com.example.trackme.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "Tracks")
data class Tracks (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DailyCheckId")
    var id: Int? = null,

    @ColumnInfo(name = "StartLat")
    var startLat: Double? = null,

    @ColumnInfo(name = "StartLng")
    var startLng: Double? = null,

    @ColumnInfo(name = "EndLat")
    var endLat: Double? = null,

    @ColumnInfo(name = "EndLng")
    var endLng: Double? = null,

    @ColumnInfo(name = "Status")
    var distance : Int? = null,

    @ColumnInfo(name = "RequestAt")
    var averageSpeed : Double? = null,

    @ColumnInfo(name = "ExpiredAt")
    var time : Long? = null

) {
    constructor() : this(null, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0L)
}