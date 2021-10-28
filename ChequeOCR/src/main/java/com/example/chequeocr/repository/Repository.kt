package com.example.chequeocr.repository


import com.example.chequeocr.api.RetrofitClient
import com.example.chequeocr.models.ChequeData
import com.example.chequeocr.models.OcrCallBack
import com.example.chequeocr.models.Response
import retrofit2.Call
import retrofit2.Callback


object Repository {


    /**
    get Bank will call the api to  get Bank name using IFSCode
    OcrCallback will be used to capture res
     * */
    fun getBank(ifsc: String, accountNo: String, ocrCallBack: OcrCallBack) {

        val apiClient = RetrofitClient().getInstance()

        val response = apiClient.getData(ifsc)
        response.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {

//                Log.d("OCRTag", "API Success")
//                Log.("OCRTag", response.body().toString())
//                Log.i("OCRTag", ifsc)
                var bank = response.body()?.BANK
                if (bank == null)
                    bank = ""
                val chequeData = ChequeData(bank, ifsc, accountNo)
                ocrCallBack.onSuccess(Result.success(chequeData))

            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                //Log.i("OCRTag", "API failed")
                // in case of failure we return bank as null
                val chequeData = ChequeData("", ifsc, accountNo)
                ocrCallBack.onSuccess(Result.success(chequeData))

            }
        })

    }


}