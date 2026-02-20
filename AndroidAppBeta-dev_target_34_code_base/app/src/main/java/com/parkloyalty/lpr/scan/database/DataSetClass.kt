package com.parkloyalty.lpr.scan.database

import android.app.Activity
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse

 class DataSetClass  {


//    public var mApplicationListMake: List<DatasetResponse>? = null
//    private var mApplicationListModel: List<DatasetResponse>? = null
//    private var mApplicationLisColor: List<DatasetResponse>? = null
////
////    private val mApplicationListMake: ArrayList<DatasetResponse?>? = null
//
//    fun getMakeList(): List<DatasetResponse?>? {
//        return mApplicationListMake
//    }
//
//    fun setMakeList(activity: List<DatasetResponse>?) {
//        mApplicationListMake = activity
//    }


    companion object {

//         var mApplicationListMake: List<DatasetResponse>? = null
//         var mApplicationListModle: List<DatasetResponse>? = null
//         var mApplicationListColor: List<DatasetResponse>? = null
//         var mApplicationListStreet: List<DatasetResponse>? = null
//         var mApplicationListBlock: List<DatasetResponse>? = null
//         var mApplicationListState: List<DatasetResponse>? = null
//         var mApplicationListSide: List<DatasetResponse>? = null
//         var mApplicationListMeter: List<DatasetResponse>? = null
//         var mApplicationSpaceList: List<DatasetResponse>? = null
//         var mApplicationLotList: List<DatasetResponse>? = null
//         var mApplicationListListRemark: List<DatasetResponse>? = null
//         var mApplicationTierStemList: List<DatasetResponse>? = null
//         var mApplicationBodyStyleList: List<DatasetResponse>? = null
//         var mApplicationDecalYearList: List<DatasetResponse>? = null
//         var mApplicationListRegulationTimeList: List<DatasetResponse>? = null
//         var mApplicationViolationList: List<DatasetResponse>? = null
//         var mApplicationNoteList: List<DatasetResponse>? = null
//         var mApplicationSettingList: List<DatasetResponse>? = null

        /*//This annotation tells Java classes to treat this method as if it was a static to [KotlinClass]
        @JvmName("getMakeList")
        @JvmStatic
        fun getMakeList(): List<DatasetResponse>? =mApplicationListMake

        //Without it, you would have to use [KotlinClass.Companion.bar()] to use this method.
        @JvmName("setMakeList")
        @JvmStatic
        fun setMakeList(activity: List<DatasetResponse>?) {
            mApplicationListMake = activity
        }

        @JvmName("getModelList")
        @JvmStatic
        fun getModleList(): List<DatasetResponse>? =mApplicationListModle

        @JvmName("setModleList")
        @JvmStatic
        fun setModleList(activity: List<DatasetResponse>?) {
            mApplicationListModle = activity
        }

        @JvmName("getColorList")
        @JvmStatic
        fun getColorList(): List<DatasetResponse>? =mApplicationListColor

        @JvmName("setColorList")
        @JvmStatic
        fun setColorList(activity: List<DatasetResponse>?) {
            mApplicationListColor = activity
        }*/

//        @JvmName("getStreetList")
//        @JvmStatic
//        fun getStreetList(): List<DatasetResponse>? =mApplicationListStreet
//
//        @JvmName("setStreetList")
//        @JvmStatic
//        fun setStreetList(activity: List<DatasetResponse>?) {
//            mApplicationListStreet = activity
//        }

//        @JvmName("getBlockList")
//        @JvmStatic
//        fun getBlockList(): List<DatasetResponse>? = mApplicationListBlock
//
//        @JvmName("setBlockList")
//        @JvmStatic
//        fun setBlockList(activity: List<DatasetResponse>?) {
//            mApplicationListBlock = activity
//        }

//        @JvmName("getStateList")
//        @JvmStatic
//        fun getStateList(): List<DatasetResponse>? = mApplicationListState
//
//        @JvmName("setStateList")
//        @JvmStatic
//        fun setStateList(activity: List<DatasetResponse>?) {
//            mApplicationListState = activity
//        }

//        @JvmName("getsideList")
//        @JvmStatic
//        fun getSideList(): List<DatasetResponse>? = mApplicationListSide
//
//        @JvmName("setSideList")
//        @JvmStatic
//        fun setSideList(activity: List<DatasetResponse>?) {
//            mApplicationListSide = activity
//        }

//        @JvmName("getMeterList")
//        @JvmStatic
//        fun getMeterList(): List<DatasetResponse>? = mApplicationListMeter
//
//        @JvmName("setMeterList")
//        @JvmStatic
//        fun setMeterList(activity: List<DatasetResponse>?) {
//            mApplicationListMeter = activity
//        }

//        @JvmName("getLotList")
//        @JvmStatic
//        fun getLotList(): List<DatasetResponse>? = mApplicationLotList
//
//        @JvmName("setLotList")
//        @JvmStatic
//        fun setLotList(activity: List<DatasetResponse>?) {
//            mApplicationLotList = activity
//        }

//        @JvmName("getRemarkList")
//        @JvmStatic
//        fun getRemarkList(): List<DatasetResponse>? = mApplicationListListRemark
//
//        @JvmName("setRemarkList")
//        @JvmStatic
//        fun setRemarkList(activity: List<DatasetResponse>?) {
//            mApplicationListListRemark = activity
//        }

//        @JvmName("getTierStemList")
//        @JvmStatic
//        fun getTierStemList(): List<DatasetResponse>? = mApplicationTierStemList
//
//        @JvmName("setTierStemList")
//        @JvmStatic
//        fun setTierStemList(activity: List<DatasetResponse>?) {
//            mApplicationTierStemList = activity
//        }

//        @JvmName("regulationTimeList")
//        @JvmStatic
//        fun getRegulationTimeList(): List<DatasetResponse>? = mApplicationListRegulationTimeList
//
//        @JvmName("regulationTimeList")
//        @JvmStatic
//        fun setRegulationTimeList(activity: List<DatasetResponse>?) {
//            mApplicationListRegulationTimeList = activity
//        }

//        @JvmName("decalYearList")
//        @JvmStatic
//        fun getDecalYearList(): List<DatasetResponse>? = mApplicationDecalYearList
//
//        @JvmName("decalYearList")
//        @JvmStatic
//        fun setDecalYearList(activity: List<DatasetResponse>?) {
//            mApplicationDecalYearList = activity
//        }

//        @JvmName("bodyStyleList")
//        @JvmStatic
//        fun getBodyStyleList(): List<DatasetResponse>? = mApplicationBodyStyleList
//
//        @JvmName("bodyStyleList")
//        @JvmStatic
//        fun setBodyStyleList(activity: List<DatasetResponse>?) {
//            mApplicationBodyStyleList = activity
//        }

//        @JvmName("spaceList")
//        @JvmStatic
//        fun getSpaceList(): List<DatasetResponse>? = mApplicationSpaceList
//
//        @JvmName("spaceList")
//        @JvmStatic
//        fun setSpaceList(activity: List<DatasetResponse>?) {
//            mApplicationSpaceList = activity
//        }

//        @JvmName("ViolationList")
//        @JvmStatic
//        fun getViolationList(): List<DatasetResponse>? = mApplicationViolationList
//
//        @JvmName("ViolationList")
//        @JvmStatic
//        fun setViolationList(activity: List<DatasetResponse>?) {
//            mApplicationViolationList = activity
//        }

//        @JvmName("NoteList")
//        @JvmStatic
//        fun getNoteList(): List<DatasetResponse>? = mApplicationNoteList
//
//        @JvmName("NoteList")
//        @JvmStatic
//        fun setNoteList(activity: List<DatasetResponse>?) {
//            mApplicationNoteList = activity
//        }
//
//        @JvmName("SettingList")
//        @JvmStatic
//        fun getSettingList(): List<DatasetResponse>? = mApplicationSettingList
//
//        @JvmName("SettingList")
//        @JvmStatic
//        fun setSettingList(activity: List<DatasetResponse>?) {
//            mApplicationSettingList = activity
//        }
    }
}

