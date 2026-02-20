package com.parkloyalty.lpr.scan.ui.ticket.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.basecontrol.BaseFragment
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SPACE_WITH_COMMA
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.LookUpCitationInterfaces
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.SearchCitationAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.*
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.municipalcitation.MunicipalCitationTicketDetailsActivity
import com.parkloyalty.lpr.scan.ui.ticket.SearchActivity
import com.parkloyalty.lpr.scan.ui.ticket.TicketDetailsActivity
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STREET_LIST
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MOTORIST_ADDRESS
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MOTORIST_DL_NUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MOTORIST_DOB
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MOTORIST_NAME
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.PermissionUtils
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * A simple [Fragment] subclass.
 * Use the [CitationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CitationFragment : BaseFragment(), CustomDialogHelper {
    private var mContext: Context? = null

    @JvmField
    @BindView(R.id.rvHistory)
    var mRecylerViewHistory: RecyclerView? = null

    @JvmField
    @BindView(R.id.searchView)
    var mSearchView: SearchView? = null

    @JvmField
    @BindView(R.id.ivFilter)
    var mImageViewFilter: AppCompatImageView? = null

    @JvmField
    @BindView(R.id.ll_search_1)
    var mLinearLayoutCompatSearch1: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_1)
    var mTextInputSearch1: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_1)
    var mEditTextSearch1: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.ll_search_2)
    var mLinearLayoutCompatSearch2: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_2)
    var mTextInputSearch2: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_2)
    var mEditTextSearch2: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.ll_search_3)
    var mLinearLayoutCompatSearch3: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_3)
    var mTextInputSearch3: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_3)
    var mEditTextSearch3: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.ll_search_4)
    var mLinearLayoutCompatSearch4: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_4)
    var mTextInputSearch4: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_4)
    var mEditTextSearch4: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.ll_search_5)
    var mLinearLayoutCompatSearch5: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.input_search_5)
    var mTextInputSearch5: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_search_5)
    var mEditTextSearch5: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.content_linerlayout)
    var mContentMain: LinearLayoutCompat? = null

    private val TypeString = StringBuilder(1000)
    private var mDb: AppDatabase? = null
    private var mUrl = ""
    private var mFinalURL = ""
    private val mFinalCitation = ""
    private val mFinalBlock = ""
    private val mFinalStreet = ""
    private val mFinalType = ""
    private val mFinalStatus = ""
    private val mFinalLpr = ""
    private var searchViewQuery = ""
    var mSearchStatus = false
    var mScrolledStatus = false
    private var mDate: String? = null
    private var mResList: MutableList<Datum> = ArrayList() //Datum
    private val pendingCitation: MutableList<Datum>? = ArrayList() //Datum
    private val arrPackage: MutableList<Int>? = ArrayList()
    private val filterOption = HashMap<Int, Boolean>(6)
    private var mCitationAdapter: SearchCitationAdapter? = null
    private var loading = true
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var type = 0
    private var indexAPI = 1
    private var mLimit = 0

    private val mGetTicketViewModel: GetTicketViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.content_citation, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
        mDb = BaseApplication.instance?.getAppDatabase()
        setOfflineList()
        setAdapterExternal()
        setZone()
        // below line is to call set on query text listener method.
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                //filter(newText);
                return false
            }
        })
        mSearchView!!.isHorizontalScrollBarEnabled = true
        mSearchView!!.queryHint = "ticket_no"
        mSearchView!!.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        setSearchListener()

        //open dialog
        filterOption[0] = false
        filterOption[1] = false
        filterOption[2] = false
        filterOption[3] = false
        filterOption[4] = false
        filterOption[5] = false


        type = 1
        val isValue = filterOption[type]!!
        filterOption[type] = !isValue

        TypeString.append("block,")
        mLinearLayoutCompatSearch1!!.visibility = View.VISIBLE
        mTextInputSearch1!!.hint = "block"


        type = 2
        val isValue1 = filterOption[type]!!
        filterOption[type] = !isValue1

        TypeString.append("street,")
        mLinearLayoutCompatSearch2!!.visibility = View.VISIBLE
        mTextInputSearch2!!.hint = "street"


        //open dialog
        showSelectFilterDialog(1)
        mEditTextSearch2?.imeOptions = EditorInfo.IME_ACTION_DONE

        mEditTextSearch2?.let { setDropdownStreet("", it) }
        Util.setFieldCaps(activity, mEditTextSearch2!!)
        filterCloseButton()


        setCrossClearButton(mContext = requireContext(), textInputLayout = mTextInputSearch1, appCompatEditText = mEditTextSearch1)
        setCrossClearButton(mContext = requireContext(), textInputLayout = mTextInputSearch2, appCompatAutoCompleteTextView = mEditTextSearch2)

        try {
            if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                ApiLogsClass.writeApiPayloadTex(
                    BaseApplication.instance?.applicationContext!!,
                    "---------look UP citation history --------"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAdapterExternal(){
        mCitationAdapter = SearchCitationAdapter(mContext!!, mResList, object : SearchCitationAdapter.ListItemSelectListener{
            override fun onItemClick(mDataTicket: Datum?) {
                if (PermissionUtils.requestCameraAndStoragePermission(activity!!)) {
                    val databaseModel = CitationInsurranceDatabaseModel()
                    val voilationModel = CitationVoilationModel()
                    val vehicleModel = CitationVehicleModel()
                    val officerModel = CitationOfficerModel()
                    val locationModel = CitationLocationModel()
                    val issuranceModel = CitationIssuranceModel()

                    val mIntent : Intent
                    if (mDataTicket?.category == API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET){
                         mIntent = Intent(mContext, MunicipalCitationTicketDetailsActivity::class.java)

                        val motoristName = AppUtils.getFullName(
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristFirstName.nullSafety(),
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristMiddleName.nullSafety(),
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristLastName.nullSafety()
                        )

                        val motoristAddress = AppUtils.getFullAddress(
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressBlock.nullSafety(),
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressStreet.nullSafety(),
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressCity.nullSafety(),
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressState.nullSafety(),
                            mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressZip.nullSafety()
                        )

                        mIntent.putExtra(INTENT_KEY_MOTORIST_NAME, motoristName)
                        mIntent.putExtra(INTENT_KEY_MOTORIST_DOB, mDataTicket.municipalCitationMotoristDetailsModel?.motoristDateOfBirth.nullSafety())
                        mIntent.putExtra(INTENT_KEY_MOTORIST_DL_NUMBER, mDataTicket.municipalCitationMotoristDetailsModel?.motoristDlNumber.nullSafety())
                        mIntent.putExtra(INTENT_KEY_MOTORIST_ADDRESS, motoristAddress)

                    }else{
                         mIntent = Intent(mContext, TicketDetailsActivity::class.java)
                    }

                    mIntent.putExtra("ticket_number", mDataTicket?.ticketNo.toString())
//                        mIntent.putExtra(
//                            "ticket_date",
//                            splitDate(mDataTicket?.citationIssueTimestamp.nullSafety()).toString()
//                        )
                    mIntent.putExtra(
                        "ticket_date",
                        splitDate(mDataTicket?.headerDetails?.timestamp.nullSafety()).toString()
                    )
                    var code = ""
                    var desr = ""
                    var fine = ""
                    var lateFine = ""
                    var due15 = ""
                    var due30 = ""
                    var due45 = ""
                    var lot: String? = ""
                    var street = ""
                    var block = ""
                    var direction = ""
                    var side = ""
                    var meter = ""
                    var spaceId = ""
                    var vinNumber = ""
                    if (!TextUtils.isEmpty(mDataTicket?.violationDetails?.code)) {
                        code = mDataTicket?.violationDetails?.code.nullSafety()
                        voilationModel.violationCode = code
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.violationDetails?.description)) {
                        desr = SPACE_WITH_COMMA + "" + mDataTicket?.violationDetails?.description
                        voilationModel.locationDescr = mDataTicket?.violationDetails?.description
                    }
                    try {
                        if (mDataTicket?.violationDetails?.fine != 0.0) {
                            fine = SPACE_WITH_COMMA + "Fine:" + mDataTicket?.violationDetails?.fine
                            voilationModel.amount =
                                mDataTicket?.violationDetails?.fine.toString() + ""
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (mDataTicket?.violationDetails?.late_fine != 0.0) {
                            lateFine =
                                SPACE_WITH_COMMA + "Late fine:" + mDataTicket?.violationDetails?.late_fine
                            voilationModel?.amountDueDate =
                                mDataTicket?.violationDetails?.late_fine.toString() + ""
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (mDataTicket?.violationDetails?.due_15_days != 0.0) {
                            due15 =
                                SPACE_WITH_COMMA + "Due 15 days:" + mDataTicket?.violationDetails?.due_15_days
                            voilationModel.dueDate =
                                mDataTicket?.violationDetails?.due_15_days.toString() + ""
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (mDataTicket?.violationDetails?.due_30_days != 0.0) {
                            due30 =
                                SPACE_WITH_COMMA + "Due 30 days:" + mDataTicket?.violationDetails?.due_30_days
                            voilationModel.dueDate30 =
                                mDataTicket?.violationDetails?.due_30_days.toString() + ""
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (mDataTicket?.violationDetails?.due_45_days != 0.0) {
                            due45 =
                                SPACE_WITH_COMMA + "Due 45 days:" + mDataTicket?.violationDetails?.due_45_days
                            voilationModel.dueDate45 =
                                mDataTicket?.violationDetails?.due_45_days.toString() + ""
                        }
                    } catch (e: Exception) {
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.location?.block)) {
                        block = mDataTicket?.location?.block.nullSafety()
                        locationModel.block = mDataTicket?.location?.block
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.location?.lot)) {
                        lot = SPACE_WITH_COMMA + "" + mDataTicket?.location?.lot
                        locationModel.lot = lot
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.location?.street)) {
                        street = SPACE_WITH_COMMA + "" + mDataTicket?.location?.street
                        locationModel.street = mDataTicket?.location?.street
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.location?.direction)) {
                        direction =
                            SPACE_WITH_COMMA + "Direction:" + mDataTicket?.location?.direction
                        locationModel.direction = mDataTicket?.location?.direction
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.location?.side)) {
                        side = SPACE_WITH_COMMA + "Side:" + mDataTicket?.location?.side
                        locationModel.side = mDataTicket?.location?.side
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.location?.meter)) {
                        meter = SPACE_WITH_COMMA + "Meter: " + mDataTicket?.location?.meter
                        locationModel.meterName = mDataTicket?.location?.meter
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.location?.spaceId)) {
                        spaceId = "" + mDataTicket?.location?.spaceId
                        locationModel.spaceName = mDataTicket?.location?.spaceId
                    }
                    if (!TextUtils.isEmpty(mDataTicket?.vehicleDetails?.vin_number)) {
                        vinNumber = "" + mDataTicket?.vehicleDetails?.vin_number
                        vehicleModel.vinNumber = mDataTicket?.vehicleDetails?.vin_number
                    }
                    if (mDataTicket?.commentDetails != null && mDataTicket?.commentDetails?.remark_1 != null && !TextUtils.isEmpty(
                            mDataTicket?.commentDetails?.remark_1
                        )
                    ) {
                        issuranceModel.locationRemarks = mDataTicket?.commentDetails?.remark_1
                    }
                    if (mDataTicket?.commentDetails != null && mDataTicket?.commentDetails?.remark_2 != null && !TextUtils.isEmpty(
                            mDataTicket?.commentDetails?.remark_2
                        )
                    ) {
                        issuranceModel.locationRemarks1 = mDataTicket?.commentDetails?.remark_2
                    }
                    if (mDataTicket?.commentDetails != null && mDataTicket?.commentDetails?.remark_2 != null && !TextUtils.isEmpty(
                            mDataTicket?.commentDetails?.remark_2
                        )
                    ) {
                        issuranceModel.locationRemarks1 = mDataTicket?.commentDetails?.remark_2
                    }
                    if (mDataTicket?.commentDetails != null && mDataTicket?.commentDetails?.note_1 != null && !TextUtils.isEmpty(
                            mDataTicket?.commentDetails?.note_1
                        )
                    ) {
                        issuranceModel.locationNotes = mDataTicket?.commentDetails?.note_1
                    }
                    if (mDataTicket?.commentDetails != null && mDataTicket?.commentDetails?.note_2 != null && !TextUtils.isEmpty(
                            mDataTicket?.commentDetails?.note_2
                        )
                    ) {
                        issuranceModel.locationNotes1 = mDataTicket?.commentDetails?.note_2
                    }
                    if (mDataTicket?.timeLimitEnforcementObservedTime != null && mDataTicket?.timeLimitEnforcementObservedTime != null && !TextUtils.isEmpty(
                            mDataTicket?.timeLimitEnforcementObservedTime!!
                        )
                    ) {
                        sharedPreference.write(
                            SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME,
                            (mDataTicket?.timeLimitEnforcementObservedTime!!)
                        )
                    }

                    var print_bitmap: String? = ""
                    if (mDataTicket != null && mDataTicket.images != null && mDataTicket?.images?.size!! > 0) {
                        mIntent.putExtra("image_size", mDataTicket?.images?.size)
                        for (i in mDataTicket?.images?.indices!!) {
//                        if (mDataTicket?.images!![i].contains(FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                            if (mDataTicket?.images!![i].contains(FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                                print_bitmap = mDataTicket?.images!![i]
                                break
                            }
                        }
                    }
                    mIntent.putExtra("print_bitmap", print_bitmap)
                    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
                        || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_BURBANK,
                            ignoreCase = true
                        )
                    ) {
                        mIntent.putExtra("address_", block + lot + street + direction + meter);
                    } else {
                        mIntent.putExtra("address_", block + street)
                    }
                    mIntent.putExtra("space_", spaceId)
                    mIntent.putExtra("vinNumber_", vinNumber)
                    mIntent.putExtra("fineAmount_", "" + mDataTicket?.violationDetails?.fine)
                    mIntent.putExtra("expire_date", mDataTicket?.vehicleDetails?.mLicenseExpiry)
                    mIntent.putExtra("bodystyle_", mDataTicket?.vehicleDetails?.body_style)
                    mIntent.putExtra("tvr", mDataTicket?.isTvr)
                    mIntent.putExtra("driveoff", mDataTicket?.isDriveOff)

                    var printQuery: String
                    var printHeight = 1000 // Default value if parsing fails

                    try {
                        val responseJson = JSONObject(mDataTicket?.print_query ?: "")
                        printQuery = responseJson.getString("print_query")
                        printHeight = responseJson.getInt("print_height")
                        sharedPreference.writeInt(
                            SharedPrefKey.LAST_PRINTOUT_HEIGHT,
                            if (printHeight > 100) printHeight else 1000
                        )

                    } catch (e: Exception) {
                        printQuery = mDataTicket?.print_query ?: ""
                        // printHeight stays as default
                    }

                    mIntent.putExtra("printQuery", printQuery)

                    mIntent.putExtra(
                        "voilation_details",
                        code + desr + fine + lateFine + due15 + due30 + due45
                    )
                    mIntent.putExtra("voilation_description", desr)
                    mIntent.putExtra("booklet_id", mDataTicket?.ticketNo.toString())
                    //                        mIntent.putExtra("ticket_id", String.valueOf(mDataTicket.getId()));
                    mIntent.putExtra(
                        "make", if (mDataTicket?.vehicleDetails?.make != null &&
                            !TextUtils.isEmpty(mDataTicket?.vehicleDetails?.make)
                        ) mDataTicket?.vehicleDetails?.make else ""
                    )
                    mIntent.putExtra(
                        "model", if (mDataTicket?.vehicleDetails?.model != null &&
                            !TextUtils.isEmpty(mDataTicket?.vehicleDetails?.model)
                        ) mDataTicket?.vehicleDetails?.model else ""
                    )
                    mIntent.putExtra(
                        "color", if (mDataTicket?.vehicleDetails?.color != null &&
                            !TextUtils.isEmpty(mDataTicket?.vehicleDetails?.color)
                        ) mDataTicket?.vehicleDetails?.color else ""
                    )
                    mIntent.putExtra(
                        "state", if (mDataTicket?.vehicleDetails?.state != null &&
                            !TextUtils.isEmpty(mDataTicket?.vehicleDetails?.state)
                        ) mDataTicket?.vehicleDetails?.state else ""
                    )
                    mIntent.putExtra(
                        "lpr_number", if (mDataTicket?.vehicleDetails?.lprNo != null &&
                            !TextUtils.isEmpty(mDataTicket?.vehicleDetails?.lprNo)
                        ) mDataTicket?.vehicleDetails?.lprNo else ""
                    )
                    mIntent.putExtra("ticket_status", mDataTicket?.status)
                    mIntent.putExtra("from_scr", "SearchScreen")
                    mIntent.putExtra("ticket_id", mDataTicket?.id.toString())
                    sharedPreference.write(SharedPrefKey.isReissueTicket, "false")
                    if (mDataTicket?.vehicleDetails != null) {
                        vehicleModel.make =
                            if (mDataTicket?.vehicleDetails?.make != null) mDataTicket?.vehicleDetails?.make else ""
                        vehicleModel.model =
                            if (mDataTicket?.vehicleDetails?.model != null) mDataTicket.vehicleDetails?.model else ""
                        vehicleModel.color =
                            if (mDataTicket?.vehicleDetails?.color != null) mDataTicket.vehicleDetails?.color else ""
                        vehicleModel.state =
                            if (mDataTicket?.vehicleDetails?.state != null) mDataTicket.vehicleDetails?.state else ""
                        vehicleModel.licensePlate =
                            if (mDataTicket?.vehicleDetails?.lprNo != null) mDataTicket.vehicleDetails?.lprNo else ""
                        vehicleModel.bodyStyle =
                            if (mDataTicket?.vehicleDetails?.body_style != null) mDataTicket.vehicleDetails?.body_style else ""
                        vehicleModel.decalNumber =
                            if (mDataTicket?.vehicleDetails?.decal_number != null) mDataTicket.vehicleDetails?.decal_number else ""
                        vehicleModel.decalYear =
                            if (mDataTicket?.vehicleDetails?.decal_year != null) mDataTicket.vehicleDetails?.decal_year else ""
                        vehicleModel.vinNumber =
                            if (mDataTicket?.vehicleDetails?.vin_number != null) mDataTicket.vehicleDetails?.vin_number else ""
                    }
                    if (mDataTicket?.officerDetails != null) {
                        officerModel.shift =
                            if (mDataTicket?.officerDetails?.mShift != null) mDataTicket.officerDetails?.mShift else ""
                        officerModel.agency =
                            if (mDataTicket.officerDetails?.agency != null) mDataTicket.officerDetails?.agency else ""
                        officerModel.badgeId =
                            if (mDataTicket.officerDetails?.badgeId != null) mDataTicket.officerDetails?.badgeId else ""
                        officerModel.beat =
                            if (mDataTicket.officerDetails?.beat != null) mDataTicket.officerDetails?.beat else ""
                        officerModel.squad =
                            if (mDataTicket.officerDetails?.squad != null) mDataTicket.officerDetails?.squad else ""
                        officerModel.zone =
                            if (mDataTicket.officerDetails?.zone != null) mDataTicket.officerDetails?.zone else ""
                        officerModel.officerId =
                            if (mDataTicket.officerDetails?.officer_name != null) mDataTicket.officerDetails?.officer_name else ""
                    }
                    issuranceModel.officer = officerModel
                    issuranceModel.vehicle = vehicleModel
                    issuranceModel.voilation = voilationModel
                    issuranceModel.location = locationModel
                    if (mDataTicket?.category == API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET){
                        if (mDataTicket.municipalCitationMotoristDetailsModel != null) {
                            val municipalCitationMotoristDetailsModel = MunicipalCitationMotoristDetailsModel()

                            municipalCitationMotoristDetailsModel.motoristFirstName = mDataTicket.municipalCitationMotoristDetailsModel?.motoristFirstName.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristMiddleName = mDataTicket.municipalCitationMotoristDetailsModel?.motoristMiddleName.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristLastName = mDataTicket.municipalCitationMotoristDetailsModel?.motoristLastName.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristDateOfBirth = mDataTicket.municipalCitationMotoristDetailsModel?.motoristDateOfBirth.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristDlNumber = mDataTicket.municipalCitationMotoristDetailsModel?.motoristDlNumber.nullSafety()

                            municipalCitationMotoristDetailsModel.motoristAddressBlock = mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressBlock.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristAddressStreet = mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressStreet.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristAddressCity = mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressCity.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristAddressState = mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressState.nullSafety()
                            municipalCitationMotoristDetailsModel.motoristAddressZip = mDataTicket.municipalCitationMotoristDetailsModel?.motoristAddressZip.nullSafety()

                            issuranceModel.municipalCitationMotoristDetailsModel = municipalCitationMotoristDetailsModel
                        }
                    }

                    databaseModel.citationData = issuranceModel
                    sharedPreference.write(SharedPrefKey.CITATION_DATAL, databaseModel)
                    startActivity(mIntent)

            }else{
                LogUtil.printSnackBar(mContentMain!!,activity!!,"Storage permission is required for printing")
            }
                }
        })
        val mLayoutManager: LinearLayoutManager
        mRecylerViewHistory!!.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        mRecylerViewHistory!!.layoutManager = mLayoutManager
        mRecylerViewHistory!!.adapter = mCitationAdapter

        mRecylerViewHistory!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    mScrolledStatus = true
                } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                    //scrolled to TOP
                    //showProgressBar();
                    //callBookFilterApi(mPre);
                }
                if (dy > 0) { //check for scroll down
                    visibleItemCount = mLayoutManager.childCount
                    totalItemCount = mLayoutManager.itemCount
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            if (mResList.size < totalRecordCount && mResList.size > 9 && mLimit > 9) {
                                callAddTimingApi(if (!mFinalURL.isEmpty()) mFinalURL else mUrl)
                            }
                            // Do pagination.. i.e. fetch new data
                            loading = false
                        }
                    }
                }
            }
        })
    }

    private fun setZone() {
        try {
            mUrl = "";
            //Returns current time in millis
            var mZone = "CST"
            if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb) != null) {
                mZone = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)!![0].mValue.nullSafety()
            }
            val mWelcomeForm = mDb?.dbDAO?.getWelcomeForm()
            val id = if (mWelcomeForm!!.siteOfficerId != null) mWelcomeForm.siteOfficerId else ""
            if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13()
                )||
                BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)) {
                mUrl = "issue_ts_from=" + AppUtils.getStartTDateForPhili(mZone) + "&issue_ts_to=" +
                        AppUtils.getEndTDate(mZone) + "&site_officer_id=" +
                        id + "&limit=25" + "&shift=" + sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            }else {
                mUrl = "issue_ts_from=" + AppUtils.getStartTDate(mZone) + "&issue_ts_to=" +
                        AppUtils.getEndTDate(mZone) + "&site_officer_id=" +
                        id + "&limit=25" + "&shift=" + sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        callAddTimingApi(mUrl)
    }

    private val getTicketResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_TICKET
        )
    }

    private fun addObservers() {
        mGetTicketViewModel!!.response.observe(viewLifecycleOwner, getTicketResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mGetTicketViewModel!!.response.removeObserver(getTicketResponseObserver)
    }

    private fun setAdapterForCitation() {
        if (mResList.size > 0) {
            try {
                if (mScrolledStatus) {
                    if (mResList.size > 0) {
                        //use to focus the item with index of 2nd last row
                        mRecylerViewHistory!!.scrollToPosition(mResList.size)
                    } else {
                        //mRecylerViewHistory.scrollToPosition(mResList.size() - 1);
                    }
                    //mMusicAdapter!!.notifyDataSetChanged()
                }
                mRecylerViewHistory!!.visibility = View.VISIBLE
                mCitationAdapter?.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mRecylerViewHistory!!.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    //perform click actions
    @OnClick(R.id.ivFilter)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.ivFilter -> {
                TypeString.setLength(0)
                showSelectFilterDialog(0)
            }
        }
    }

    /* Call Api to Add timing */
    private fun callAddTimingApi(url: String) {
        var url = url
        if (isInternetAvailable(mContext!!)) {
            //Returns current time in millis
            url = "$url&page=$indexAPI"
            //            String mZone = "CST";
//            DatasetDatabaseModel model = mDb.getDbDAO().getDataset();
//            if (model != null && model.getDataset().getSettingsList()!=null) {
//                mZone = model.getDataset().getSettingsList().get(0).getMetadata().getTimezoneName();
//            }
            mGetTicketViewModel!!.hitGetTicketApi(url)
        } else {
            if (pendingCitation != null && pendingCitation.size > 0) {
                mResList.addAll(pendingCitation)
            }
            setAdapterForCitation()
            LogUtil.printToastMSGForErrorWarning(
                mContext?.applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
            pendingCitation!!.clear()
        }
    }
    var totalRecordCount =0
    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> {
//              showProgressLoader(getString(R.string.scr_message_please_wait))
            }
            Status.SUCCESS ->                 //dismissLoader();
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.GET_TICKET, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), GetTicketData::class.java)

                            if (responseModel != null && responseModel.success.nullSafety()) {
                                //LogUtil.printToastMSG(SearchActivity.this, String.valueOf(responseModel.getData().size()));
                                if (responseModel.data != null) {
                                    ioScope.launch {
                                        updatePendingCitationStatus(responseModel.data!!)
                                        if (pendingCitation != null && pendingCitation.size > 0) {
                                            mResList.addAll(pendingCitation)
                                        }
                                        totalRecordCount = responseModel.mLength
                                        mResList.addAll(responseModel.data!!)
                                        mainScope.launch {
                                            setAdapterForCitation()
                                            loading = true
                                            pendingCitation!!.clear()
                                            mLimit = responseModel.mLength.nullSafety()
                                            if (responseModel.data!!.size > 9) {
                                                indexAPI++
                                            }
                                        }
                                    }
                                }
                            } else if (responseModel != null && !responseModel.success!!) {
                                // Not getting response from server..!!
                                val message: String
                                if (responseModel.response != null && responseModel.response != "") {
                                    message = responseModel.response!!
                                    AppUtils.showCustomAlertDialog(mContext, "GET_TICKET",
                                        message, "Ok", "Cancel", this
                                    )
                                } else {
                                    responseModel.response = "Not getting response from server..!!"
                                    message = responseModel.response.nullSafety()
                                    AppUtils.showCustomAlertDialog(mContext, "GET_TICKET",
                                        message, "Ok", "Cancel", this
                                    )
                                }
                                // AppUtils.showCustomAlertDialog(mContext, "GET_TICKET",
                                //      "Not getting response from server..!!", "Ok", "Cancel", this);
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext, "GET_TICKET",
                                    "Something wen't wrong..!!", "Ok", "Cancel",
                                    this
                                )
                                dismissLoader()

                                // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
                            }
                        }
                    } catch (e: Exception) {
                        LogUtil.printLog("Data_new", "")
                        e.printStackTrace()
                        //token expires
                        //dismissLoader();
                        //LogUtil.printToastMSG(mContext,e.getMessage());
                        //((BaseActivity)mContext).logout(mContext);
                    }
                }
            Status.ERROR -> {
                //dismissLoader();
                AppUtils.showCustomAlertDialogSessionTimeOut(mContext, "ERROR",
                    apiResponse.error!!.message, "Ok", "Cancel",this)
                LogUtil.printToastMSG(mContext?.applicationContext, getString(R.string.err_msg_connection_was_refused))
            }

            else -> {}
        }
    }

    private fun splitsFormat(Date: String?): String {
        val separated = Date!!.split("-".toRegex()).toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        val calendar = Calendar.getInstance()
        calendar[Calendar.MONTH] = MM.toInt()
        val simpleDateFormat = SimpleDateFormat("MMM")
        simpleDateFormat.calendar = calendar
        val monthName = simpleDateFormat.format(calendar.time)
        return MM + "-" + DD + "-" + YYYY.substring(2, 4)
    }

    private fun getHours(hours: String): String? {
        val separated = hours.split(":".toRegex()).toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvert(`val`)
    }

    fun splitDate(date: String): String? {
        return try {
            val separated = date.split("T".toRegex()).toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHours(`val`)
        } catch (e: Exception) {
            date
        }
    }

    private fun dateConvert(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(`val`)
            return splitsFormat(mDate) + " " + _12HourSDF.format(_24HourDt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //set listener for search view
    private fun setSearchListener() {
        // below line is to call set on query text listener method.
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                createURLBeforeCallingAPI(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchViewQuery = newText
                if(newText.isEmpty())
                {
                    mResList.clear()
                    indexAPI = 1
                    setZone()
                }
                return false
            }
        })
    }

    private fun createURLBeforeCallingAPI(query: String) {
        indexAPI = 1
        var uri: URI? = null
        var mtypeUrl = ""
        val mtypeLANG = ""
        var mtypeCit = ""
        var mtypeBlo = ""
        var mtypeStr = ""
        var mtypeTick = ""
        var mtypeSta = ""
        var mtypeLpr = ""
        try {
            //%20 for spaces
            uri = URI(query.replace(" ".toRegex(), "%20"))
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        if (TextUtils.isEmpty(TypeString) && arrPackage!!.size < 1) {
            TypeString.append("citation,")
            arrPackage.add(0)
        }
        if (arrPackage != null) {
            for (data in arrPackage) {
                type = data
                if (!query.isEmpty()) {
                    mtypeCit = "&ticket_no=$query"
                }
                if (type == 1 && !TextUtils.isEmpty(mEditTextSearch1!!.text)) {
                    mtypeBlo = "&block=" + mEditTextSearch1!!.text.toString()
                }
                if (type == 2 && !TextUtils.isEmpty(mEditTextSearch2!!.text)) {
                    mtypeStr = "&street=" + mEditTextSearch2!!.text.toString()
                }
                if (type == 3 && !TextUtils.isEmpty(mEditTextSearch3!!.text)) {
                    mtypeTick = "&side=" + mEditTextSearch3!!.text.toString()
                }
                if (type == 4 && !TextUtils.isEmpty(mEditTextSearch4!!.text)) {
                    mtypeSta = "&status=" + mEditTextSearch4!!.text.toString()
                }
                if (type == 5 && !TextUtils.isEmpty(mEditTextSearch5!!.text)) {
                    mtypeLpr = "&lp_number=" + mEditTextSearch5!!.text.toString()
                }
            }
        }
        mtypeUrl = mUrl + mtypeCit + mtypeBlo + mtypeStr + mtypeTick + mtypeSta + mtypeLpr
        mFinalURL = mtypeUrl
        totalRecordCount = 0
        callAddTimingApi(mFinalURL)
        mtypeUrl = ""
        //mUrl = "";
        mSearchStatus = true
        //mResList = ArrayList()
        mResList.clear()
        mSearchView!!.clearFocus()
        mRecylerViewHistory!!.visibility = View.GONE
    }

    private fun setSearchHint(type: Int): String {
        var value = ""
        if (type == 1) {
            TypeString.append("block,")
            mLinearLayoutCompatSearch1!!.visibility = View.VISIBLE
            mTextInputSearch1!!.hint = "block"
            value = "block"
        }
        if (type == 2) {
            TypeString.append("street,")
            mLinearLayoutCompatSearch2!!.visibility = View.VISIBLE
            mTextInputSearch2!!.hint = "street"
            value = "street"
        }
        if (type == 3) {
            TypeString.append("side,")
            mLinearLayoutCompatSearch3!!.visibility = View.VISIBLE
            mTextInputSearch3!!.hint = "side of street"
            value = "type"
        }
        if (type == 4) {
            TypeString.append("status,")
            mLinearLayoutCompatSearch4!!.visibility = View.VISIBLE
            mTextInputSearch4!!.hint = "status"
            value = "status"
        }
        if (type == 5) {
            TypeString.append("lpr number,")
            mLinearLayoutCompatSearch5!!.visibility = View.VISIBLE
            mTextInputSearch5!!.hint = "lpr number"
            value = "lpr number"
        }
        return value
    }

    private fun setFilterSelection(
        typeValue: Int,
        cardView: CardView,
        mEditText: TextInputEditText
    ) {
        val isClicked = filterOption[type]!!
        if (isClicked) {
            cardView.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
            mEditText.setHintTextColor(resources.getColor(R.color.white))
        } else {
            cardView.setCardBackgroundColor(resources.getColor(R.color.white))
            mEditText.setHintTextColor(resources.getColor(R.color.gray))
        }
        editTextViewActionButtonClick()
        mSearchView!!.clearFocus()
    }

    private fun setFilterSelection(
            typeValue: Int,
            cardView: CardView,
            mEditText: AppCompatAutoCompleteTextView
    ) {
        val isClicked = filterOption[type]!!
        if (isClicked) {
            cardView.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
            mEditText.setHintTextColor(resources.getColor(R.color.white))
        } else {
            cardView.setCardBackgroundColor(resources.getColor(R.color.white))
            mEditText.setHintTextColor(resources.getColor(R.color.gray))
        }
        editTextViewActionButtonClick()
        mSearchView!!.clearFocus()
    }

    private fun showSelectFilterDialog(mSearchStatus: Int) {
        val mDialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
        mDialog.setContentView(R.layout.dialog_search_filter)
        val mEditTextCitation: TextInputEditText = mDialog.findViewById(R.id.editTextCitation)
        val mEditTextBlock: TextInputEditText = mDialog.findViewById(R.id.editTextBlock)
        val mEditTextStreet: TextInputEditText = mDialog.findViewById(R.id.editTextStreet)
        val mEditTextRecordType: TextInputEditText = mDialog.findViewById(R.id.editTextRecordType)
        val mEditTextStatus: TextInputEditText = mDialog.findViewById(R.id.editTextStatus)
        val mEditTextNumber: TextInputEditText = mDialog.findViewById(R.id.editTextNumber)
        val appCompatButton: AppCompatButton = mDialog.findViewById(R.id.btn_done)
        val cardCitation: CardView = mDialog.findViewById(R.id.cardCitation)
        val cardBlock: CardView = mDialog.findViewById(R.id.cardBlock)
        val cardStreet: CardView = mDialog.findViewById(R.id.cardStreet)
        val cardRecType: CardView = mDialog.findViewById(R.id.cardRecType)
        val cardStatus: CardView = mDialog.findViewById(R.id.cardStatus)
        val cardNum: CardView = mDialog.findViewById(R.id.cardNum)
        appCompatButton.visibility = View.VISIBLE

        //default block and street visible and set selected in popup
        val isClickedBlock = filterOption[0]!!
        if(isClickedBlock)
        setFilterSelection(type, cardBlock, mEditTextBlock)

        val isClickedStreet = filterOption[1]!!
        if(isClickedStreet)
        setFilterSelection(type, cardStreet, mEditTextStreet)

        appCompatButton.setOnClickListener {
            TypeString.setLength(0)
            TypeString.append("citation,")
            arrPackage!!.clear()
            filterCloseButton()
            if (arrPackage != null) {
                hideFilterLayout()
                for (comapare in arrPackage) {
                    setSearchHint(comapare)
                }
                editTextViewActionButtonClick()
            }
            mDialog.dismiss()
        }
        mEditTextCitation.setOnClickListener {
            type = 0
            val isValue = filterOption[type]!!
            filterOption[type] = !isValue
            setFilterSelection(type, cardCitation, mEditTextCitation)
            // mDialog.dismiss();
        }
        mEditTextBlock.setOnClickListener {
            type = 1
            val isValue = filterOption[type]!!
            filterOption[type] = !isValue
            setFilterSelection(type, cardBlock, mEditTextBlock)
            //mDialog.dismiss();
        }
        mEditTextStreet.setOnClickListener {
            type = 2
            val isValue = filterOption[type]!!
            filterOption[type] = !isValue
            setFilterSelection(type, cardStreet, mEditTextStreet)
            //mDialog.dismiss();
        }
        mEditTextRecordType.setOnClickListener {
            type = 3
            val isValue = filterOption[type]!!
            filterOption[type] = !isValue
            setFilterSelection(type, cardRecType, mEditTextRecordType)
            // mDialog.dismiss();
        }
        mEditTextStatus.setOnClickListener {
            type = 4
            val isValue = filterOption[type]!!
            filterOption[type] = !isValue
            setFilterSelection(type, cardStatus, mEditTextStatus)
            //mDialog.dismiss();
        }
        mEditTextNumber.setOnClickListener {
            try {
                type = 5
                val isValue = filterOption[type]!!
                filterOption[type] = !isValue
                setFilterSelection(type, cardNum, mEditTextNumber)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //mDialog.dismiss();
        }
        if (arrPackage != null && arrPackage.size > 0) {
            hideFilterLayout()
            TypeString.append("citation,")
            for (data in arrPackage) {
                setSearchHint(data)
                if (data == 0) { // true if you want to submit, otherwise false
                    mSearchView!!.clearFocus()
                    cardCitation.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
                    mEditTextCitation.setHintTextColor(resources.getColor(R.color.white))
                } else if (data == 1) {
                    mSearchView!!.clearFocus()
                    cardBlock.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
                    mEditTextBlock.setHintTextColor(resources.getColor(R.color.white))
                } else if (data == 2) {
                    mSearchView!!.clearFocus()
                    cardStreet.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
                    mEditTextStreet.setHintTextColor(resources.getColor(R.color.white))
                } else if (data == 3) {
                    mSearchView!!.clearFocus()
                    cardRecType.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
                    mEditTextRecordType.setHintTextColor(resources.getColor(R.color.white))
                } else if (data == 4) {
                    mSearchView!!.clearFocus()
                    cardStatus.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
                    mEditTextStatus.setHintTextColor(resources.getColor(R.color.white))
                } else if (data == 5) {
                    mSearchView!!.clearFocus()
                    cardNum.setCardBackgroundColor(resources.getColor(R.color.deep_yellow))
                    mEditTextNumber.setHintTextColor(resources.getColor(R.color.white))
                }
            }
            editTextViewActionButtonClick()
        }
        if (mSearchStatus == 0) {
            mDialog.show()
        }
    }

    private fun setOfflineList() {
        try {
            var mIssuranceModel: List<CitationInsurranceDatabaseModel?>? = ArrayList()
            mIssuranceModel = mDb?.dbDAO?.getCitationInsurrance()
            if (mIssuranceModel != null) {
                for (i in mIssuranceModel.indices) {
                    if (mIssuranceModel[i]!!.formStatus == 1) {
                        pendingCitation!!.add(getPendingCitationFromDB(mIssuranceModel[i]))
                    }
                }
                //                setAdapterForCitation();
            } //setMixedList(mIssuranceModel);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPendingCitationFromDB(mIssuranceModel: CitationInsurranceDatabaseModel?): Datum {
        val datum = Datum()
        datum.ticketNo = mIssuranceModel!!.citationData?.ticketNumber
        datum.citationIssueTimestamp = mIssuranceModel.citationData?.issueTime
        datum.ticketType = mIssuranceModel.citationData?.ticketType
        datum.status = "Pending"
        datum.ticketNo = mIssuranceModel.citationData?.ticketNumber
        val location = Location()
        location.block = mIssuranceModel.citationData?.location?.block
        location.direction = mIssuranceModel.citationData?.location?.direction
        location.lot = mIssuranceModel.citationData?.location?.lot
        location.meter = mIssuranceModel.citationData?.location?.meterName
        location.side = mIssuranceModel.citationData?.location?.side
        location.street = mIssuranceModel.citationData?.location?.street
        datum.location = location
        val violationDetails = ViolationDetails()
        violationDetails.code = mIssuranceModel.citationData?.voilation?.code
        violationDetails.description = mIssuranceModel.citationData?.voilation?.locationDescr
        violationDetails.fine = mIssuranceModel.citationData?.voilation?.amount.nullSafety("0.0").toDouble()
        violationDetails.fine = mIssuranceModel.citationData?.voilation?.amount.nullSafety("0.0").toDouble()
        violationDetails.late_fine =mIssuranceModel.citationData?.voilation?.amountDueDate.nullSafety("0.0").toDouble()
        violationDetails.due_15_days =mIssuranceModel.citationData?.voilation?.dueDate.nullSafety("0.0").toDouble()
        violationDetails.due_30_days =mIssuranceModel.citationData?.voilation?.dueDate30.nullSafety("0.0").toDouble()
        violationDetails.due_45_days =mIssuranceModel.citationData?.voilation?.dueDate45.nullSafety("0.0").toDouble()
        datum.violationDetails = violationDetails
        datum.lpNumber = mIssuranceModel.citationData?.vehicle?.licensePlate
        //        datum.setNotes(mIssuranceModel.getCitationData().geN);
//        datum.setNotes(mIssuranceModel.getCommentDetails().getNote_1());
        datum.uploadStatus = 1
        return datum
    }

    private fun setOnlineList(mIssuranceModel: List<Datum>): List<CitationMixedList> {
        val mixedLists: MutableList<CitationMixedList> = ArrayList()
        for (i in mIssuranceModel.indices) {
            val datum = CitationMixedList()
            datum.id = mIssuranceModel[i].id
            datum.ticketNo = mIssuranceModel[i].ticketNo
            datum.citationIssueTimestamp = mIssuranceModel[i].citationIssueTimestamp
            datum.ticketType = mIssuranceModel[i].ticketType
            datum.status = mIssuranceModel[i].status
            datum.ticketNo = mIssuranceModel[i].ticketNo
            val location = Location()
            location.block = mIssuranceModel[i].location?.block
            location.direction = mIssuranceModel[i].location?.direction
            location.lot = mIssuranceModel[i].location?.lot
            location.meter = mIssuranceModel[i].location?.meter
            location.side = mIssuranceModel[i].location?.side
            location.street = mIssuranceModel[i].location?.street
            datum.location = location
            val violationDetails = ViolationDetails()
            violationDetails.code = mIssuranceModel[i].violationDetails?.code
            violationDetails.description = mIssuranceModel[i].violationDetails?.description
            violationDetails.fine = mIssuranceModel[i].violationDetails?.fine.nullSafety()
            violationDetails.late_fine = mIssuranceModel[i].violationDetails?.late_fine.nullSafety()
            violationDetails.due_15_days = mIssuranceModel[i].violationDetails?.due_15_days.nullSafety()
            violationDetails.due_30_days = mIssuranceModel[i].violationDetails?.due_30_days.nullSafety()
            violationDetails.due_45_days = mIssuranceModel[i].violationDetails?.due_45_days.nullSafety()
            datum.violationDetails = violationDetails
            val vehicleDetails = VehicleDetails()
            vehicleDetails.color = mIssuranceModel[i].vehicleDetails?.color
            vehicleDetails.make = mIssuranceModel[i].vehicleDetails?.make
            vehicleDetails.model = mIssuranceModel[i].vehicleDetails?.model
            vehicleDetails.state = mIssuranceModel[i].vehicleDetails?.state
            datum.vehicleDetails = vehicleDetails
            datum.lpNumber = mIssuranceModel[i].lpNumber
            datum.notes = mIssuranceModel[i].commentDetails?.note_1
            datum.uploadStatus = 0
            for (imagePrintBitmap in mIssuranceModel[i].images!!) {
                if (imagePrintBitmap.contains(FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                    datum.mPrintBitmap=imagePrintBitmap
                    break
                }
            }
            mixedLists.add(datum)
        }
        return mixedLists
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {
//        (mContext as BaseActivity?)?.logout(mContext!!)
    }

    private fun hideFilterLayout() {
        mLinearLayoutCompatSearch1?.visibility = View.GONE
        mLinearLayoutCompatSearch2?.visibility = View.GONE
        mLinearLayoutCompatSearch3?.visibility = View.GONE
        mLinearLayoutCompatSearch4?.visibility = View.GONE
        mLinearLayoutCompatSearch5?.visibility = View.GONE
    }

    private fun editTextViewActionButtonClick() {
        var arrayListSize = 0
        if (arrPackage != null && arrPackage.size > 0) {
            arrayListSize = Collections.max(arrPackage)
        }
        //        int arrayListSize = arrPackage.size();
        mEditTextSearch1?.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch1!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch2?.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch2!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch3?.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch3!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch4?.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch4!!.filters = arrayOf<InputFilter>(AllCaps())
        mEditTextSearch5?.imeOptions = EditorInfo.IME_ACTION_NEXT
        mEditTextSearch5!!.filters = arrayOf<InputFilter>(AllCaps())

        when (arrayListSize) {
            1 -> mEditTextSearch1?.imeOptions = EditorInfo.IME_ACTION_DONE
            2 -> mEditTextSearch2?.imeOptions = EditorInfo.IME_ACTION_DONE
            3 -> mEditTextSearch3?.imeOptions = EditorInfo.IME_ACTION_DONE
            4 -> mEditTextSearch4?.imeOptions = EditorInfo.IME_ACTION_DONE
            5 -> mEditTextSearch5?.imeOptions = EditorInfo.IME_ACTION_DONE
        }
        mEditTextSearch1?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch2?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch3?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch4?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                return@OnEditorActionListener true
            }
            false
        })
        mEditTextSearch5?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                createURLBeforeCallingAPI(searchViewQuery)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun filterCloseButton() {
        for ((key, value) in filterOption) {
            if (value) {
                arrPackage!!.add(key)
            }
        }
    }

    /**
     * If pending citation status change from server then
     * match from offline and update in offline DB then referesh list
     * @param getData
     */
    private fun updatePendingCitationStatus(getData: List<Datum>) {
        try {
            for (datum in getData) {
                for (i in pendingCitation!!.indices) {
                    if (pendingCitation[i].ticketNo == datum.ticketNo) {
                        mDb?.dbDAO?.updateCitationUploadStatus(0, datum.ticketNo)
                        pendingCitation.removeAt(i)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newInstance(): CitationFragment {
            val fragment = CitationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(value: String?, mAutoComTextViewStreet: AppCompatAutoCompleteTextView) {
//        val mApplicationList = mDatasetList?.dataset?.streetList
        try {
                ioScope.launch {
                    val mApplicationList = Singleton.getDataSetList(DATASET_STREET_LIST, mDb)

                    var pos = -1
                    if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            mDropdownList[i] = mApplicationList[i].street_name.toString()
                            try {
                                if (value != null) {
                                    if (mApplicationList[i].street_name.equals(value, ignoreCase = true)) {
                                        pos = i
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                        mAutoComTextViewStreet.post {
                            try {
                                if(pos>0)
                                    mAutoComTextViewStreet.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                            }

                            //mAutoComTextViewDirection.setText(mApplicationList.get(pos).getDirection()); ;
                            val adapter = activity?.let {
                                ArrayAdapter(
                                        it,
                                        R.layout.row_dropdown_menu_popup_item,
                                        mDropdownList
                                )
                            }
                            try {
                                mAutoComTextViewStreet.threshold = 1
                                mAutoComTextViewStreet.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewStreet.onItemClickListener =
                                        AdapterView.OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                                            activity?.let { AppUtils.hideSoftKeyboard(it) }
                                            createURLBeforeCallingAPI(searchViewQuery)
                                        }
                                if (mAutoComTextViewStreet.tag != null && mAutoComTextViewStreet.tag == "listonly") {
                                    activity?.let { AppUtils.setListOnly(it, mAutoComTextViewStreet) }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}