package com.parkloyalty.lpr.scan.interfaces

import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.Datum

interface LookUpCitationInterfaces {
    fun onCitationData(msg: Datum?)
}