package com.parkloyalty.lpr.scan.startprinterfull.functions;


import static com.starmicronics.starioextension.ICommandBuilder.PeripheralChannel;

import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starioextension.StarIoExt.Emulation;

public class CashDrawerFunctions {
    public static byte[] createData(Emulation emulation, PeripheralChannel channel) {
        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        builder.beginDocument();

        builder.appendPeripheral(channel);

        builder.endDocument();

        return builder.getCommands();
    }
}
