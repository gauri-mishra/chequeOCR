package com.example.chequeocr.models

interface OcrCallBack {
    fun onSuccess(value: Result<ChequeData>)

}

