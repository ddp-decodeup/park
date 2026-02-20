//***************************************************************************
// Demo Completion Handler - applies end of print marker
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.twotechnologies.n5library.printer.Fonts;
import com.twotechnologies.n5library.printer.PrtEOJListener;
import com.twotechnologies.n5library.printer.PrtFormatting;
import com.twotechnologies.n5library.printer.PrtStatus;
import com.twotechnologies.n5library.printer.PrtTextStream;

import java.io.IOException;

//***************************************************************************

/**
 * Demo Completion Listener
 *
 * Created by lcolavito on 3/2/2015.
 */
//***************************************************************************
public
class DemoDoneHandler extends PrtEOJListener
{
    //----------------------------------------------------------------------
    // Constants
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Storage
    //----------------------------------------------------------------------

    // print marker flag
    private boolean printMarker = false;

    //----------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------

    //***********************************************************************
    /** Enable listening for completion */
    //***********************************************************************
    public
    void setPrintMarker() { printMarker = true; }

    //----------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------

    //*******************************************************************
    /**
     * Registers a broadcast receiver for the selected key.
     * <p>
     * The onReceive handler must be overridden.
     *
     * @param context
     *         context in which to execute listener
     */
    //*******************************************************************
    public DemoDoneHandler(Context context ) { super( context ); }

    //**********************************************************************
    /** onReceive handler - prints marker */
    //**********************************************************************
    @Override
    public
    void onReceive( Context context, Intent intent )
    {
        // abort if not expected
        if( !printMarker ) { return; }

        // clear marker print
        printMarker = false;

        // stop listening
        stopListening();

        // load info
        createEndMarker( intent );
    }

    //***********************************************************************
    /**
     * Create and print the end of ticket marker showing elapsed time
     * and print head temperature
     *
     * @param intent
     *         PrtCompletion intent that prompted calling
     */
    //***********************************************************************
    private
    void createEndMarker( Intent intent )
    {
        try
        {
//            // set font
//            PrtFormatting.setFont( Fonts.COURIER_16_9_CPI );
//
//            // add thick line
//            DemoSupport.addGraphicLine( 4 );
//
//            // repeat count
//            PrtTextStream.write( "Ticket 1 of 1\n" );
//
//            // elapsed printing time
//            @SuppressLint("DefaultLocale")
//            String elapsedStr = String.format( "Printing Time: %5.3f sec\n", getElapsedTime( intent ) / 1000.0 );
//            PrtTextStream.write( elapsedStr );
//
//            // show temperature
//            String tempStr = String.format( "Temperature: %3.1f C\n",
//                                            PrtStatus.getTemperature() );
//            PrtTextStream.write( tempStr );
//
//            // add final line
//            DemoSupport.addGraphicLine( 4 );

            // new line
//            PrtTextStream.formfeed();

            // flush buffers
            PrtTextStream.flush();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

} // end class
// end file =================================================================
