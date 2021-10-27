package com.example.ocr.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ocr.models.Response
import com.example.ocr.api.RetrofitClient
import com.example.ocr.models.ChequeData
import com.example.ocr.models.OcrCallBack
import retrofit2.Call
import retrofit2.Callback


object Repository {

    private var _currentResponse: MutableLiveData<Response> = MutableLiveData()
    val currentResponse: LiveData<Response>
        get() = _currentResponse


    fun  getBank(ifsc: String, accountNo: String, ocrCallBack: OcrCallBack){

        val apiClient = RetrofitClient().getInstance()

        val response = apiClient.getData(ifsc)
        response.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {

                Log.i("SOSTag", "SMS Success")
                Log.i("SOSTag", response.body().toString())
                _currentResponse.postValue(response.body())
                var bank = response.body()?.BANK
                if(bank == null)
                    bank = ""
                val chequeData = ChequeData(bank,ifsc,accountNo)
                Log.i("sdasd", chequeData.BANK)
                ocrCallBack.onSuccess(Result.success(chequeData))

            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.i("SOSTag", "SMS Failed due to ?")
                val chequeData = ChequeData("",ifsc,accountNo)
                ocrCallBack.onSuccess(Result.success(chequeData))

            }
        })

    }


}