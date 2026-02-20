package com.parkloyalty.lpr.scan.doubangoultimatelpr.utils

import android.os.Handler
import android.os.HandlerThread

class AlprBackgroundTask {

    private var mHandler: Handler? = null
    private var mThread: HandlerThread? = null

    @Synchronized
    fun getHandler(): Handler {
        return mHandler!!
    }

    @Synchronized
    fun isRunning(): Boolean {
        return mHandler != null
    }

    @Synchronized
    fun start(threadName: String?) {
        if (mThread != null) {
            return
        }
        mThread = HandlerThread(threadName)
        mThread!!.start()
        mHandler = Handler(mThread!!.looper)
    }

    @Synchronized
    fun stop() {
        if (mThread == null) {
            return
        }
        mThread!!.quitSafely()
        try {
            mThread!!.join()
            mThread = null
            mHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun post(r: Runnable?) {
        if (mHandler != null) {
            mHandler!!.post(r!!)
        }
    }
}