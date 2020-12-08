package yayuki.heartrate.android.patient.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import yayuki.heartrate.patient.data.Location
import yayuki.heartrate.patient.data.LocationDataSource


class GoogleGps(private val ctx: Context) : LocationDataSource {
    override var isConnect: Boolean = false
        private set
    private val _locationFlow = MutableSharedFlow<Location>(1, 1, BufferOverflow.DROP_OLDEST)
    override val locationFlow: SharedFlow<Location>
        get() = _locationFlow.asSharedFlow()

    @SuppressLint("MissingPermission")
    override suspend fun connect() {
        _locationFlow.tryEmit(Location(0f, .0, .0))
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 5000
        }
        val client = LocationServices.getFusedLocationProviderClient(ctx)
        client.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
            .addOnFailureListener {
                Log.e("GPS", it.toString())
            }
    }

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.minByOrNull { it.accuracy }?.run {
                runBlocking {
                    _locationFlow.emit(Location(accuracy, latitude, longitude))
                }
            }
        }
    }
}