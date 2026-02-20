package com.parkloyalty.lpr.scan.ui.ticket

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.continuousmode.model.ResultDataObject
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LprContinuousResultActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.recycler_result)
    var recyclerViewResult: RecyclerView? = null

    @JvmField
    @BindView(R.id.nestscrollview)
    var nestedScrollView: NestedScrollView? = null

    private var mContext: Context? = null
    private var mWelcomeFormData: WelcomeForm? = null
    private var mDb: AppDatabase? = null
    private val resultDataObjectList: MutableList<ResultDataObject> = ArrayList()
    private val mBackgroudColorArray = intArrayOf(0, 1, 1, 0)
    private val mFileName: String? = null
    private val mSessionID: String? = null
    private var mLoadAllCsvFileFromFolder: File? = null
    private var continousResultAdapter: LprResultAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lpr_continuous_result)
        setFullScreenUI()
        ButterKnife.bind(this)
        init()
    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()


        setToolbar()
        //        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.FILE_NAME + Constants.COTINOUS + "/vehicleList_" + mFileName + ".csv");
        mLoadAllCsvFileFromFolder = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.COTINOUS
        )
        getAllCSVFile(mLoadAllCsvFileFromFolder!!)
    }

    //init toolbar navigation
    private fun setToolbar() {
        initToolbar(
            0,
            this,
            R.id.layHome,
            R.id.layTicketing,
            R.id.layMyActivity,
            R.id.laySetting,
            R.id.layReport,
            R.id.layLogout,
            R.id.drawerLy,
            R.id.imgBack,
            R.id.imgOptions,
            R.id.imgCross,
            R.id.cardTicketing,
            R.id.layIssue,
            R.id.layLookup,
            R.id.layScan,
            R.id.layMunicipalCitation,
            R.id.layGuideEnforcement,
            R.id.laySummary,
            R.id.cardMyActivity,
            R.id.layMap,
            R.id.layContinue,
            R.id.cardGuide,
            R.id.laypaybyplate,
            R.id.laypaybyspace,
            R.id.cardlookup,
            R.id.laycitation,
            R.id.laylpr,
            R.id.layClearcache,
            R.id.laySuperVisorView,
                R.id.layAllReport,
                R.id.layBrokenMeterReport,
                R.id.layCurbReport,
                R.id.layFullTimeReport,
                R.id.layHandHeldMalfunctionReport,
                R.id.laySignReport,
                R.id.layVehicleInspectionReport,
                R.id.lay72HourMarkedVehiclesReport,
                R.id.layBikeInspectionReport,
                R.id.cardAllReport,
                R.id.lay_eow_supervisor_shift_report,
        R.id.layPartTimeReport,
            R.id.layLprHits,
            R.id.laySpecialAssignmentReport,
            R.id.layQRCode,
            R.id.cardQRCode,
            R.id.layGenerateQRCode,
            R.id.layScanQRCode,
            R.id.laySunlight,
            R.id.imgSunlight,
            R.id.lay72hrNoticeToTowReport,
            R.id.layTowReport,
            R.id.laySignOffReport,
            R.id.layNFL,
            R.id.layHardSummer,
            R.id.layAfterSeven,
            R.id.layPayStationReport,
            R.id.laySignageReport,
            R.id.layHomelessReport,
            R.id.laySafetyReport,
            R.id.layTrashReport,
            R.id.layLotCountVioRateReport,
            R.id.layLotInspectionReport,
            R.id.layWordOrderReport,
            R.id.txtlogout,
            R.id.laycameraviolation,
            R.id.layScanSticker,
            R.id.laygenetichit,
            R.id.layDirectedEnforcement,
            R.id.layOwnerBill
        )
    }

    private fun getAllCSVFile(mFile: File) {
        val pattern = ".csv"
        val listFile = mFile.listFiles()
        if (listFile != null) {
            for (i in listFile.indices) {
                if (listFile[i].isDirectory) {
                    getAllCSVFile(listFile[i])
                } else {
                    if (listFile[i].name.endsWith(pattern) && listFile[i].name.startsWith("v")) {
//                        Toast.makeText(getApplicationContext(), listFile[i].getName()+"", Toast.LENGTH_SHORT).show();
                        val file = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            Constants.FILE_NAME + Constants.COTINOUS + "/" + listFile[i].name
                        )
                        readFileData(file)
                    }
                }
            }
        }
    }

    private fun readFileData(file: File) {
        try {
            val mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
            var data: Array<String>
            if (file.exists()) {
                val br = BufferedReader(FileReader(file))
                try {
                    var position = 0
                    var positionColor = -1
                    var csvLine: String
                    while (br.readLine().also { csvLine = it } != null) {
                        data = csvLine.split(",").toTypedArray()
                        try {
                            if (position > 0) {
                                val dataObject = ResultDataObject()
                                try {
                                    dataObject.mLpNumber = data[0].replace("\"", "")
                                    dataObject.mDes = data[1].replace("\"", "")
                                    dataObject.mBackgroundColor =
                                        mBackgroudColorArray[positionColor]
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    if (!data[9].isEmpty() && !data[10].isEmpty()) dataObject.mLat =
                                        data[9].replace("\"", "").toDouble()

                                    dataObject.mLong = data[10].replace("\"", "").toDouble()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    dataObject.mFirstTime = data[7].replace("\"", "")
                                    dataObject.mLastTime = data[13].replace("\"", "")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    val imageName = "anpr_" + dataObject.mLpNumber + ".jpg"
                                    val mImagePath = mPath + Constants.COTINOUS + "/" + imageName
                                    dataObject.mImagePath = mImagePath
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                val resultData: MutableList<ResultDataObject> = ArrayList()
                                resultData.add(dataObject)
                                resultDataObjectList.addAll(resultData)
                            }
                            positionColor++
                            position++
                            if (positionColor % 4 == 0) {
                                positionColor = 0
                            }
                        } catch (e: Exception) {
                            Log.e("Problem", e.toString())
                        }
                    }
                    setAdapterForCitationList(resultDataObjectList)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            } else {
                Toast.makeText(applicationContext, "file not exists", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterForCitationList(listData: List<ResultDataObject>) {
        continousResultAdapter = LprResultAdapter(
            mContext!!,
            listData,
            object : LprResultAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
//                finish();
//                Intent mIntent = new Intent(LprContinuousResultActivity.this, LprDetailsActivity.class);
//                mIntent.putExtra("lpr_number", listData.get(position).getmLpNumber());
//                mIntent.putExtra("screen", "ContinuosResultActivity");
//                startActivity(mIntent);
                }
            })
        recyclerViewResult?.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(mContext, 1)
        gridLayoutManager.scrollToPositionWithOffset(0, 0)
        recyclerViewResult?.adapter = continousResultAdapter
        recyclerViewResult?.layoutManager = gridLayoutManager
        recyclerViewResult?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }
}