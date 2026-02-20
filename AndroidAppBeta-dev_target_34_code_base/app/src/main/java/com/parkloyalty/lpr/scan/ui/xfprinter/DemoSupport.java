//***************************************************************************
// Common methods used by print demos
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

import com.twotechnologies.n5library.printer.Fonts;
import com.twotechnologies.n5library.printer.PrtContrastLevel;
import com.twotechnologies.n5library.printer.PrtFormatting;
import com.twotechnologies.n5library.printer.PrtGraphics;
import com.twotechnologies.n5library.printer.PrtIdentity;
import com.twotechnologies.n5library.printer.PrtTextStream;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

//***************************************************************************

/**
 * Print Demo Supporting Methods Class
 *
 * Created by lcolavito on 3/2/2015.
 */
//***************************************************************************
public
class DemoSupport
{
    //----------------------------------------------------------------------
    // Constants
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Storage
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------

    //***********************************************************************
    /**
     * Create Demo Ticket header
     *
     * @param contrast
     *         current contrast level, shown in header text
     *
     * @throws IOException
     */
    //***********************************************************************
    static
    void demoHeader( PrtContrastLevel contrast ) throws IOException
    {
        PrtFormatting.setFont( Fonts.SAN_SERIF_10_7_CPI );
        PrtTextStream.write( "*****************************\n" );

        PrtTextStream.write( "Font:\t\tSans Serif 10.7CPI\n" );

        String contrastLine = String.format( "Contrast:\t%d\n",
                                             contrast.toInt() );

        PrtTextStream.write(contrastLine );

        String fwId = PrtIdentity.getIdentity();
        fwId = fwId.substring( 0, fwId.indexOf( ')' ) + 1 );
        String fwLine = String.format( "Firmware:\t%s\n", fwId );
        PrtTextStream.write( fwLine );

        DateFormat dateFormat = new SimpleDateFormat( "MM/dd/yy HH:mm a",
                                                      Locale.US );
        String sDate = dateFormat.format( new Date() );
        String dateLine = String.format( "Date:\t\t%s\n", sDate );
        PrtTextStream.write( dateLine );

        PrtTextStream.write( "*****************************\n" );
        PrtTextStream.newline();
    }

    //*******************************************************************
    /**
     * add a graphic line
     *
     * @param rows
     *         number of rows for line thickness
     */
    //*******************************************************************
    static
    void addGraphicLine( int rows )
    {
        int cols = 576;
        byte[] line = new byte[rows * cols / 8];
        Arrays.fill( line, (byte) 0xff );
        PrtGraphics.printGraphic( rows, cols, line );
    }

    //***********************************************************************
    /**
     * Create and print the demo title using the usual format
     *
     * @param text
     *         text for ticket title
     */
    //***********************************************************************
    static
    void demoTitle( String text ) throws IOException
    {
        PrtFormatting.setFont( Fonts.SAN_SERIF_5_5_CPI );
        PrtTextStream.write( text );
        PrtTextStream.newline();

    }
} // end class
// end file =================================================================
