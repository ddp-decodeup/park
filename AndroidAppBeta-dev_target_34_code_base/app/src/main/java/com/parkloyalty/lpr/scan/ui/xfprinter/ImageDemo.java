//***************************************************************************
// Image Print Demo - encapsulates the image demo code
//
// Copyright (c)2015 Two Technologies, Inc.
//***************************************************************************

//---------------------------------------------------------------------------
// Package
//---------------------------------------------------------------------------
package com.parkloyalty.lpr.scan.ui.xfprinter;

//---------------------------------------------------------------------------
// Imports
//---------------------------------------------------------------------------

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.twotechnologies.n5library.printer.ImageAlignment;
import com.twotechnologies.n5library.printer.ImageScale;
import com.twotechnologies.n5library.printer.PrtActionRequest;
import com.twotechnologies.n5library.printer.PrtContrastLevel;
import com.twotechnologies.n5library.printer.PrtGraphics;
import com.twotechnologies.n5library.printer.PrtSeekRequest;
import com.twotechnologies.n5library.printer.PrtTextStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//***************************************************************************

/**
 * Image Print Demo
 * Created by lcolavito on 3/2/2015.
 */
//***************************************************************************
public class ImageDemo
{
    //----------------------------------------------------------------------
    // Constants
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Storage
    //----------------------------------------------------------------------

    // completion listener
    private DemoDoneHandler doneHandler;
    private String mLabel= "";

    //----------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------

    //***********************************************************************


    //***********************************************************************
    /**
     * Output Text Demo to printer
     *
     * @param context
     *         execution context
     * @param activity
     *         reference to the main activity
     */
    //***********************************************************************
    void printBitmap(Context context, PrtContrastLevel eContrast, Bitmap  bitmap,int topSetFF,int bottomSetFF)
    {
        this.mLabel = mLabel;
        doneHandler = new DemoDoneHandler(context );
        try
        {
            // Reset parameters
            PrtActionRequest.resetPrinter();

            // Set contrast
            //PrtContrastLevel contrast = activity.getContrastSetting();
//            PrtActionRequest.setPrintContrast( eContrast );

            // clear elapsed timer
//            PrtActionRequest.resetElapsedTime();
            // print 12 row black line TOP SETFF 1= 6 row
//            PrtGraphics.printBlackLine(topSetFF);

            //
//            DemoSupport.addGraphicLine(4);

            PrtGraphics.printImage(bitmap,
                                    ImageScale.SCALE_ONE_TO_ONE,
                                    ImageAlignment.IMAGE_CENTER);


//            PrtGraphics.printGraphic()

            // add formfeed
//            PrtTextStream.formfeed();

//            PrtSeekRequest.forwardSeek(208)

//            PrtActionRequest.reverseFeed( 1 );

//            PrtActionRequest.forwardFeed(255)

            // flush data to printer
//            PrtTextStream.flush();

//            Toast.makeText(context, "flush and form feed", Toast.LENGTH_SHORT).show();
//            PrtActionRequest.forwardFeed(255);
//             BOTTOM SETFF 1= 6 row
//            PrtGraphics.printBlackLine(bottomSetFF);
            PrtSeekRequest.forwardSeek(bottomSetFF);

            // mark EOJ
            PrtActionRequest.markEOJ();
        }
        catch(Exception e )
        {
            Log.d( "N5Example", "print failed" );
        }

        // wait for completion and print marker
        doneHandler.setPrintMarker();

        // enable listener
        doneHandler.startListening();

//        Toast.makeText(context, "End printing", Toast.LENGTH_SHORT).show();
//        PrtActionRequest.forwardFeed(255);

    }





} // end class
// end file =================================================================
