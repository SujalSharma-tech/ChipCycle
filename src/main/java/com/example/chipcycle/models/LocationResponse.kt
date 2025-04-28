package com.example.chipcycle.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val state: String = "",
    val country: String = "",
    val city: String = "",
    val zipCode: String = ""
)



@Parcelize
data class LocationResponseCenters(
    val id: String,
    val state: String,
    val centername: String,
    val address: String,
    val isContactAvailable: Boolean,
    val contactonly1: Number?,
    val lat: Double?,
    val long: Double?
) : Parcelable
data class LocationByStateData(
    val state : String
)

data class ResponseData(
    val success: Boolean,
    val centersList : List<LocationResponseCenters>
)

data class MapsCoordinates(
    val longitude: Double,
    val latitude: Double
)
