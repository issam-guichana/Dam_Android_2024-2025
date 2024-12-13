package com.example.gourmetia.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// 10.0.2.2 adresse locale de l'émulateur

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3002/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: UserAPI by lazy {
        retrofit.create(UserAPI::class.java)
    }
}
