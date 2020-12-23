package com.example.trackme.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackme.R
import com.example.trackme.common.Common
import com.example.trackme.model.Tracks
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

internal class TrackingAdapter(private val tracksList: List<Tracks>, val onItemSelectedListener : OnItemSelected, val context : Context) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), OnMapReadyCallback {

        var mapView: MapView = view.findViewById(R.id.mapView)
        var distance: TextView = view.findViewById(R.id.txtDistanceNumber)
        var speed: TextView = view.findViewById(R.id.txtSpeedNumber)
        var time: TextView = view.findViewById(R.id.txtTimeNumber)

        var map: GoogleMap? = null

        override fun onMapReady(googleMap: GoogleMap?) {
            MapsInitializer.initialize(context)
            map = googleMap
            val data = mapView.tag as Tracks
            setMapLocation(map!!, data)
        }

        fun initializeMapView() {
            mapView.onCreate(null)
            mapView.getMapAsync(this)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracksList[position]
        holder.initializeMapView()
        holder.distance.text = "" + track.distance + " km"
        holder.speed.text = "" + track.averageSpeed + " km/h"
        holder.time.text = Common.convertMillisecondToTime(track.time!!)

        holder.mapView.tag = track
        if (holder.map != null) {
            setMapLocation(holder.map, track)
        }

        holder.itemView.setOnClickListener {
            onItemSelectedListener.onSelectedTracking(track)
        }
    }
    override fun getItemCount(): Int {
        return tracksList.count()
    }

    interface OnItemSelected{
        fun onSelectedTracking(item : Tracks)
    }

    companion object {
        private fun setMapLocation(mMap: GoogleMap?, track: Tracks?) {
            val startLocation = LatLng(track?.startLat!!, track.startLng!!)
            val endLocation = LatLng(track.endLat!!, track.endLng!!)
            mMap?.addMarker(MarkerOptions().position(startLocation).title("Marker in start"))
            mMap?.addMarker(MarkerOptions().position(endLocation).title("Marker in end"))
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(startLocation))
            mMap?.animateCamera(CameraUpdateFactory.zoomTo(10.0f))

            mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

}