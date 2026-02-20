/**
 * Created by BWai on 7/7/2015.
 */
package com.parkloyalty.lpr.scan.ui.printer

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.parkloyalty.lpr.scan.R

//Implements the Bluetooth Device Array List View
class BluetoothDeviceArrayAdapter constructor(
    private val mContext: Context,
    private val values: ArrayList<BluetoothDevice>
) : ArrayAdapter<BluetoothDevice?>(
    mContext, R.layout.row_printer_layout, values as List<BluetoothDevice?>
) {
    public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.row_printer_layout, parent, false)
        val tvDeviceName: TextView = rowView.findViewById<View>(R.id.tvDeviceName) as TextView
        tvDeviceName.setText(values.get(position).getName())
        val tvDeviceAddress: TextView = rowView.findViewById<View>(R.id.tvDeviceAddress) as TextView
        tvDeviceAddress.setText(values.get(position).getAddress())
        return rowView
    }
}