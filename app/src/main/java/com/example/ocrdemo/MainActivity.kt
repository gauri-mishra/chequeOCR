package com.example.ocrdemo

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.ocr.ChequeOcr
import com.example.ocr.models.ChequeData
import com.example.ocr.models.OcrCallBack
import com.example.ocr.repository.Repository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sbiBitmap  = BitmapFactory.decodeResource(resources,R.drawable.c9)
        val bobBitmap  = BitmapFactory.decodeResource(resources,R.drawable.c10)
        val formBitmap  = BitmapFactory.decodeResource(resources,R.drawable.bob)
        val data = findViewById<TextView>(R.id.text)

        val sbi = findViewById<Button>(R.id.sbibutton)
        var cheque = ChequeData("","","")
        sbi.setOnClickListener {
                var cheque =     ChequeOcr(sbiBitmap).getChequeDetails(object : OcrCallBack{
                    override fun onSuccess(value: Result<ChequeData>) {
                        data.text = value.getOrNull()?.IFSC +"\n"+ value.getOrNull()?.AccountNo+"\n"+ value.getOrNull()?.BANK
                    }

                    override fun onFailure() {
                        TODO("Not yet implemented")
                    }

                })

                }



        val bob = findViewById<Button>(R.id.bob)
        bob.setOnClickListener {
        var cheque =     ChequeOcr(bobBitmap).getChequeDetails(object : OcrCallBack{
            override fun onSuccess(value: Result<ChequeData>) {
                data.text = value.getOrNull()?.IFSC +"\n"+ value.getOrNull()?.AccountNo+"\n"+ value.getOrNull()?.BANK
            }

            override fun onFailure() {
                TODO("Not yet implemented")
            }

        })

    }

            }


        }





