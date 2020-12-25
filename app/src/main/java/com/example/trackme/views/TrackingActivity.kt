package com.example.trackme.views

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackme.R
import com.example.trackme.common.Common
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_tracking.*


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var fusedLocation : FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setListener()

        checkRunTimePermission()
    }

    private fun setListener() {
        btnPause.setOnClickListener {

        }

        btnFresh.setOnClickListener {

        }

        btnStop.setOnClickListener {

        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener {
            val sydney = LatLng(it.latitude, it.longitude)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun checkRunTimePermission() {
        if (checkPermission()) {
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
        fusedLocation?.lastLocation?.addOnCompleteListener {
            val location = it.result
            if (location != null){
                val sydney = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f))
            }
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
}