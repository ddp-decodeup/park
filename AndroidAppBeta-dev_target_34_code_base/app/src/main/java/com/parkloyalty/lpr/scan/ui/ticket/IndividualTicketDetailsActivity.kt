package com.parkloyalty.lpr.scan.ui.ticket

import android.os.Bundle
import butterknife.ButterKnife
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity

class IndividualTicketDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_ticket_details)
        ButterKnife.bind(this)
    }
}