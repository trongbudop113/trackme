package com.example.trackme.views

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackme.R
import com.example.trackme.common.Common
import com.example.trackme.common.DirectionJSONParser
import com.example.trackme.interfaceGPS.CLocation
import com.example.trackme.interfaceGPS.IBaseGpsListener
import com.example.trackme.remote.IGeoCoordinates
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_tracking.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback, IBaseGpsListener {

    var mMap: GoogleMap? = null
    private var fusedLocation : FusedLocationProviderClient? = null
    var mLastLocation: Location? = null
    var locationCallback: LocationCallback? = null
    var currentMarker: Marker? = null
    var polyline: Polyline? = null
    var mServices: IGeoCoordinates? = null
    var locationRequest: LocationRequest? = null
    var moveMarker : Marker? = null
    var currentSpeed = 0.0
    var currentLocation : LatLng? = null
    private var manager : LocationManager? = null

    private var pauseOffset: Long = 0
    private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mServices = Common.getGeoCodeService()
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setListener()

        setChronometer()

        checkRunTimePermission()
    }

    @SuppressLint("SetTextI18n")
    private fun setChronometer() {
        timeCount.format = "00:%s"
        timeCount.base = SystemClock.elapsedRealtime()
        timeCount.setOnChronometerTickListener { chronometer ->
            val time: Long = SystemClock.elapsedRealtime() - chronometer.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            val hh = if (h < 10) "0$h" else h.toString() + ""
            val mm = if (m < 10) "0$m" else m.toString() + ""
            val ss = if (s < 10) "0$s" else s.toString() + ""
            chronometer.text = "$hh:$mm:$ss"
        }

        if (!running) {
            timeCount.base = SystemClock.elapsedRealtime() - pauseOffset
            timeCount.start()
            running = true
        }
    }

    private fun setListener() {
        btnPause.setOnClickListener {
            changeStatusFromPauseToStop()
            if (running) {
                timeCount.stop()
                pauseOffset = SystemClock.elapsedRealtime() - timeCount.base
                running = false
            }
        }

        btnFresh.setOnClickListener {
            changeStatusFromRefreshToPause()
            if (!running) {
                timeCount.base = SystemClock.elapsedRealtime() - pauseOffset
                timeCount.start()
                running = true
            }
        }

        btnStop.setOnClickListener {
            stopTracking()
        }
    }

    private fun changeStatusFromPauseToStop(){
        btnPause.visibility = View.GONE
        layoutRefresh.visibility = View.VISIBLE
    }

    private fun changeStatusFromRefreshToPause(){
        btnPause.visibility = View.VISIBLE
        layoutRefresh.visibility = View.GONE
    }

    private fun stopTracking(){
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun checkRunTimePermission() {
        if (checkPermission()) {
            manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!manager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
                buildAlertMessageNoGps()
            }else{
                getLocation()
            }
        } else {
            requestPermission()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes"
            ) { dialog, id ->
                dialog.dismiss()
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1111)
            }
            .setNegativeButton("No"
            ) { dialog, id -> finish() }
        val alert = builder.create()
        alert.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1111){
            checkRunTimePermission()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        manager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        this.updateSpeed(null)
        fusedLocation?.lastLocation?.addOnCompleteListener {
            val location = it.result
            if (location != null){
                currentLocation = LatLng(location.latitude, location.longitude)
                mMap?.addMarker(MarkerOptions().position(currentLocation!!).title("Marker in Sydney"))
                mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                mMap?.animateCamera(CameraUpdateFactory.zoomTo(15.0f))
                buildLocationRequest()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSpeed(location: CLocation?) {
        var nCurrentSpeed = 0f
        if (location != null) {
            nCurrentSpeed = location.speed
        }
        val fmt = Formatter(StringBuilder())
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed)
        var strCurrentSpeed: String = fmt.toString()
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0')
        val strUnits = " km/h"
        txtSpeedNumber.text = "$strCurrentSpeed $strUnits"
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.smallestDisplacement = 10f
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 3000
    }

    @SuppressLint("CheckResult")
    private fun drawRoute(requests : LatLng) {
        if (polyline != null) polyline?.remove()
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.box)
        bitmap = Common.scaleBitmap(bitmap, 70, 70)
        val marker = MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title("Order of ABC").position(
            LatLng(mLastLocation?.latitude!!, mLastLocation?.longitude!!)
        )
        if (moveMarker != null) moveMarker?.remove()
        moveMarker = mMap?.addMarker(marker)
        mServices?.getDirections(
             requests.latitude.toString() + "," + requests.longitude.toString(),
            mLastLocation?.latitude.toString() + "," + mLastLocation?.longitude.toString(),
            Common.KEY_MAP
        )?.enqueue(object : Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@TrackingActivity, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                ParserTask().execute(response.body().toString())
                ParserDistance().execute(response.body().toString())
            }

        })
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        override fun doInBackground(vararg p0: String?): List<List<HashMap<String, String>>> {
            val jsonObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jsonObject = JSONObject(p0[0]!!)
                val parser = DirectionJSONParser()
                routes = parser.parse(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return routes!!
        }

        override fun onPostExecute(lists: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng>
            var lineOptions: PolylineOptions? = null
            for (i in lists!!.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                val path = lists[i]
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(Color.BLUE)
                lineOptions.geodesic(true)
            }
            mMap?.addPolyline(lineOptions)
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class ParserDistance : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val jsonObject: JSONObject
            var distance = "-- km"
            try {
                jsonObject = JSONObject(p0[0]!!)
                val a = ((JSONArray(jsonObject.getString("routes")).get(0) as JSONObject).getJSONArray("legs").get(0) as JSONObject).getJSONObject("distance").get("text")
                distance = a.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return distance
        }

        override fun onPostExecute(distance: String?) {
            txtDistanceNumber.text = distance
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun checkPermission(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        val result2 = ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION), Common.REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Common.REQUEST_CODE) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    val showRationale = shouldShowRequestPermissionRationale(permission)
                    if (showRationale) {
                        showAlertView()
                    }
                } else {
                    checkRunTimePermission()
                }
            }
        }
    }

    private fun showAlertView() {
        val dialog =
            AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog_Alert)
        dialog.setTitle(getString(R.string.permission_denied))
        dialog.setInverseBackgroundForced(true)
        dialog.setMessage(getString(R.string.permission_warning))
        dialog.setNegativeButton(
            getString(R.string.sure)
        ) { dialogInterface, i -> dialogInterface.dismiss() }
        dialog.setPositiveButton(getString(R.string.retry)) { dialogInterface, i ->
            dialogInterface.dismiss()
            checkRunTimePermission()
        }
        dialog.show()
    }

    var recentGPSLocationSegments = listOf<Pair<Location, Location>>()

    fun applyWeightedMovingAverageSpeed(location: Location, previous: Location): Double
    {
        recentGPSLocationSegments = recentGPSLocationSegments + Pair(location, previous)
        val cachedLocationsNs = location.elapsedRealtimeNanos - 4500000000 // 4.5 seconds, This will typically get 4 entries (1 second apart)
        val targetZeroWeightNs = location.elapsedRealtimeNanos - 5000000000 // 5.0 seconds, Weights will be approx 5000000000, 4000000000, 3000000000, 1000000000

        // Toss old locations
        recentGPSLocationSegments = recentGPSLocationSegments.filter { it -> it.first.elapsedRealtimeNanos > cachedLocationsNs }

        // Total up all the weights. Weight is based on age, younger has higher weight
        val weights = recentGPSLocationSegments.map { it.first.elapsedRealtimeNanos - targetZeroWeightNs }.sum()

        // Apply the weights and get average speed in meters/second
        return recentGPSLocationSegments.map { speedFromGPS(it.first, it.second) * (it.first.elapsedRealtimeNanos - targetZeroWeightNs) }.sum() / weights
    }

    private fun speedFromGPS(location: Location, previous: Location): Double
    {
        val dist = location.distanceTo(previous)
        val time = (location.elapsedRealtimeNanos - previous.elapsedRealtimeNanos) / 1000000000.0
        return dist / time
    }

    val locationManagerExample: LocationListener = object : LocationListener
    {
        var lastLocation: Location? = null
        var lastPreviousLocation: Location? = null

        override fun onLocationChanged(location: Location?)
        {
            if (location != null)
            {
                if (lastPreviousLocation != null)
                {
                    currentSpeed = applyWeightedMovingAverageSpeed(location, lastPreviousLocation!!)

                    lastPreviousLocation = lastLocation
                }

                lastLocation = location

                if (currentSpeed < 0.0)
                {
                    currentSpeed = 0.0
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val myLocation = CLocation(location)
        updateSpeed(myLocation)
        mLastLocation = location
        if (currentMarker != null){
            currentMarker?.position = LatLng(
                mLastLocation?.latitude!!,
                mLastLocation?.longitude!!
            )
        }
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(
            LatLng(
                mLastLocation?.latitude!!,
                mLastLocation?.longitude!!
            )
        )
        )
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(16.0f))
        drawRoute(currentLocation!!)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onGpsStatusChanged(event: Int) {

    }

    override fun onProviderDisabled(provider: String) {

    }
}