package com.parkloyalty.lpr.scan.util

class AutomaticFuzzyClass {
    var Array0 = arrayOf("0", "O", "Q", "D")
    var ArrayO = arrayOf("O", "0", "Q", "D")
    var ArrayQ = arrayOf("Q", "O", "0", "D")
    var ArrayD = arrayOf("D", "O", "0", "Q")
    var Array1 = arrayOf("1", "I")
    var ArrayI = arrayOf("I", "1")
    var ArrayA = arrayOf("A", "4")
    var Array4 = arrayOf("A", "4")
    var Array5 = arrayOf("5", "S")
    var ArrayS = arrayOf("S", "5")
    var ArrayB = arrayOf("B", "8", "3")
    var Array8 = arrayOf("8", "B", "3")
    var Array3 = arrayOf("3", "8", "B")
    var ArrayZ = arrayOf("Z", "2")
    var Array2 = arrayOf("2", "Z")
    var ArrayT = arrayOf("T", "V", "Y")
    var ArrayV = arrayOf("V", "Y", "T")
    var ArrayY = arrayOf("Y", "V", "T")
    var ArrayE = arrayOf("E", "F", "P")
    var ArrayF = arrayOf("F", "E", "P")
    var Arrayp = arrayOf("P", "F", "E")
    var ArrayH = arrayOf("H", "K", "M", "R")
    var Arrayk = arrayOf("K", "R", "H", "M")
    var ArrayR = arrayOf("R", "K", "H", "M")
    var ArrayM = arrayOf("M", "K", "H", "R")
    var Array6 = arrayOf("6", "G")
    var ArrayG = arrayOf("G", "6")
    var mClickPreviousPosition = 0
    var arrayIndex = 0
    var mapList = HashMap<String, Array<String>>()
    private fun setMapValue() {
        mapList["0"] = Array0
        mapList["1"] = Array1
        mapList["2"] = Array2
        mapList["3"] = Array3
        mapList["4"] = Array4
        mapList["5"] = Array5
        //        mapList.put("6", Array4);
//        mapList.put("7", Array7);
        mapList["8"] = Array8
        //        mapList.put("9", Array4);
        mapList["A"] = ArrayA
        mapList["B"] = ArrayB
        //        mapList.put("C", Array4);
        mapList["D"] = ArrayD
        mapList["E"] = ArrayE
        mapList["F"] = ArrayF
        //        mapList.put("G", Array11);
        mapList["H"] = ArrayH
        mapList["I"] = ArrayI
        //        mapList.put("J", Array14);
        mapList["K"] = Arrayk
        //        mapList.put("L", Array16);
        mapList["M"] = ArrayM
        //        mapList.put("N", Array17);
        mapList["O"] = ArrayO
        mapList["P"] = Arrayp
        mapList["Q"] = ArrayQ
        mapList["R"] = ArrayR
        mapList["S"] = ArrayS
        mapList["T"] = ArrayT
        //        mapList.put("U", Array20);
        mapList["V"] = ArrayV
        //        mapList.put("W", Array21);
//        mapList.put("X", Array22);
        mapList["Y"] = ArrayY
        mapList["Z"] = ArrayZ

        mapList["6"] = Array6
        mapList["G"] = ArrayG
    }

    fun resetIndex() {
        mClickPreviousPosition = 0
        arrayIndex = 0
    }

    fun matchStringLogic(mString: String, mClickPosition: Int): String {
        var found = ""
        setMapValue()

//        for (int i = 0; i < mapList.size(); i++) {
        for (key in mapList.keys) {
            if (key == mString) {
                val result = mapList[mString]!!
                LogUtil.printLog("Match String", result[0])
                if (mClickPosition == mClickPreviousPosition) {
                    arrayIndex++
                    if (result.size <= arrayIndex) {
                        arrayIndex = 1
                    }
                } else {
                    arrayIndex = 1
                }
                mClickPreviousPosition = mClickPosition
                found = try {
                    if(result[arrayIndex].equals(mString))
                    {
                        arrayIndex++
                        result[arrayIndex]
                    }else {
                        result[arrayIndex]
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    result[0]
                }
                break
            }
        }
        //        }
        return found
    }
}