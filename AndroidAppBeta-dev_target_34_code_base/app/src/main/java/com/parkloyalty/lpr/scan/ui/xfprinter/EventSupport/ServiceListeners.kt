package com.twotechnologies.xfexample.EventSupport

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.twotechnologies.n5library.N5Information
import com.twotechnologies.n5library.N5Library
import com.twotechnologies.n5library.N5ReadyListener
import com.twotechnologies.n5library.keyboard.ActionKeyListener
//import com.twotechnologies.util.TTDebug

class ServiceListeners(private val mContext: Context) {
    private var mCallback: ServiceEventCallback? = null
    private val mSrvReady: N5ReadyListener
    private val mSrvNotReady: N5ReadyListener

    //**************************************************************************************************
    //
    //**************************************************************************************************
    fun setCallback(callback: ServiceEventCallback?) {
        mCallback = callback
    }

    //**************************************************************************************************
    // start
    //   listener starting
    //**************************************************************************************************
    fun start() {
        mSrvReady.startListening()
        mSrvNotReady.startListening()
    }

    //**************************************************************************************************
    // stop
    //  turn off listeners and close/unbind library from service
    //**************************************************************************************************
    fun stop() {
        mSrvNotReady.stopListening()
        mSrvReady.stopListening()
        // unbind library
        N5Library.close()
    }

    //**********************************************************************************************
    // N5ReadyHandler
    //**********************************************************************************************
    private inner class N5ReadyHandler
    //************************************************************
    //
    //************************************************************
        (context: Context?, s: String?) : N5ReadyListener(context, s) {
        //************************************************************
        //
        //************************************************************
        override fun onReceive(context: Context, intent: Intent) {
            if (N5Information.isPlatformAvailable()) {
                mCallback!!.listener(ServiceEvents.PlatformConnect, context, intent)
            } else {
                mCallback!!.listener(ServiceEvents.PlatformDisconnect, context, intent)
            }
        }
    }

    private val actionKeyA: ActionKeyListener =
        object : ActionKeyListener(mContext, ACTION_N5_KBD_KEY_A) {
            private val count = 1
            //************************************************************
            /** Handle action key event  */ //************************************************************
            override fun onReceive(context: Context, intent: Intent) {
                val nDeviceNo = intent.getIntExtra("DeviceNumber", 0)
                val msg = "Key A received from device $nDeviceNo"
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }

    //private ActionKeyListener mKeyHandler;
    init {
        // initialize hmiQ API
        N5Library.initialize(mContext)

        // create listeners
        mSrvReady = N5ReadyHandler(mContext, N5ReadyListener.ACTION_N5_READY)
        mSrvNotReady = N5ReadyHandler(mContext, N5ReadyListener.ACTION_N5_NOT_READY)
    }
}
