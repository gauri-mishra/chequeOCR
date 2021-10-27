package com.example.ocr

import android.util.Log
import com.example.ocr.models.ChequeData
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.text.Text
import java.lang.Math.min

class ocrProcessor  {
  var OcrText: ArrayList<String> = ArrayList(0)

    fun process(visionText : Text): ChequeData {
        clean()
    val blocks: List<Text.TextBlock> = visionText.textBlocks
    var temp = ""
    for (i in blocks.indices) {

        var blockstring = ""
        val lines = blocks[i].lines
        for (j in lines.indices) {
            val elements = lines[j].elements
            for (k in elements.indices) {

                blockstring+= elements[k].text

            }
        }
        temp+=blockstring
        temp+="\n"

        OcrText.add(blockstring)
    }
        Log.d("ocr",temp)
       return searchOcrText()

    }

    private fun clean() {
       isIfsc = false
        isAccount = false
    }

    private fun searchOcrText() :ChequeData {
        val chequeData = ChequeData("","","");

        for(i in OcrText.indices)
        {
            val ifs = matchDetails(OcrText[i],"ifs",0)
            val ifscode = matchDetails(OcrText[i],"ifscode",0)
            if(ifscode!=-1 && !isIfsc )
            {
                ifscCode =findIFSCCode(OcrText[i].subSequence(ifscode+7, min(OcrText[i].length,ifs+50)))
                isIfsc = true
                ifscCleanup()
            }
            if(ifs!=-1 && !isIfsc )
            {
                ifscCode =findIFSCCode(OcrText[i].subSequence(ifs+3, min(OcrText[i].length,ifs+50)))
                isIfsc = true
                ifscCleanup()
            }


            val acc = matchDetails(OcrText[i],"no",0)
            if(acc!=-1 && !isAccount)
            {
                if(findAccountNumber(OcrText[i].subSequence(acc, min(OcrText[i].length,acc+50)))=="")

            {
                if(i>=1 && !isAccount )
                {
                     val accountRes = findAccountNumber(OcrText[i-1])
                    if(accountRes!="")
                    {
                        accountNumber = accountRes
                        isAccount  = true
                    }

                }
                if(i+1 < OcrText.size && !isAccount)
                {
                    val accountRes = findAccountNumber(OcrText[i+1])
                    if(accountRes!="")
                    {
                        accountNumber = accountRes
                        isAccount  = true
                    }
                }
            }
                else
            {
                accountNumber =findAccountNumber(OcrText[i].subSequence(acc, min(OcrText[i].length,acc+50)))
                isAccount = true
            }

            }
        }
        var res = ""
       if(isAccount)
       {    chequeData.AccountNo = accountNumber
           res+= "Account No $accountNumber\n"
       }
        if(isIfsc)
        {   chequeData.IFSC = ifscCode
            res+= "IFSC Code $ifscCode\n"

        }
      return chequeData
    }

    private fun  ifscCleanup()
    {   if(ifscCode.length == 9) {
        val temp = ifscCode.substring(0, 4) + '0' + ifscCode.substring(5)
        ifscCode = temp
    }
    }
    private fun findAccountNumber(text: CharSequence) :String
    {
        val pattern = Regex("[0-9]{9,18}")
        val ans : MatchResult? = pattern.find(text)
        if(ans!=null)
        {Log.d("ocrtext account found", ans.value)
        return ans.value}
        return ""
    }
    private fun findIFSCCode(text: CharSequence) :String
    {
        val pattern = Regex("[A-Za-z]{4}[0,O]{1}[a-zA-Z0-9]{6}")
        val ans : MatchResult? = pattern.find(text)
        if(ans!=null)
        {   Log.d("ocrtext ifsc found", ans.value)

            return ans.value}
        return ""
    }
    private fun matchDetails(inputString: String, whatToFind: String, startIndex: Int = 0): Int {
        val matchIndex = inputString.indexOf(whatToFind, startIndex,true)
        if(matchIndex < 0)
            return -1
       return matchIndex
    }
    companion object{
        var ifscCode :String = ""
        var accountNumber:String = ""
        var bankName :String = ""
        var isIfsc:Boolean = false
        var isAccount:Boolean = false
    }


}
