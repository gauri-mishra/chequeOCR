package com.example.chequeocr.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://ifsc.razorpay.com"


class RetrofitClient {

    private var apiCall: ApiInterface? = null
    fun getInstance(): ApiInterface {

        if (apiCall == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiCall = retrofit.create(ApiInterface::class.java)
        }
        return apiCall!!
    }


}
