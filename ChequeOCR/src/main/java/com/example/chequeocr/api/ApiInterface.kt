package com.example.chequeocr.api


import com.example.chequeocr.models.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("{ifsc}")
    fun getData(
        @Path("ifsc") details: String,
    ): Call<Response>


}