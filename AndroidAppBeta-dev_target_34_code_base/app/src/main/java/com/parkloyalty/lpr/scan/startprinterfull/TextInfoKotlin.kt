package com.parkloyalty.lpr.scan.startprinterfull

class TextInfoKotlin {
    private val defaultTextColor = -0xbd7a0c

    private var mText: String? = null
    private var mTextResourceID = 0
    private var mAnimationResourceID = 0
    private var mTextColor = 0
    private var mIsAnimated = false
    private var mIsCustomTextColor = false

    constructor(text: String?, resourceID: Int) {
//        this(text, resourceID, defaultTextColor)
        mText = text
        mTextResourceID = resourceID
        mIsAnimated = false
        mIsCustomTextColor = false
    }

    constructor(text: String?, resourceID: Int, textColor: Int) {
//        this(text, resourceID, 0, textColor)
        mText = text
        mTextResourceID = resourceID
        mTextColor = textColor
        mIsAnimated = false
        mIsCustomTextColor = true
    }

    constructor(text: String?, textResourceId: Int, animationResourceId: Int, textColor: Int) {
        mText = text
        mTextResourceID = textResourceId
        mAnimationResourceID = animationResourceId
        mTextColor = textColor
        mIsAnimated = true
        mIsCustomTextColor = true
    }

    fun getText(): String? {
        return mText
    }

    fun getTextResourceID(): Int {
        return mTextResourceID
    }

    fun getAnimationResourceID(): Int {
        return mAnimationResourceID
    }

    fun getTextColor(): Int {
        return mTextColor
    }

    fun isAnimated(): Boolean {
        return mIsAnimated
    }

    fun isCustomTextColor(): Boolean {
        return mIsCustomTextColor
    }
}