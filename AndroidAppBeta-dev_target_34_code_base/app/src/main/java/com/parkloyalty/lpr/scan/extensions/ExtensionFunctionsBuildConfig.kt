package com.parkloyalty.lpr.scan.extensions

import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.getMyDatabase
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST

/**
 * This function is used for call timing api in scan result page
 */
fun checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpenGOA(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used for call timing api in scan result page
 */
fun checkBuildConfigForTimingAPICallScanResultPageWithArrivalStatusOpen(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
        BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to remark auto filled
 */
fun checkBuildConfigForAutoFilledRemark(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, true)
    ) {
        return true
    } else {
        return false
    }
}

/**
  * This function is used to bottom address for cmd
  */


/*
fun checkBuildConfigForBottomAddressInCommand(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) ||
 BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}*/

/**
 * This function is used to Top setff for facsimile print static 5 4
 */
fun checkBuildConfigForTopSetFFValueStaticBitmapPrint_5_4(): Boolean {
    if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BURLINGTON,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ENCINITAS,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHULAVISTA,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ROSEBURG,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_COHASSET,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MARTIN,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IRVINE,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANFRANCISCO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_FRESNO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACEAMAZON,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FASHION_CITY,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
            ignoreCase = true
        )|| BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GREENBURGH_NY,
            ignoreCase = true
        )
       || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHOENIX,
            ignoreCase = true
        )
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to get top setff value from setting file
 */
fun checkBuildConfigForTopSetFFValueSettingFileBitmapPrint(): Boolean {
    if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CARTA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DUNCAN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ISLEOFPALMS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BISMARCK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HARTFORD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANIBEL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LITTLEROCK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_KANSAS_CITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
            ignoreCase = true
        )||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
            ignoreCase = true
        )||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SEASTREAK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PEAKPARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RUTGERS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PEAKTEXAS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SMYRNABEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DURANGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EPHRATA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_Easton,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CLIFTON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ORLEANS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BOSTON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VOLUSIA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VALLEJOL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHARLESTON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DALLAS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_VIRGINIA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SATELLITE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_OCEANCITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LEAVENWORTH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANDIEGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WOODSTOCK_GA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ASHLAND,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VISTA_CA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to preview Activity for white background image check
 */
fun checkBuildConfigForLoadBitmapPreviewActivityForWhiteImageForPrint(): Boolean {
    if (
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to get top setff value from setitng file
 */
fun checkBuildConfigForCitationFormVinNumberNotCopyInLprField(): Boolean {
    if(
//            !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
//            &&!BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
//            &&!BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
//            &&!BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
//            &&!BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true
//            )  &&!BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
//            ) &&!BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
//            ) && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON,ignoreCase = true)
//            && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)
//            && getSettingFileValuesForCopyLast8DigitVinAndPasteItInLprField()||
            getSettingFileValuesForCopyLast8DigitVinAndPasteItInLprField()){
        return true
    } else {
        return false
    }
}

/**
 * This function is used to Top setff for facsimile print static 10 8
 */
fun checkBuildConfigForTopSetFFValueStaticBitmapPrint_10_8(): Boolean {
    if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GLENDALE,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GLENDALE_POLICE,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAMETRO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CORPUSCHRISTI,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WESTCHESTER,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RISE_TEK,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RISE_TEK_OKC,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,
            ignoreCase = true
        )
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to Top setff for facsimile print static 5 2
 */
fun checkBuildConfigForTopSetFFValueStaticBitmapPrint_5_2(): Boolean {
    if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAWRENCE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BEAUFORT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SURF_CITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CLEMENS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_KALAMAZOO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_CCP,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RIVEROAKS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MACKAY_SAMPLE,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_A_BOBS_TOWING,
            ignoreCase = true
        )
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to Bottom setff for facsimile print setting file
 */
fun checkBuildConfigForBottomSetFFValueSettingFileBitmapPrint(): Boolean {
    if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VALLEJOL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHARLESTON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LEAVENWORTH,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_OCEANCITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EPHRATA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_Easton,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_VIRGINIA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SATELLITE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CLIFTON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BISMARCK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HARTFORD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ISLEOFPALMS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GLASGOW,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LITTLEROCK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_KANSAS_CITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SEASTREAK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_OXFORD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PRIME_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DANVILLE_VA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CAMDEN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SOUTH_LAKE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WINPARK_TX,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PEAKPARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RUTGERS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PEAKTEXAS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DURANGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SMYRNABEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ORLEANS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BOSTON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VOLUSIA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DALLAS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILTONHEAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANIBEL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANDIEGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WOODSTOCK_GA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ASHLAND,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DUNCAN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VISTA_CA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to Bottom setff for facsimile print 20 8
 */
fun checkBuildConfigForBottomSetFFValueStaticBitmapPrint_20_8(): Boolean {
    if (
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_CCP,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FASHION_CITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
            ignoreCase = true
        ) ||BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GREENBURGH_NY,
            ignoreCase = true
        )  ||BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACEAMAZON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RIVEROAKS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SURF_CITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to Preview Activity for citation adapter with gird 3
 */
fun checkBuildConfigForPreviewActivityCitationAdapterGrid_3(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STRATOS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to printer activity for check top and bottom value
 */
fun checkBuildConfigForPrinterActivityForTakeTopAndBottomValueFromSettingFile(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DANVILLE_VA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CAMDEN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GREENBURGH_NY,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MEMORIALHERMAN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to preview Activity for create view to print method for QR code
 */
fun checkBuildConfigForPreviewActivityForQRCode(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to preview Activity for create view to print method for Top Message
 */
fun checkBuildConfigForPreviewActivityForTopMessage(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to preview Activity for create view to print method for Top Message
 */
fun checkBuildConfigForPreviewActivityLoadBitmapForSaveOCRImage(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to LprDetails2 Activity for vin dropdown
 */
fun checkBuildConfigForLprDetails2ForVinNumberSupportDropDown(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
//        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VISTA_CA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, true)||
        getSettingFileValuesForEnableVinFieldAsDropdown()
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to LprDetails2 Activity for warning type
 */
fun checkBuildConfigForLprDetails2ForGetWarningFromObject(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to LprDetails2 Activity for time stamp on camera image
 */
fun checkBuildConfigForLprDetails2ForTimeStampOnCameraImage(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to LprDetails2 Activity for base 64 image compress
 */
fun checkBuildConfigForLprDetails2ForBase64ImageCompress(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to Citation adapter Facsimile without Bx
 */
fun checkBuildConfigForCitationFormFacsimileWithoutBox(): Boolean {
    if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) &&
        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used to timing adapter click and carry forward elase time in citation form
 */
fun checkBuildConfigForTimingDataClickOnScanResultScreenForAutoFillRemark(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used for take bottom set ff value from setting file for command print
 */
fun checkBuildConfigForBottomSetFFCMDGetFromSettingFile(): Boolean {
    if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_OCEANCITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DUNCAN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EPHRATA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_Easton,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_VIRGINIA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SATELLITE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CLIFTON,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BISMARCK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HARTFORD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ISLEOFPALMS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GLASGOW,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LITTLEROCK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_KANSAS_CITY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SEASTREAK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_OXFORD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PRIME_PARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DANVILLE_VA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CAMDEN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SOUTH_LAKE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WINPARK_TX,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PEAKPARKING,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RUTGERS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PEAKTEXAS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DURANGO,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SMYRNABEACH,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||

        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BOSTON,
            ignoreCase = true
        ) ||

        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DALLAS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GREENBURGH_NY,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILTONHEAD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MEMORIALHERMAN,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANIBEL,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WOODSTOCK_GA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MILLBRAE,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ASHLAND,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VOLUSIA,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BURBANK,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ORLEANS,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CFFB,
            ignoreCase = true
        ) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used for take top set ff value from setting file for command print
 */
fun checkBuildConfigForTopSetFFCMDGetFromSettingFile (): Boolean {
    if(BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CARTA,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DUNCAN,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ISLEOFPALMS,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BISMARCK,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_HARTFORD,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SANIBEL,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LITTLEROCK,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_KANSAS_CITY,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SEASTREAK,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_OXFORD,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PRIME_PARKING,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DANVILLE_VA,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CAMDEN,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SOUTH_LAKE,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_WINPARK_TX,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PEAKPARKING,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_RUTGERS,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PEAKTEXAS,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SMYRNABEACH,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DURANGO,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_EPHRATA,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_Easton,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CLIFTON,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ORLEANS,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BOSTON,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_VALLEJOL,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CHARLESTON,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DALLAS,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CITY_VIRGINIA,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SATELLITE,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
                    ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_GREENBURGH_NY,
                    ignoreCase = true
            )  || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_HILTONHEAD,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_OCEANCITY,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_MEMORIALHERMAN,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_WOODSTOCK_GA,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_MILLBRAE,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_ASHLAND,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_VOLUSIA,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_CFFB,
                    ignoreCase = true
            ) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)

    ) {
        return true
    } else {
        return false
    }
}

/**
 * This function is used for recursive call for generate facsimile Image Method In Preview Activity
 */
fun checkBuildConfigForGenerateFacsimileImageMethodForRecursiveCall(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STRATOS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MEMORIALHERMAN,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_IMPARK_PHSA,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,
            ignoreCase = true
        )|| BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_A_BOBS_TOWING,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,
            ignoreCase = true
        )|| BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
            ignoreCase = true
        )|| BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
            ignoreCase = true
        )|| BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GREENBURGH_NY,
            ignoreCase = true
        )
       || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
            ignoreCase = true
        )
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, true)
        || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, true)
    ) {
        return true
    } else {
        return false
    }
}


/**
 * Function used to fetch setting files values
 */
fun getSettingFileValuesForCopyLast8DigitVinAndPasteItInLprField(): Boolean {
        val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

        val isEnabled = settingsList?.firstOrNull {
                it.type.equals(Constants.SETTINGS_FLAG_COPY_VIN_LAST_8_DIGIT_PASTE_LPR_NUMBER, ignoreCase = true)
                        && it.mValue.toBooleanFromYesNo()
        }?.mValue.toBooleanFromYesNo() ?: false

        return isEnabled
}

fun checkBuildConfigForMonthYearPickerDialog(): Boolean {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true)
        || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true)
    ) {
        return true
    } else {
        return false
    }
}

fun getExpiryDateFormatForDatePickerDialog(): String {
    if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GLENDALE,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_GLENDALE_POLICE,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAMETRO,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_CORPUSCHRISTI,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BURBANK,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_WESTCHESTER,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_LAZLB,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_VALLEJOL,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ENCINITAS,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_NORWALK,
            ignoreCase = true
        )
    ) {
        return "MM/yy"
    } else if (BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_ORLEANS,
            ignoreCase = true
        ) || BuildConfig.FLAVOR.equals(
            Constants.FLAVOR_TYPE_BOSTON,
            ignoreCase = true
        )
    ) {
        return "MM/dd/yyyy"
    } else {
        return "MM/yyyy"
    }
}

fun getExpiryDateFormatForMonthYearPickerDialog(): String {
    if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)
    ) {
        return "yy"
    } else {
        return "MM/yy"
    }
}

/**
 * Function used to fetch setting files values
 */
fun getSettingFileValuesForEnableVinFieldAsDropdown(): Boolean {
    val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

    val isEnabled = settingsList?.firstOrNull {
        it.type.equals(Constants.SETTINGS_ENABLE_VIN_FIELD_AS_DROP_DOWN, ignoreCase = true)
                && it.mValue.toBooleanFromYesNo()
    }?.mValue.toBooleanFromYesNo() ?: false

    return isEnabled
}
/**
 * Function used to fetch setting files values
 */
fun getSettingFileValuesForVinFieldAsDropdownOption(): String {
    val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

    val value = settingsList?.firstOrNull {
        it.type.equals(Constants.SETTINGS_VIN_FIELD_AS_DROP_DOWN_OPTION, ignoreCase = true)
    }?.mValue

    return value?.toString() ?: ""
}

//NEW Functions : JANAK
fun hideCompanyTitleOnLoginScreen() : Boolean {
    val flavorsToHide = setOf(
        Constants.FLAVOR_TYPE_GLASGOW,
        Constants.FLAVOR_TYPE_FLOWBIRD,
        Constants.FLAVOR_TYPE_MACKAY,
        Constants.FLAVOR_TYPE_MACKAY_SAMPLE,
        Constants.FLAVOR_TYPE_STRATOS
    )
    return flavorsToHide.any { BuildConfig.FLAVOR.equals(it, true) }
}


fun hideShiftTimeDropdownOnLoginScreen() : Boolean {
    val flavorsToHide = setOf(
        Constants.FLAVOR_TYPE_CHARLESTON,
        Constants.FLAVOR_TYPE_COHASSET,
        Constants.FLAVOR_TYPE_PEAKPARKING,
        Constants.FLAVOR_TYPE_PARKX,
        Constants.FLAVOR_TYPE_RUTGERS,
        Constants.FLAVOR_TYPE_SEPTA,
        Constants.FLAVOR_TYPE_PEAKTEXAS,
        Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
        Constants.FLAVOR_TYPE_MEMORIALHERMAN,
        Constants.FLAVOR_TYPE_GREENBURGH_NY,
        Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
        Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
        Constants.FLAVOR_TYPE_CITYOFSANDIEGO
    )
    return flavorsToHide.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun showHearingDateTimeDropdownOnLoginScreen() : Boolean {
    val flavorsToShow = setOf(
        Constants.FLAVOR_TYPE_ORLEANS
    )
    return flavorsToShow.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun writeDefaultShiftTimeToSharedPreference() : Boolean {
    val flavorsToWrite = setOf(
        Constants.FLAVOR_TYPE_CHARLESTON,
        Constants.FLAVOR_TYPE_COHASSET,
        Constants.FLAVOR_TYPE_PEAKPARKING,
        Constants.FLAVOR_TYPE_PARKX,
        Constants.FLAVOR_TYPE_RUTGERS,
        Constants.FLAVOR_TYPE_SEPTA,
        Constants.FLAVOR_TYPE_PEAKTEXAS,
        Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
        Constants.FLAVOR_TYPE_MEMORIALHERMAN,
        Constants.FLAVOR_TYPE_GREENBURGH_NY,
        Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
        Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
        Constants.FLAVOR_TYPE_CITYOFSANDIEGO
    )
    return flavorsToWrite.any { BuildConfig.FLAVOR.equals(it, true) }
}

//Create Citation Ticket Checks
fun addImpoundCodeInCreateTicket(): Boolean {
    val flavorsToAddImpoundCode = setOf(
        Constants.FLAVOR_TYPE_LAMETRO
    )
    return flavorsToAddImpoundCode.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun addUnpaidCitationBasedInvoiceFeeStructureInCreateTicket(): Boolean {
    val flavorsToAddInvoiceStructure = setOf(
        Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING
    )
    return flavorsToAddInvoiceStructure.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun addConditionalOfficerNameInCreateTicket(): Boolean {
    val flavorsToAddConditionalOfficerName = setOf(
        Constants.FLAVOR_TYPE_DURANGO,
        Constants.FLAVOR_TYPE_PEAKPARKING,
        Constants.FLAVOR_TYPE_RUTGERS,
        Constants.FLAVOR_TYPE_PEAKTEXAS,
        Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
        Constants.FLAVOR_TYPE_LITTLEROCK,
    )
    return flavorsToAddConditionalOfficerName.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun addPeoDetailsInCreateTicket(): Boolean {
    val flavorsToAddPeoDetails = setOf(
        Constants.FLAVOR_TYPE_DUNCAN,
        DuncanBrandingApp13(),
        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
        Constants.FLAVOR_TYPE_HARTFORD,
    )
    return flavorsToAddPeoDetails.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun addTicketTypeValueWarningInCreateTicket(): Boolean {
    val flavorsToAddTicketTypeValueWarning = setOf(
        Constants.FLAVOR_TYPE_DURANGO,
        Constants.FLAVOR_TYPE_PEAKPARKING,
        Constants.FLAVOR_TYPE_RUTGERS,
        Constants.FLAVOR_TYPE_PEAKTEXAS,
        Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,
        Constants.FLAVOR_TYPE_LITTLEROCK,
    )

    return flavorsToAddTicketTypeValueWarning.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun addTimeLimitEnforcementTimeFromSharedPreferenceInCreateTicket(): Boolean {
    val flavorsToAddTimeLimitEnforcement = setOf(
        Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
        Constants.FLAVOR_TYPE_STORMWATER_DIVISION
    )
    return flavorsToAddTimeLimitEnforcement.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun isFlavorForRedirectToScofflaw(): Boolean {
    val flavorToAutoRedirectForScofflaw = setOf(
        Constants.FLAVOR_TYPE_DUNCAN,
        DuncanBrandingApp13(),
        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
        Constants.FLAVOR_TYPE_MACKAY_SAMPLE
    )
    return flavorToAutoRedirectForScofflaw.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun isFlavorForPriorWarningHandling(): Boolean {
    val flavorForPriorWarningHandling = setOf(
        Constants.FLAVOR_TYPE_GLENDALE,
        Constants.FLAVOR_TYPE_GLENDALE_POLICE,
        Constants.FLAVOR_TYPE_LAMETRO,
        Constants.FLAVOR_TYPE_CORPUSCHRISTI,
        Constants.FLAVOR_TYPE_VALLEJOL,
        Constants.FLAVOR_TYPE_ENCINITAS,
        Constants.FLAVOR_TYPE_PHOENIX,
        Constants.FLAVOR_TYPE_MEMORIALHERMAN
    )
    return flavorForPriorWarningHandling.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun isFlavorForFinalWarningHandling(): Boolean {
    val flavorForFinalWarningHandling = setOf(
        Constants.FLAVOR_TYPE_FASHION_CITY
    )
    return flavorForFinalWarningHandling.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun isFlavorForCitationHistoryPrinter(): Boolean {
    val flavorForCitationHistoryPrinter = setOf(
        Constants.FLAVOR_TYPE_ORLEANS, Constants.FLAVOR_TYPE_BOSTON
    )
    return flavorForCitationHistoryPrinter.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun isFlavorForBlockCitationHistoryItemClick(): Boolean{
    val flavorForBlockCitationHistoryItemClick = setOf(
        Constants.FLAVOR_TYPE_LAMETRO, Constants.FLAVOR_TYPE_CORPUSCHRISTI
    )
    return flavorForBlockCitationHistoryItemClick.any { BuildConfig.FLAVOR.equals(it, true) }
}

fun isFlavorForNotAssigningStreetBlockSideFromCitationHistoryItemClick(): Boolean{
    val flavorForNotAssigningStreetBlockSideFromCitationHistoryItemClick =setOf(
        Constants.FLAVOR_TYPE_GLENDALE,
        Constants.FLAVOR_TYPE_GLENDALE_POLICE,
        Constants.FLAVOR_TYPE_LAMETRO,
        Constants.FLAVOR_TYPE_CORPUSCHRISTI,
        Constants.FLAVOR_TYPE_BURBANK,
        Constants.FLAVOR_TYPE_WESTCHESTER,
        Constants.FLAVOR_TYPE_LAZLB,
        Constants.FLAVOR_TYPE_VALLEJOL,
        Constants.FLAVOR_TYPE_ENCINITAS,
        Constants.FLAVOR_TYPE_PHOENIX,
        Constants.FLAVOR_TYPE_SANDIEGO,
        Constants.FLAVOR_TYPE_MILLBRAE,
        Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
        Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
        Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
        Constants.FLAVOR_TYPE_CHULAVISTA,
        Constants.FLAVOR_TYPE_ROSEBURG,
        Constants.FLAVOR_TYPE_COHASSET,
        Constants.FLAVOR_TYPE_MARTIN,
        Constants.FLAVOR_TYPE_IRVINE,
        Constants.FLAVOR_TYPE_SANFRANCISCO,
        Constants.FLAVOR_TYPE_ACE_FRESNO,
        Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,
        Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,
        Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,
        Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,
        Constants.FLAVOR_TYPE_DALLAS,
        Constants.FLAVOR_TYPE_SANIBEL,
        Constants.FLAVOR_TYPE_SMYRNABEACH,
        Constants.FLAVOR_TYPE_VOLUSIA,
        Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,
        Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
        Constants.FLAVOR_TYPE_ACEAMAZON,
        Constants.FLAVOR_TYPE_FASHION_CITY,
        Constants.FLAVOR_TYPE_PRRS,
        Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
        Constants.FLAVOR_TYPE_GREENBURGH_NY,
        Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
        Constants.FLAVOR_TYPE_ADOBE
    )
    return flavorForNotAssigningStreetBlockSideFromCitationHistoryItemClick.any { BuildConfig.FLAVOR.equals(it, true) }
}