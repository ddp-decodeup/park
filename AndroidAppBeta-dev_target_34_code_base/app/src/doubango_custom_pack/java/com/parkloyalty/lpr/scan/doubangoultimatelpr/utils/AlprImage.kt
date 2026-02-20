package com.parkloyalty.lpr.scan.doubangoultimatelpr.utils

import android.media.Image
import java.util.concurrent.atomic.AtomicInteger

class AlprImage private constructor(image: Image?) {
    var mImage: Image?
    val mRefCount: AtomicInteger
    val image: Image?
        get() {
            assert(mRefCount.toInt() >= 0)
            return mImage
        }

    fun takeRef(): AlprImage? {
        assert(mRefCount.toInt() >= 0)
        if (mRefCount.toInt() < 0) {
            return null
        }
        mRefCount.incrementAndGet()
        return this
    }

    fun releaseRef() {
        assert(mRefCount.toInt() >= 0)
        val refCount = mRefCount.decrementAndGet()
        if (refCount <= 0) {
            mImage!!.close()
            mImage = null
        }
    }

    @Synchronized
    protected fun finalize() {
        if (mImage != null && mRefCount.toInt() < 0) {
            mImage!!.close()
        }
    }

    companion object {
        fun newInstance(image: Image?): AlprImage {
            return AlprImage(image)
        }
    }

    init {
        assert(image != null)
        mImage = image
        mRefCount = AtomicInteger(0)
    }
}