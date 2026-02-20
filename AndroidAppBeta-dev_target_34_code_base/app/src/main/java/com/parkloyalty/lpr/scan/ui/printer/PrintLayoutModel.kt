package com.parkloyalty.lpr.scan.ui.printer

class PrintLayoutModel constructor(sPrintOrder: Double,sColumn: Int, sLayoutSection: String?, sLabelName: String?,sPrintPosition: String?,sColumnSize: Int?=0) {
    var printOrder: Double = 0.0
    var column: Int = 0
    var layoutOrder: String? = null
    var labelName: String? = null
    var printFont: String? = null
    var printAxisX: String? = null
    var printAxisY: String? = null
    var printColumnSize: Int? = 0

    init {
        printOrder = sPrintOrder
        layoutOrder = sLayoutSection
        labelName = sLabelName
        column = sColumn
        printColumnSize = sColumnSize
        printAxisX = if(sPrintPosition!=null && sPrintPosition!!.contains("#"))sPrintPosition!!.split("#")[0] else "0.0"
        printAxisY = if(sPrintPosition!=null && sPrintPosition!!.contains("#")&& sPrintPosition!!.split("#").size>2)sPrintPosition!!.split("#")[1] else "0.0"
        printFont = if(sPrintPosition!=null && sPrintPosition!!.contains("#")&& sPrintPosition!!.split("#").size>=3)sPrintPosition!!.split("#")[2] else "0"
    }
}