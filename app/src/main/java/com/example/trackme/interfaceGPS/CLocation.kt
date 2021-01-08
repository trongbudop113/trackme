package com.example.trackme.interfaceGPS

import android.location.Location

class CLocation constructor(location: Location?) : Location(location) {

    override fun distanceTo(dest: Location): Float {
        var nDistance = super.distanceTo(dest)
        nDistance *= 3.28083989501312f
        return nDistance
    }

    override fun getAccuracy(): Float {
        var nAccuracy = super.getAccuracy()
        nAccuracy *= 3.28083989501312f
        return nAccuracy
    }

    override fun getAltitude(): Double {
        var nAltitude = super.getAltitude()
        nAltitude *= 3.28083989501312
        return nAltitude
    }

    override fun getSpeed(): Float {
        return super.getSpeed() * 2.2369362920544f / 3.6f
    }
}