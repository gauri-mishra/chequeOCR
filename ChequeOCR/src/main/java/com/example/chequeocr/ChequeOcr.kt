package com.example.chequeocr

import android.graphics.Bitmap
import com.example.chequeocr.models.ChequeData
import com.example.chequeocr.models.OcrCallBack
import com.example.chequeocr.repository.Repository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class ChequeOcr(val image: Bitmap) {
    var chequeData = ChequeData("", "", "")

    /**
     * this function calls ML toolkit ocr on the bitmap of the image
     * Results of the textRecognition are fetched using the callback
     *
     * */
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

    /**
     * this is a 2 part function
     * first the extractInformation is called to get the IFSCode and Account Number from
     * the image.  ExtractInformation returns a callback
     * When we have  Success we  call the second function to fetch bank name from ifsc code
     * this also returns a callback
     * */
    fun getChequeDetails(ocrCallBack: OcrCallBack) {
        extractInformation(object : OcrCallBack {
            override fun onSuccess(value: Result<ChequeData>) {
                val ifsc = value.getOrNull()?.IFSC
                val accountNo = value.getOrNull()?.AccountNo
                var bank = ""

                Repository.getBank(ifsc!!, accountNo!!, object : OcrCallBack {
                    override fun onSuccess(value2: Result<ChequeData>) {
                        bank = value2.getOrNull()?.BANK!!

                        val chequeData = ChequeData(ifsc, accountNo!!, bank)
                        ocrCallBack.onSuccess(Result.success(chequeData))
                    }

                })

            }


        })
    }
}