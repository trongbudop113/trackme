package com.example.trackme.remote

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IGeoCoordinates {
    @GET("maps/api/geocode/json")
    fun getGeoCode(@Query("address") address: String?): Call<String?>?

    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("key") key : String?
    ): Call<String>
}