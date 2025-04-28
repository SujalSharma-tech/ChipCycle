package com.example.chipcycle.retrofitClient

import com.example.chipcycle.models.LocationByStateData
import com.example.chipcycle.models.LocationData
import com.example.chipcycle.models.ResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @POST("location")
    fun sendLocation(@Body locationData: LocationData): Call<ResponseData>

    @POST("locationbystate")
    fun sendState(@Body locationData : LocationByStateData) : Call<ResponseData>

    @POST("searchlocation")
    fun sendUserSearch(@Body locationData : LocationByStateData) : Call<ResponseData>

}