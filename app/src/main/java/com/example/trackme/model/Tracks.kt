package com.example.trackme.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "Tracks")
data class Tracks (

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "DailyCheckId")
    var id: Int? = null,

    @ColumnInfo(name = "StartLocation")
    var startLocation: LatLng? = null,

    @ColumnInfo(name = "EndLocation")
    var endLocation: LatLng? = null,

    @ColumnInfo(name = "Status")
    var distance : Int? = null,

    @ColumnInfo(name = "RequestAt")
    var averageSpeed : Double? = null,

    @ColumnInfo(name = "ExpiredAt")
    var time : Double? = null

) {
    constructor() : this(null, null, null, 0, 0.0, 0.0)
}