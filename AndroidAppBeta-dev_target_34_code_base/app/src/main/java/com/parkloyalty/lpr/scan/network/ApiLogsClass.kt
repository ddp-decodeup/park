package com.parkloyalty.lpr.scan.network

import android.content.Context
import android.os.Environment
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_API_PAYLOAD
import com.parkloyalty.lpr.scan.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException

object ApiLogsClass {


    @JvmStatic
    fun writeApiPayloadTex(mContext: Context, apiPayload : String){
        try
        {
                CoroutineScope(Job() + Dispatchers.Main).async {
                    val localFolder =
                        File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            Constants.FILE_NAME
                        )
                    if (!localFolder.exists()) {
                        localFolder.mkdirs()
                    }
                    val fileName1 = FILE_NAME_API_PAYLOAD + ".txt" //like 2016_01_12.txt
                    val file = File(localFolder, fileName1)
                    val writer = FileWriter(file, true)
                    writer.append(apiPayload).append("\n\n")
                    writer.flush()
                    writer.close()
                    if (!file.exists()) {
                        try {
                            file.createNewFile()
                        } catch (e: IOException) {
                            e.printStackTrace()

                        }
                    }
            }
        }
        //Catching any file errors that could occur
        catch(e: FileNotFoundException)
        {
            e.printStackTrace()
        }
        catch(e:NumberFormatException)
        {
            e.printStackTrace()
        }
        catch(e: IOException)
        {
            e.printStackTrace()
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }
    }
}