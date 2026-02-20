package com.parkloyalty.lpr.scan.interfaces

interface Constants {
    companion object {
        const val APP_BAR_ELEVATION = 7f

        //If you add new build flavor here please add the same in gradle.properties as well
        const val FLAVOR_TYPE_PARK = "park"
        const val FLAVOR_TYPE_RISE_TEK = "risetekMuzic"
        const val FLAVOR_TYPE_RISE_TEK_INNOVA = "risetekInnova"
        const val FLAVOR_TYPE_RISE_TEK_OKC = "risetekOkc"
        const val FLAVOR_TYPE_DUNCAN = "duncan"
        const val FLAVOR_TYPE_GLENDALE = "glendale"
        const val FLAVOR_TYPE_GLENDALE_POLICE = "glendalePolice"
        const val FLAVOR_TYPE_CEDAR = "cedar"
        const val FLAVOR_TYPE_STRATOS = "stratos"
        const val FLAVOR_TYPE_RIDGEHILL = "ridgehill"
        const val FLAVOR_TYPE_VALLEJOL = "vallejo"
        const val FLAVOR_TYPE_ENCINITAS = "encinitas"
        const val FLAVOR_TYPE_PHOENIX = "phoenix"
        const val FLAVOR_TYPE_SANDIEGO = "SanDiego"
        const val FLAVOR_TYPE_MILLBRAE = "Millbrae"
        const val FLAVOR_TYPE_CITYOFSANDIEGO = "CityOfSanDiego"
        const val FLAVOR_TYPE_CHULAVISTA = "ChulaVista"
        const val FLAVOR_TYPE_ROSEBURG = "Roseburg"
        const val FLAVOR_TYPE_IRVINE = "Irvine"
        const val FLAVOR_TYPE_MARTIN = "Martin"
        const val FLAVOR_TYPE_LEAVENWORTH = "leavenworth"
        const val FLAVOR_TYPE_FLOWBIRD = "flowbird"
        const val FLAVOR_TYPE_MACKAY = "mackay"
        const val FLAVOR_TYPE_LAZ = "laz"
        const val FLAVOR_TYPE_LAZPILOT = "lazpilot"
        const val FLAVOR_TYPE_CARTA = "carta"
        const val FLAVOR_TYPE_SCPM = "scpm"
        const val FLAVOR_TYPE_MONKTON = "monkton"
        const val FLAVOR_TYPE_SOL = "sol"
        const val FLAVOR_TYPE_BELLINGHAM = "Bellingham"
        const val FLAVOR_TYPE_OCEANCITY = "OceanCity"
        const val FLAVOR_TYPE_DURANGO = "Durango"
        const val FLAVOR_TYPE_CFFB = "CFB"
        const val FLAVOR_TYPE_HILTONHEAD = "HiltonHead"
        const val FLAVOR_TYPE_BURBANK = "BurBankCA"
        const val FLAVOR_TYPE_PILOTPITTSBURGPA = "PilotPittsburgPA"
        const val FLAVOR_TYPE_MANSFIELDCT = "MansFieldCT"
        const val FLAVOR_TYPE_CLEMENS = "ClemensKickOff"
        const val FLAVOR_TYPE_CITY_VIRGINIA = "CityofVirginia"
        const val FLAVOR_TYPE_MYSTICCT = "mysticCT"
        const val FLAVOR_TYPE_CHARLESTON = "Charleston"
        const val FLAVOR_TYPE_BISMARCK = "Bismarck"
        const val FLAVOR_TYPE_SATELLITE = "Satellite"
        const val FLAVOR_TYPE_BURLINGTON = "Burlington"
        const val FLAVOR_TYPE_NORTHERN_CALIFORNIA = "NorthernCalifornia"
        const val FLAVOR_TYPE_ADOBE = "Adobe"
        const val FLAVOR_TYPE_MACKAY_SAMPLE = "MackaySample"
        const val FLAVOR_TYPE_LAZ_ATLANTA = "LazAtlanta"
        const val FLAVOR_TYPE_LAZ_CCP = "LazCcp"
        const val FLAVOR_TYPE_LAZ_KCMO_PRIVATE = "LazKcmoPrivate"
        const val FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT = "LazConjuctivePoint"
        const val FLAVOR_TYPE_SANFRANCISCO = "SanFrancisco"
        const val FLAVOR_TYPE_ACE_FRESNO = "Fresno"
        const val FLAVOR_TYPE_ACE_VALET_DIVISION = "ValetDivision"
        const val FLAVOR_TYPE_ACE_SAN_ANTONIO = "SanAntonio"
        const val FLAVOR_TYPE_ACE_LAKE_TAHOE = "LakeTahoe"
        const val FLAVOR_TYPE_KALAMAZOO = "Kalamazoo"
        const val FLAVOR_TYPE_ISLEOFPALMS = "IsleOfPalms"
        const val FLAVOR_TYPE_LAMETRO = "LAMetro"
        const val FLAVOR_TYPE_LAWRENCE = "Lawrence"
        const val FLAVOR_TYPE_HARTFORD = "HartFord"
        const val FLAVOR_TYPE_COB = "COB"
        const val FLAVOR_TYPE_LAZLB = "LAZLB"
        const val FLAVOR_TYPE_ASHLAND = "Ashland"
        const val FLAVOR_TYPE_RIVEROAKS = "Riveroaks"
        const val FLAVOR_TYPE_ACEAMAZON = "ACEAmazon"
        const val FLAVOR_TYPE_FASHION_CITY = "FashionCity"
        const val FLAVOR_TYPE_BEAUFORT = "Beaufort"
        const val FLAVOR_TYPE_SANIBEL = "Sanibel"
        const val FLAVOR_TYPE_GLASGOW = "Glasgow"
        const val FLAVOR_TYPE_COHASSET = "Cohasset"
        const val FLAVOR_TYPE_SMYRNABEACH = "SmyrnaBeach"
        const val FLAVOR_TYPE_LITTLEROCK = "LittleRock"
        const val FLAVOR_TYPE_PEAKPARKING = "PeakParking"
        const val FLAVOR_TYPE_PEAKTEXAS = "PeakTexas"
        const val FLAVOR_TYPE_RUTGERS = "Rutgers"
        const val FLAVOR_TYPE_MEMORIALHERMAN = "MemorialHerman"
        const val FLAVOR_TYPE_SEPTA = "Septa"
        const val FLAVOR_TYPE_KANSAS_CITY = "KansasCity"
        const val FLAVOR_TYPE_SOUTHMIAMI = "SouthMiami"
        const val FLAVOR_TYPE_EPHRATA = "Ephrata"
        const val FLAVOR_TYPE_WESTCHESTER = "WestChester"
        const val FLAVOR_TYPE_DALLAS = "Dallas"
        const val FLAVOR_TYPE_CLIFTON = "Clifton"
        const val FLAVOR_TYPE_ORLEANS = "Orleans"
        const val FLAVOR_TYPE_ORLEANS_OLD = "Orleans_OLD"
        const val FLAVOR_TYPE_VOLUSIA = "Volusia"
        const val FLAVOR_TYPE_Easton = "Easton"
        const val FLAVOR_TYPE_CORPUSCHRISTI = "CorpusChristi"
        const val FLAVOR_TYPE_PRRS = "PRRS"
        const val FLAVOR_TYPE_OXFORD = "Oxford"
        const val FLAVOR_TYPE_PRIME_PARKING = "PrimeParking"
        const val FLAVOR_TYPE_BOSTON = "boston"
        const val FLAVOR_TYPE_SURF_CITY = "SurfCity"
        const val FLAVOR_TYPE_ATLANTIC_BEACH_NC = "AtlanticBeachNc"
        const val FLAVOR_TYPE_ATLANTIC_BEACH_SC = "AtlanticBeachSc"
        const val FLAVOR_TYPE_ST_JOHNSBURY_VT = "StJohnsburyVT"
        const val FLAVOR_TYPE_WOODSTOCK_GA = "WoodstockGA"
        const val FLAVOR_TYPE_INDIAN_HARBOUR_BEACH = "IndianHarbourBeach"
        const val FLAVOR_TYPE_CITY_OF_WATERLOO = "CityOfWaterloo"
        const val FLAVOR_TYPE_SOUTH_LAKE = "SouthLake"


        const val FLAVOR_TYPE_PARK_RIDGE = "ParkRidge"
        const val FLAVOR_TYPE_ALAMEDA_COUNTY_SHERIFF = "AlamedaCountySheriff"
        const val FLAVOR_TYPE_ALAMEDA_COUNTY_TRANSIT = "AlamedaCountyTransit"
        const val FLAVOR_TYPE_JACKSONVILLA = "Jacksonville"
        const val FLAVOR_TYPE_NORWALK = "Norwalk"
        const val FLAVOR_TYPE_COVINA = "Covina"
        const val FLAVOR_TYPE_KENOSHA = "Kenosha"
        const val FLAVOR_TYPE_WATSONVILLE = "Watsonville"
        const val FLAVOR_TYPE_PORTHOODRIVER = "PorthoodRiver"
        const val FLAVOR_TYPE_VISTA_CA = "VistaCA"
        const val FLAVOR_TYPE_INDIANA_BOROUGH = "IndianaBorough"
        const val FLAVOR_TYPE_MOUNT_RAINIER = "MountRainier"
        const val FLAVOR_TYPE_PARKX = "ParkX"
        const val FLAVOR_TYPE_SANMATEO_REDWOOD = "SanMateoRedwood"
        const val FLAVOR_TYPE_SEASTREAK = "Seastreak"
        const val FLAVOR_TYPE_UPTOWN_ATLANTA = "UptownAtlanta"
        const val FLAVOR_TYPE_DANVILLE_VA = "DanvilleVA"
        const val FLAVOR_TYPE_CAMDEN = "Camden"
        const val FLAVOR_TYPE_WINPARK_TX = "WinparkTx"
        const val FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING = "JacksonvilleFlReimaginedParking"
        const val FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING = "ChattanoogaTnReimaginedParking"
        const val FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING = "CityOfWiltonManorsReimaginedParking"
        const val FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING = "ReefCasperWYReimaginedParking"
        const val FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING = "MobileAlReimaginedParking"
        const val FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING = "FayettevilleNcReimaginedParking"
        const val FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING = "PortLandRo"
        const val FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING = "MerrickParkFLReimaginedParking"
        const val FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING = "HillsboroOrPrivateReimaginedParking"
        const val FLAVOR_TYPE_PHSA_REIMAGINED_PARKING = "PhsaReimaginedParking"
        const val FLAVOR_TYPE_HAMTRAMCK_MI = "HamtramckMi"
        const val FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD = "MtaLongIslandRailRoad"
        const val FLAVOR_TYPE_GREENBURGH_NY = "GreenburghNY"
        const val FLAVOR_TYPE_BOTTLEWORKS_IN = "BottleworksIn"
        const val FLAVOR_TYPE_ACE_HGI_MANHATTAN = "AceHgiManhattan"
        const val FLAVOR_TYPE_A_BOBS_TOWING = "ABobsTowing"
        const val FLAVOR_TYPE_EL_PASO_TX_MACKAY = "ElPasoTxMackay"
        const val FLAVOR_TYPE_IMPARK_PHSA = "ImParkPHSA"
        const val FLAVOR_TYPE_EL_CAMINO_COLLEGE = "ElCaminoCollege"
        const val FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO = "AceSanDiegoZoo"
        const val FLAVOR_TYPE_JEFFERSON_CITY_MO = "JeffersonCityMo"
        const val FLAVOR_TYPE_VEST_SECURITY_SYSTEM = "VestSecuritySystem"
        const val FLAVOR_TYPE_DEER_FIELD_BEACH_FL = "DeerFieldBeachFL"
        const val FLAVOR_TYPE_WELLS_PD = "WellsPd"
        const val FLAVOR_TYPE_STORMWATER_DIVISION = "StormwaterDivision"
        const val FLAVOR_TYPE_TEST = "test"

        //If you add new release type here please add the same in gradle.properties as well
        const val RELEASE_TYPE_UAT = "UAT"
        const val RELEASE_TYPE_PROD = "PROD"

        /*date  format*/
        const val DATE_FORMAT_YYYY_MM_DD = "dd MMM yyyy"
        const val DATE_FORMAT_dd_MM_yyyy = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val DATE_FORMAT_dd_mm_yyyy_ = "dd MMM yyyy"

        //    public String SITE_ID = "60105ba4-8d60-4d35-a84f-5e9255b83cd8";// risetekInnova
        //    public String SITE_ID = "d57ca4fd-53f2-4894-8cb4-51061b569670";//park
        const val SPLAHS_TIME_OUT = 1000
        const val ANDROID = "Android"

        const val TABLET = "Tablet"
        const val PHONE = "Phone"
        const val SPACE_WITH_COMMA = " "

        //For decrypt Token
        const val SECRET_KEY = "TheBestSecretKey"
        const val INTERNAL_FOLDER_NAME = "send_bits"
        const val DEFAULT_IMAGE_SIZE = 240
        const val FROM_TAB_CHANGE = "from_tab_change"
        const val SESSION = "SESSION"
        const val FROM_SCREEN_WELCOME = "CITATION_FORM_COMPLETION"
        const val SUCCESS_CITATION_FORM = "SUCCESS_CITATION_FORM"
        const val FROM_SCREEN_FORGET_PASS = "FORGET_PASSWORD"
        const val FROM_SCREEN_DASHBOARD = "DASHBOARD"
        const val FROM_SCREEN_LPR_DETAILS = "SUCCESS_LPR_SCAN"
        const val FROM_SCREEN_LPR_SCAN = "LPR_SCAN_COMPLETION"
        const val FROM_SCREEN_LOGIN = "LOGIN"
        const val LOCATION_UPDATE = "LOCATION_UPDATE"
        const val gallery = "gallery"
        const val camera = "camera"
        const val TITLE = "title"
        const val MESSAGE = "MESSAGE"
        const val CHANGE_PASSWORD = "CHANGE_PASSWORD"
        const val CHANGE_PIN = "CHANGE_PIN"
        const val KEY_OBJECT = "KEY_Object"

        /*SCREEEN NAME */
        const val DASHBOARD = "DASHBOARD"
        const val LOGIN = "LOGIN"
        const val REGISTRATION = "REGISTRATION"
        const val SETTING = "Setting"
        const val MY_PROFILE = "MY_PROFILE"
        const val KEY_TITLE = "title"
        const val KEY_FROM_SCREEN = "from_screen"
        const val CREATE_PIN = "create_pin"
        const val PAYMENT_INFO = "payment_info"
        const val SCANNED_RESULT = "scannedResult"
        const val CreatePIN = "create_pin"
        const val REGISTRATION_MY_PROFILE = "register_myProfile"
        const val SCANNER = "/ScannerImages"
        const val COTINOUS = "/CotinuesImages"
        const val CAMERA = "/CameraImages"
        const val VINCAMERA = "/VinCameraImages"
        const val SIGNATURE = "/Signature"
        const val QRCODE = "/QrCodeImages"
        const val LPRSCANIMAGES = "/LprScanImages"
        const val ALLREPORTIMAGES = "/AllReportImages"
        const val DATABASEBACKUP = "/DataBaseBackUp"
        const val AddNote = "/AddNote"
        const val HEADER_FOOTER = "/HeaderFooterImages"
        const val OTP = "otp"
        const val RESEND_OTP = "resend_otp"
        const val UPDATE_PROFILE_INFO = "update_profile_info"
        const val FILE_NAME = "ParkLoyalty"
        const val FETCH_HISTORY_INFO = "fetch_history_info"
        const val FETCH_HISTORY_INFO_More = "fetch_history_info_More"
        const val UPLOAD_DOCUMENT = "upload_document"
        const val UPDATE_DOCUMENT = "update_document"
        const val FETCH_USER_CHARGE = "fetch_charge"
        const val UPDATE_CARD = "update_card"
        const val BANK_LIST = "bank_list"
        const val HISTORY_ITEM = "history_item"
        const val CALL = "call"
        const val SEND_TEXT = "send_text"
        const val IS_FRONT_IMAGE = 0
        const val IS_BACK_IMAGE = 1

        //    int  APICOUNT = 17;
        const val FORGOT_PASSWORD = "forgot_password"
        const val CONFIRM_PIN = "confirm_pin"
        const val SEND_MONEY = "send_money"

        //for 1 min
        /* long INTERVAL = 1 * 60 * 1000; //1 min
        long FASTEST_INTERVAL = 1 * 60 * 1000; //1 min
        long SERVICE_TIME = 60; //1 min*/
        //for 5 min
        const val INTERVAL = (5 * 60 * 1000).toLong() //5 min

        const val FASTEST_INTERVAL = (2 * 60 * 1000).toLong()//2 min

        const val SERVICE_TIME: Long = 300 //5 min
        const val LOCATION_KEY = "location_key" //regular
        const val LOCATION_KEYLogin = "location_key_login" //login
        const val LOCATION_KEYLogout = "location_key_login" //logout
        const val LOCATION_KEYCitation = "location_key_citation" //citation
        const val LOCATION_KEYActivity = "location_key_activity" //break
        const val LOCATION_DATA_KEY = "location_data_key"
        const val SEND_LOCATION_DATA = "SEND_LOCATION_DATA"
        const val PRINT_LAYOUT_ORDER_SPARATER = "0#p"
        const val SETTING_VALUE_PRINTER = "Zebra"
        const val SETTING_PRINTER_TYPE = "Printer_Type"
        
        /*Activity Type*/
        const val ACTIVITY_TYPE_LOCATION_UPDATE = "LocationUpdate"
        /*Activity Type*/
        
        /*Log Type*/
        const val LOG_TYPE_NODE_PORT = "NodePort"
        /*Log Type*/

        /*Location Update Type*/
        const val LOCATION_UPDATE_TYPE_REGULAR = "regular"
        const val PRINT_LAYOUT_VERTICAL = "YES"
        const val PRINT_TEXT_LARGE = "YES"
        /*Location Update Type*/

        /* Adapter */
        const val VIEW_ITEM_CONTAINER = 0
        const val VIEW_ITEM_LOADING = 1
        const val SAVEPRINTBITMAPDELAYTIME:Long = 3000
        const val END_X_FOR_BOX_NOLA:Double = 563.0
        const val END_X_FOR_BOX_BOSTON:Double = 550.0
        const val END_X_FOR_BOX_OCEAN_CITY:Double = 545.0
//        var isTireStemWithImageView:Boolean? = false

        const val PPA_SAN_DIEGO_LPR_EMPTY_THEN_VIN_MIN= 11

        /*Print Constant*/
        const val PRINT_SECTION_VIOLATION = "violation"
        const val PRINT_LINE_HEIGHT = 2
        const val PRINT_EXTRA_BOTTOM_MARGIN_FOR_CMD = 50
        /*Print Constant*/

        /*Setting File Constant*/
        //Flag
        const val SETTINGS_FLAG_IS_AUTO_TIMING = "IS_AUTO_TIMING"
        const val SETTINGS_FLAG_TIMING_RECORD_CARRY_FORWARD = "TIMING_RECORD_CARRY_FORWARD"
        const val SETTINGS_FLAG_DEFAULT_REGULATION_TIME_AUTO = "DEFAULT_REGULATION_TIME_AUTO"
        const val SETTINGS_FLAG_OFFICER_NAME_FORMAT_FOR_PRINT = "OFFICER_NAME_FORMAT_FOR_PRINT"
        const val SETTINGS_FLAG_INVENTORY_MODULE = "INVENTORY_MODULE"
        const val SETTINGS_FLAG_HEADER_FOOTER_IN_FACSIMILE = "HEADER_FOOTER_IN_FACSIMILE"
        const val SETTINGS_FLAG_HEADER_URL_FOR_FACSIMILE = "HEADER_URL_FOR_FACSIMILE"
        const val SETTINGS_FLAG_FOOTER_URL_FOR_FACSIMILE = "FOOTER_URL_FOR_FACSIMILE"
        const val SETTINGS_FLAG_BEAT_FIELD_EMPTY_AFTER_EVERY_LOGIN = "SETTINGS_FLAG_BEAT_FIELD_EMPTY_AFTER_EVERY_LOGIN"
        const val SETTINGS_FLAG_SHOW_CLEAR_ICON_FOR_INPUT_FIELDS = "SHOW_CLEAR_ICON_FOR_INPUT_FIELDS"
        const val SETTINGS_FLAG_REMARK_AUTO_FILLED_WITH_ELAPSED_TIME = "REMARK_AUTO_FILLED_WITH_ELAPSED_TIME"
        const val SETTINGS_FLAG_NARROW_DOWN_STREET_BLOCK_TO_SELECTION_ZONE = "NARROW_DOWN_STREET_BLOCK_TO_SELECTION_ZONE"
        const val SETTINGS_FLAG_PRINTINGBY = "PRINTINGBY"
        const val SETTINGS_FLAG_COPY_VIN_LAST_8_DIGIT_PASTE_LPR_NUMBER = "FULL_VIN_COPY8_LPR_EMPTY_STATE"
        const val SETTINGS_FLAG_SCAN_VEHICLE_REGISTRATION_STICKER = "SCAN_VEHICLE_REGISTRATION_STICKER"
        const val SETTINGS_FLAG_DIRECTED_ENFORCEMENT_MODULE = "DIRECTED_ENFORCEMENT_MODULE"
        const val SETTINGS_FLAG_CAMERA_FEED_GUIDE_ENFORCEMENT = "CAMERA_FEED_GUIDE_ENFORCEMENT"
        const val SETTINGS_FLAG_CAMERA_GUIDE_ENFORCEMENT = "CAMERA_GUIDE_ENFORCEMENT"//leaven worth
        const val SETTINGS_FLAG_IS_METER_BUZZER_ACTIVE = "IS_METER_BUZZER_ACTIVE"
        const val SETTINGS_FLAG_IS_GENERATE_QR_CODE_VISIBILE = "IS_GENERATE_QR_CODE_VISIBILE"
        const val SETTINGS_FLAG_IS_GENERATE_QR_CODE_VISIBLE = "IS_GENERATE_QR_CODE_VISIBLE"
        const val EXPIRY_YEAR_IS_DROP_DOWN = "EXPIRY_YEAR_IS_DROP_DOWN"
        const val SETTINGS_FLAG_BAR_CODE_HEIGHT = "BAR_CODE_HEIGHT"
        const val SETTINGS_FLAG_BAR_CODE_X = "BAR_CODE_X"
        const val SETTINGS_FLAG_BAR_CODE_Y = "BAR_CODE_Y"
        const val SETTINGS_FLAG_HEARING_DATE_THRESHOLD_DAY = "HEARING_DATE_THRESHOLD_DAY"

        //Value
        const val SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_INITIAL_LAST_FULL = "FIRST_INITIAL_LAST_FULL"
        const val SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_LAST_INITIAL = "FIRST_FULL_LAST_INITIAL"
        const val SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_LAST_FULL = "FIRST_FULL_LAST_FULL"
        const val SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_INITIAL_LAST_INITIAL = "FIRST_INITIAL_LAST_INITIAL"
        const val SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_ONLY = "FIRST_FULL_ONLY"
        const val SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_NAME_BLANK_LAST_NAME_FULL = "FIRST_NAME_BLANK_LAST_NAME_FULL"
        const val SETTINGS_FLAG_QR_CODE_SIZE = "QR_CODE_SIZE"
        const val SETTINGS_ENABLE_VIN_FIELD_AS_DROP_DOWN = "ENABLE_VIN_FIELD_AS_DROP_DOWN"
        const val SETTINGS_VIN_FIELD_AS_DROP_DOWN_OPTION = "VIN_FIELD_AS_DROP_DOWN_OPTION"

        /*Setting File Constant*/

        const val VIN_DROPDOWN_OPTION_CUU = "CUU"
        const val VIN_DROPDOWN_OPTION_UVV = "UVV"

        /*File Name Constant*/
        const val FILE_NAME_API_PAYLOAD = "API_Payload"
        const val FILE_NAME_HEADER_FOOTER_BITMAP = "header_footer_bitmap"
        const val FILE_NAME_FACSIMILE_PRINT_BITMAP = "print_bitmap"
        const val FILE_NAME_FACSIMILE_OCR_BITMAP = "facsimile_ocr_bitmap"

        const val UNUPLOAD_IMAGE_TYPE_FACSIMILE = "Facsimile"
        const val DIRECTED_ENFORCEMENT = "DIRECTED_ENFORCEMENT"

        const val TIME_FORMAT = "MILITARY"
        const val COMMA = ","
        const val DOT = "."
        /*File Name Constant*/
        const val HONOR_BILL_ACTIVITY = "Honor_Bill"
        const val MUNICIPAL_ACTIVITY = "Municipal"
        const val CITATION_ACTIVITY = "Citation"

        val stickerList = arrayListOf("White Sticker", "Red Sticker")

    }
}