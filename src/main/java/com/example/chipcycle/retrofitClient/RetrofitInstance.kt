package com.example.chipcycle.retrofitClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {

//        var BaseURL = "http://10.35.143.96:8000"
       var BaseURL = "https://chipcycle-backend-c8b00cfc36f3.herokuapp.com"
        Retrofit.Builder().baseUrl("${BaseURL}/").addConverterFactory(GsonConverterFactory.create()).build()

    }

    val apiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}