package com.example.chequeocr

import android.util.Log
import com.example.ocr.models.ChequeData
import com.google.mlkit.vision.text.Text
import java.lang.Math.min

class ocrProcessor {
    var OcrText: ArrayList<String> = ArrayList(0)

    /**
     * Text returned form the ml kit is stored in the ArrayList in form of string
     * */
    fun process(visionText: Text): ChequeData {
        clean()
        val blocks: List<Text.TextBlock> = visionText.textBlocks
        var temp = ""
        for (i in blocks.indices) {

            var blockstring = ""
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {

                    blockstring += elements[k].text

                }
            }
            temp += blockstring
            temp += "\n"

            OcrText.add(blockstring)
        }
        Log.d("ocr", temp)
        return searchOcrText()

    }

    private fun clean() {
        isIfsc = false
        isAccount = false
    }

    /***
     * This function searches for the keyword "IFSC" and "NO" in the string
     * When we get the keyword in our string we try to extract ifsc number and account no
     * IFSc code will be in the same line as the keyword "IFSC" or in the next line
     * findIFSCCode function is called for both
     *
     * Account NO can be in the same line/above/below
     * findAccountNumber is called first for same line then below and then above if needed
     *
     */
    private fun searchOcrText(): ChequeData {
        val chequeData = ChequeData("", "", "");

        for (i in OcrText.indices) {
            val ifs = matchDetails(OcrText[i], "ifs", 0)
            val ifscode = matchDetails(OcrText[i], "ifscode", 0)
            if (ifscode != -1 && !isIfsc) {
                ifscCode = findIFSCCode(OcrText[i].subSequence(ifscode + 7,
                    min(OcrText[i].length, ifs + 50)))
                isIfsc = true
                ifscCleanup()
            }

            if (ifs != -1 && !isIfsc) {
                ifscCode =
                    findIFSCCode(OcrText[i].subSequence(ifs + 3, min(OcrText[i].length, ifs + 50)))
                isIfsc = true
                ifscCleanup()
            }


            val acc = matchDetails(OcrText[i], "no", 0)
            if (acc != -1 && !isAccount) {
                if (findAccountNumber(OcrText[i].subSequence(acc,
                        min(OcrText[i].length, acc + 50))) == ""
                ) {
                    if (i >= 1 && !isAccount) {
                        val accountRes = findAccountNumber(OcrText[i - 1])
                        if (accountRes != "") {
                            accountNumber = accountRes
                            isAccount = true
                        }

                    }
                    if (i + 1 < OcrText.size && !isAccount) {
                        val accountRes = findAccountNumber(OcrText[i + 1])
                        if (accountRes != "") {
                            accountNumber = accountRes
                            isAccount = true
                        }
                    }
                } else {
                    accountNumber = findAccountNumber(OcrText[i].subSequence(acc,
                        min(OcrText[i].length, acc + 50)))
                    isAccount = true
                }

            }
        }
        var res = ""
        if (isAccount) {
            chequeData.AccountNo = accountNumber
            res += "Account No $accountNumber\n"
        }
        if (isIfsc) {
            chequeData.IFSC = ifscCode
            res += "IFSC Code $ifscCode\n"

        }
        return chequeData
    }

    /**
     * Regex of IFSC code requires that 5th character should be 0(zero).
     * But some time ml kit reads the 0 as O
     * So we clean the same using this function
     * */
    private fun ifscCleanup() {
        if (ifscCode.length == 11) {
            val temp = ifscCode.substring(0, 4) + '0' + ifscCode.substring(5)
            ifscCode = temp
        }
    }

    /**
     * RBI Guidelines state that Account Number should be
     * of minimum length 9 as maximum length as 18
     * We use regex to search for the account no.
     * */
    private fun findAccountNumber(text: CharSequence): String {
        val pattern = Regex("[0-9]{9,18}")
        val ans: MatchResult? = pattern.find(text)
        if (ans != null) {
            Log.d("ocrtext account found", ans.value)
            return ans.value
        }
        return ""
    }

    /**
     * RBI Guidelines state that IFSC Code should be
     * of length 11
     * first 4 should be alphabets
     * 5th character should be 0
     * last 6 characters should be alphanumeric
     * We use regex to search for the IFSC code.
     * Regex of IFSC code requires that 5th character should be 0(zero).
     * But some time ml kit reads the 0 as O
     * So we have allowed the 5th character to be either 0 or O in our regex
     **/
    private fun findIFSCCode(text: CharSequence): String {
        val pattern = Regex("[A-Za-z]{4}[0,O]{1}[a-zA-Z0-9]{6}")
        val ans: MatchResult? = pattern.find(text)
        if (ans != null) {
            Log.d("ocrtext ifsc found", ans.value)

            return ans.value
        }
        return ""
    }

    /**
     * this function searches for a particular string in our input string and
     * returns the first idx where match is found
     * in case of now match -1 is returned
     *
     * **/
    private fun matchDetails(inputString: String, whatToFind: String, startIndex: Int = 0): Int {
        val matchIndex = inputString.indexOf(whatToFind, startIndex, true)
        if (matchIndex < 0)
            return -1
        return matchIndex
    }

    companion object {
        var ifscCode: String = ""
        var accountNumber: String = ""
        var bankName: String = ""
        var isIfsc: Boolean = false
        var isAccount: Boolean = false
    }


}
