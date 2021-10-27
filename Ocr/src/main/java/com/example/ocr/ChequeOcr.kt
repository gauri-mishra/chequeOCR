package com.example.ocr

import android.graphics.Bitmap
import android.util.Log
import com.example.ocr.models.ChequeData
import com.example.ocr.models.OcrCallBack
import com.example.ocr.repository.Repository
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChequeOcr( val image: Bitmap) {
     var chequeData =  ChequeData("","","")
     fun extractInformation(ocrCallBack: OcrCallBack) {
        val image = InputImage.fromBitmap(image, 0)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                chequeData = ocrProcessor().process(visionText)
                ocrCallBack.onSuccess(Result.success(chequeData))

            }
            .addOnFailureListener {
//                ocrCallBack.onFailure()
            }


    }
    fun getChequeDetails(ocrCallBack: OcrCallBack)
    {
        extractInformation(object  :OcrCallBack{
            override fun onSuccess(value: Result<ChequeData>) {
                val ifsc = value.getOrNull()?.IFSC
                val accountNo = value.getOrNull()?.AccountNo
                var bank =""

                Repository.getBank(ifsc!!, accountNo!!,object :OcrCallBack{
                    override fun onSuccess(value2: Result<ChequeData>) {
                         bank= value2.getOrNull()?.BANK!!
                        Log.d("bank found",bank)
                        val chequeData = ChequeData(ifsc,accountNo!!, bank)
                        ocrCallBack.onSuccess(Result.success(chequeData))
                    }

                    override fun onFailure() {
//                        val chequeData = ChequeData(ifsc!!,accountNo!!, bank)
//                       ocrCallBack.onSuccess(Result.success(chequeData))
                    }

                })

            }

            override fun onFailure() {
//               ocrCallBack.onFailure()
            }


        })
    }
}