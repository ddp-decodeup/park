package com.parkloyalty.lpr.scan.startprinterfull;

import static com.parkloyalty.lpr.scan.interfaces.Constants.FILE_NAME_FACSIMILE_PRINT_BITMAP;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.parkloyalty.lpr.scan.R;
import com.parkloyalty.lpr.scan.interfaces.Constants;
import com.parkloyalty.lpr.scan.interfaces.PrintInterface;
import com.parkloyalty.lpr.scan.startprinterfull.functions.PrinterFunctions;
import com.parkloyalty.lpr.scan.startprinterfull.localizereceipts.ILocalizeReceipts;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends ItemListFragment implements CommonAlertDialogFragment.Callback {

    private static final int PRINTER_SET_REQUEST_CODE = 1;

    private static final String PRINTER_LANG_SELECT_DIALOG           = "PrinterLanguageSelectDialog";

    private static final String BLACK_MARK_LANG_SELECT_DIALOG        = "BlackMarkLanguageSelectDialog";
    private static final String BLACK_MARK_PASTE_LANG_SELECT_DIALOG  = "BlackMarkPasteLanguageSelectDialog";

    private static final String PRINTER_LANG_SELECT_PAGE_MODE_DIALOG = "PrinterLanguageSelectPageModeDialog";

    private static final String PRINT_REDIRECTION_LANG_SELECT_DIALOG = "PrintRedirectionLanguageSelectDialog";

    private static final String MPOP_COMBINATION_LANG_SELECT_DIALOG  = "mPOPCombinationLanguageSelectDialog";

    private static final String SERIAL_NUMBER_DIALOG                 = "SerialNumberDialog";

    private static final String LICENSE_DIALOG                       = "LicenseDialog";

    private static final int BLUETOOTH_REQUEST_CODE = 1000;

//    private PrintInterface printCallBack = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
//        this.printCallBack = (com.parkloyalty.lpr.scan.interfaces.PrintInterface) getActivity();;
        updateList();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If you are using Android 12 and targetSdkVersion is 31 or later,
        // you have to request Bluetooth permission (Nearby devices permission) to use the Bluetooth printer.
        // https://developer.android.com/about/versions/12/features/bluetooth-permissions
        if (Build.VERSION_CODES.S <= Build.VERSION.SDK_INT) {
            requestBluetoothPermission();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.infoIcon) {
            LicenseDialogFragment dialog = LicenseDialogFragment.newInstance(LICENSE_DIALOG);
            dialog.show(getChildFragmentManager());
            return true;
//        }

//        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PRINTER_SET_REQUEST_CODE) {
            updateList();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        final Intent intent = new Intent(getActivity(), CommonActivity.class);

        switch (position) {
            case 1: {   // Tapped Destination Device row VINOD
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_printer_search);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Search Port");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_RELOAD_BUTTON, true);
                intent.putExtra(CommonActivity.BUNDLE_KEY_PRINTER_SETTING_INDEX, 0);    // Index of the backup printer

                startActivityForResult(intent, PRINTER_SET_REQUEST_CODE);
                break;
            }
            case 3: {   // Tapped Printer row (Sample)
                LanguageSelectDialogFragment dialog = LanguageSelectDialogFragment.newInstance(PRINTER_LANG_SELECT_DIALOG);
                dialog.show(getChildFragmentManager());
                break;
            }
            case 4: {   // Tapped Printer row (Black Mark)
                EnJpLanguageSelectDialogFragment dialog = EnJpLanguageSelectDialogFragment.newInstance(BLACK_MARK_LANG_SELECT_DIALOG);
                dialog.show(getChildFragmentManager());
                break;
            }
            case 5: {   // Tapped Printer row (Black Mark Paste)
                EnJpLanguageSelectDialogFragment dialog = EnJpLanguageSelectDialogFragment.newInstance(BLACK_MARK_PASTE_LANG_SELECT_DIALOG);
                dialog.show(getChildFragmentManager());
                break;
            }
            case 6: {   // Tapped Printer row (Page Mode)
                EnJpLanguageSelectDialogFragment dialog = EnJpLanguageSelectDialogFragment.newInstance(PRINTER_LANG_SELECT_PAGE_MODE_DIALOG);
                dialog.show(getChildFragmentManager());
                break;
            }
            case 7: {   // Tapped Printer row (Presenter)
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_presenter);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Presenter");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 8: {   // Tapped Printer row (LED)
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_led);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "LED");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 9: {   // Tapped Printer row (Print Re-Direction Sample)
                CombinationLanguageSelectDialogFragment dialog = CombinationLanguageSelectDialogFragment.newInstance(PRINT_REDIRECTION_LANG_SELECT_DIALOG);
                dialog.show(getChildFragmentManager());
                break;
            }
            case 10: {   // Tapped Printer row (Hold Print Sample)
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_hold_print);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Hold Print");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 11: {   // Tapped AutoSwitch Interface row
                final String[] items = {"StarIO Sample", "StarIoExtManager Sample"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select Sample")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int index) {
                                switch (index) {
                                    default:
                                    case 0:
                                        intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_auto_switch_interface);
                                        intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "AutoSwitch Interface");
                                        intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
                                        break;

                                    case 1:
                                        intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_auto_switch_interface_ext);
                                        intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "AutoSwitch Interface Ext");
                                        intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
                                        intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_RELOAD_BUTTON, true);
                                        break;
                                }

                                startActivity(intent);

                            }
                        })
                        .show();
                break;
            }
            case 13: {   // Tapped CashDrawer row
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_cash_drawer);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Cash Drawer");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 14: {   // Tapped Barcode Reader row
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_barcode_reader_ext);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Barcode Reader Ext");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_RELOAD_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 15: {   // Tapped Display row
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_display);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Display");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 16: {   // Tapped Melody Speaker row
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_melody_speaker);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Melody Speaker");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 18: {   // Tapped Combination row
                CombinationLanguageSelectDialogFragment dialog = CombinationLanguageSelectDialogFragment.newInstance(MPOP_COMBINATION_LANG_SELECT_DIALOG);
                dialog.show(getChildFragmentManager());
                break;
            }
            case 20: {   // Tapped API row
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_api);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "API");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 22: {   // Tapped Device Status row (Device Status/Firmware Information)
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_device_status);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Device Status");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_RELOAD_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 23: {   // Tapped Device Status row (Product Serial Number)
                SerialNumberDialogFragment dialog = SerialNumberDialogFragment.newInstance(SERIAL_NUMBER_DIALOG);
                dialog.show(getChildFragmentManager());
                break;
            }
            case 25: {   // Tapped Bluetooth Setting row
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_bluetooth_setting);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Bluetooth Setting");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_RELOAD_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 26: {   // Tapped USB Serial Number row
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_usb_serial_number_setting);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "USB Serial Number");
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);
                break;
            }
            case 28: {   // Tapped Library Version row
                CommonAlertDialogFragment dialog = CommonAlertDialogFragment.newInstance("");
                dialog.setTitle("Library Version");
                dialog.setMessage(
                        "StarIOPort3.1 version " + StarIOPort.getStarIOVersion() + "\n" +
                        StarIoExt.getDescription());
                dialog.setPositiveButton("OK");
                dialog.show(getChildFragmentManager());
                break;
            }
        }
    }

    @Override
    public void onDialogResult(String tag, Intent data) {
        boolean isCanceled = data.hasExtra(CommonAlertDialogFragment.LABEL_NEGATIVE);

        if (isCanceled) return;
        switch (tag) {
            case PRINTER_LANG_SELECT_DIALOG: {

                PrinterSettingManager settingManager = new PrinterSettingManager(getActivity());
                PrinterSettings       settings       = settingManager.getPrinterSettings();

                Toast.makeText(getActivity(), ""+ settings.getPortName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), ""+ settings.getPaperSize(), Toast.LENGTH_SHORT).show();

                int language = data.getIntExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, PrinterSettingConstant.LANGUAGE_ENGLISH);

                Intent intent = new Intent(getActivity(), CommonActivity.class);
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_printer);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Printer");
                intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, language);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);

                break;
            }
            case BLACK_MARK_LANG_SELECT_DIALOG: {
                int language = data.getIntExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, PrinterSettingConstant.LANGUAGE_ENGLISH);

                Intent intent = new Intent(getActivity(), CommonActivity.class);
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_blackmark);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Black Mark");
                intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, language);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);

                break;
            }
            case BLACK_MARK_PASTE_LANG_SELECT_DIALOG: {
                int language = data.getIntExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, PrinterSettingConstant.LANGUAGE_ENGLISH);

                Intent intent = new Intent(getActivity(), CommonActivity.class);
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_blackmark_paste);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Black Mark Paste");
                intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, language);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);

                break;
            }
            case PRINTER_LANG_SELECT_PAGE_MODE_DIALOG: {
                int language = data.getIntExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, PrinterSettingConstant.LANGUAGE_ENGLISH);

                Intent intent = new Intent(getActivity(), CommonActivity.class);
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_page_mode);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Page Mode");
                intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, language);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);

                break;
            }
            case PRINT_REDIRECTION_LANG_SELECT_DIALOG: {
                int language = data.getIntExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, PrinterSettingConstant.LANGUAGE_ENGLISH);

                Intent intent = new Intent(getActivity(), CommonActivity.class);
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_print_redirection);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Print Re-Direction");
                intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, language);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);

                break;
            }
            case MPOP_COMBINATION_LANG_SELECT_DIALOG: {
                int language = data.getIntExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, PrinterSettingConstant.LANGUAGE_ENGLISH);

                Intent intent = new Intent(getActivity(), CommonActivity.class);
                intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_combination);
                intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Combination");
                intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, language);
                intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);

                startActivity(intent);

                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BLUETOOTH_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth permissions are granted.
            } else {
                String text = "You have to allow \"Nearby devices\" to use the Bluetooth printer";
            }
        }
    }

    @RequiresApi(31)
    private void requestBluetoothPermission() {
        if (getContext().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED ||
                getContext().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                    },
                    BLUETOOTH_REQUEST_CODE
            );
        }
    }

    private void updateList() {
        adapter.clear();

        PrinterSettingManager settingManager = new PrinterSettingManager(getActivity());
        PrinterSettings       settings       = settingManager.getPrinterSettings();

        boolean isDeviceSelected     = true;
        int     modelIndex           = ModelCapability.MPOP;
        String  modelName            = "";
        boolean isBluetoothInterface = false;
        boolean isUsbInterface       = false;
        if (settings != null) {
            isDeviceSelected     = true;
            modelIndex           = settings.getModelIndex();
            modelName            = settings.getModelName();
            isBluetoothInterface = settings.getPortName().toUpperCase().startsWith("BT:");
            isUsbInterface       = settings.getPortName().toUpperCase().startsWith("USB:");
        }

        addTitle("Destination Device");
        addPrinterInfo(settingManager.getPrinterSettingsList());

        addTitle("Printer");
        addMenu("Sample",                     isDeviceSelected);
        addMenu("Black Mark Sample",          isDeviceSelected && ModelCapability.canUseBlackMark(modelIndex));
        addMenu("Black Mark Sample (Paste)",  isDeviceSelected && ModelCapability.canUseBlackMark(modelIndex));
        addMenu("Page Mode Sample",           isDeviceSelected && ModelCapability.canUsePageMode(modelIndex));
        addMenu("Presenter Sample",           isDeviceSelected && ModelCapability.canUsePresenter(modelIndex));
        addMenu("LED Sample",                 isDeviceSelected && ModelCapability.canUseLed(modelIndex));
        addMenu("Print Re-Direction Sample",  isDeviceSelected);
        addMenu("Hold Print Sample",          isDeviceSelected && ModelCapability.canUsePaperPresentStatus(modelIndex));
        addMenu("AutoSwitch Interface Sample",      isDeviceSelected && ModelCapability.canUseAutoSwitchInterface(modelIndex));

        addTitle("Peripheral");
        addMenu("Cash Drawer Sample",         isDeviceSelected && ModelCapability.canUseCashDrawer(modelIndex));
        addMenu("Barcode Reader Sample",      isDeviceSelected && ModelCapability.canUseBarcodeReader(modelIndex));
        addMenu("Display Sample",             isDeviceSelected && ModelCapability.canUseCustomerDisplay(modelIndex, modelName));
        addMenu("Melody Speaker Sample",      isDeviceSelected && ModelCapability.canUseMelodySpeaker(modelIndex));

        addTitle("Combination");
        addMenu("StarIoExtManager Sample",    isDeviceSelected && ModelCapability.canUseBarcodeReader(modelIndex));

        addTitle("API");
        addMenu("Sample",                     isDeviceSelected);

        addTitle("Device Status");
        addMenu("Sample",                     isDeviceSelected);
        addMenu("Product Serial Number",      isDeviceSelected && ModelCapability.canGetProductSerialNumber(modelIndex, modelName, isBluetoothInterface));

        addTitle("Interface");
        addMenu("Bluetooth Setting",          isDeviceSelected && isBluetoothInterface);
        addMenu("USB Serial Number",          isDeviceSelected && ModelCapability.settableUsbSerialNumberLength(modelIndex, modelName, isUsbInterface) != 0);

        addTitle("Appendix");
        addMenu("Library Version");
    }

    private void addPrinterInfo(List<PrinterSettings> settingsList) {
        if (settingsList.size() == 0) {
            List<TextInfo> mainTextList = new ArrayList<>();

//            mainTextList.add(new TextInfo("Unselected State", R.id.menuTextView, R.anim.blink, Color.RED));

            adapter.add(new ItemList(R.layout.list_main_row, mainTextList, ContextCompat.getColor(getActivity(), R.color.gray), true));
        }
        else {
            List<TextInfo> textList = new ArrayList<>();

            // Get a port name, MAC address and model name of the destination printer.
            String portName   = settingsList.get(0).getPortName();
            String macAddress = settingsList.get(0).getMacAddress();
            String modelName  = settingsList.get(0).getModelName();

            if (portName.startsWith(PrinterSettingConstant.IF_TYPE_ETHERNET) ||
                portName.startsWith(PrinterSettingConstant.IF_TYPE_BLUETOOTH)) {
                textList.add(new TextInfo(modelName, R.id.deviceTextView));
                if (macAddress.isEmpty()) {
                    textList.add(new TextInfo(portName,                           R.id.deviceDetailTextView));
                }
                else {
                    textList.add(new TextInfo(portName + " (" + macAddress + ")", R.id.deviceDetailTextView));
                }
            }
            else {  // USB interface
                textList.add(new TextInfo(modelName, R.id.deviceTextView));
                textList.add(new TextInfo(portName,  R.id.deviceDetailTextView));
            }

            adapter.add(new ItemList(R.layout.list_destination_device_row, textList, ContextCompat.getColor(getActivity(), R.color.gray), true));


//            Intent intentForPassingData = new Intent();
//            intentForPassingData.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, 0);
//
//            mCallbackTarget.onDialogResult(getArguments().getString(DIALOG_TAG), intentForPassingData);
//
//
//            int language = data.getIntExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, PrinterSettingConstant.LANGUAGE_ENGLISH);

//            Intent intent = new Intent(getActivity(), CommonActivity.class);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_printer);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Printer");
//            intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, 0);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
//
//            startActivity(intent);



//            final Intent intent = new Intent(getActivity(), CommonActivity.class);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_printer_search);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Search Port");
//            intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_RELOAD_BUTTON, true);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_PRINTER_SETTING_INDEX, 0);    // Index of the backup printer
////
//            startActivityForResult(intent, PRINTER_SET_REQUEST_CODE);


//            Intent intent = new Intent(getActivity(), CommonActivity.class);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_printer);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Printer");
//            intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, 0);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
//
//            startActivity(intent);

        }





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (settingsList!=null && settingsList.size() == 0) {
/*
            final Intent intent = new Intent(getActivity(), CommonActivity.class);
            intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_printer_search);
            intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Search Port");
            intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
            intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_RELOAD_BUTTON, true);
            intent.putExtra(CommonActivity.BUNDLE_KEY_PRINTER_SETTING_INDEX, 0);    // Index of the backup printer
            startActivityForResult(intent, PRINTER_SET_REQUEST_CODE);*/
                    SearchTask searchTask = new SearchTask();
                    searchTask.execute(PrinterSettingConstant.IF_TYPE_BLUETOOTH);
                }
            }
        }, 5000);


    }




    /**
     * Printer search task.
     */
    private class SearchTask extends AsyncTask<String, Void, Void> {
        private List<PortInfo> mPortList;

        SearchTask() {
            super();
        }

        @Override
        protected Void doInBackground(String... interfaceType) {
            try {
                mPortList = StarIOPort.searchPrinter(interfaceType[0], getActivity());
            }
            catch (StarIOPortException | SecurityException e) {
                mPortList = new ArrayList<>();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void doNotUse) {
            Toast.makeText(getActivity(),"vinod "+mPortList.size(),Toast.LENGTH_SHORT).show();

            for (PortInfo info : mPortList) {
                addItem(info);
            }
            if(mPortList!=null && mPortList.size()>0)
            {
//                printCallBack.onActionSuccess();
            }
        }
    }


    private int       mModelIndex = 19  ;
    private String    mPortName= "test";
    private String    mPortSettings = "Portable";
    private String    mMacAddress="";
    private String    mModelName="";

    private void addItem(PortInfo info) {
        List<TextInfo> textList = new ArrayList<>();
        List<ImgInfo>  imgList  = new ArrayList<>();

        String modelName;
        String portName;
        String macAddress;

        // --- Bluetooth ---
        // It can communication used device name(Ex.BT:Star Micronics) at bluetooth.
        // If android device has paired two same name device, can't choose destination target.
        // If used Mac Address(Ex. BT:00:12:3f:XX:XX:XX) at Bluetooth, can choose destination target.
        if (info.getPortName().startsWith(PrinterSettingConstant.IF_TYPE_BLUETOOTH)) {
            modelName  = info.getPortName().substring(PrinterSettingConstant.IF_TYPE_BLUETOOTH.length());
            portName   = PrinterSettingConstant.IF_TYPE_BLUETOOTH + info.getMacAddress();
            macAddress = info.getMacAddress();
        }
        else {
            modelName  = info.getModelName();
            portName   = info.getPortName();
            macAddress = info.getMacAddress();
        }

        textList.add(new TextInfo(modelName,  R.id.modelNameTextView));
        textList.add(new TextInfo(portName,   R.id.portNameTextView));

        if (   info.getPortName().startsWith(PrinterSettingConstant.IF_TYPE_ETHERNET)
                || info.getPortName().startsWith(PrinterSettingConstant.IF_TYPE_BLUETOOTH)) {
            textList.add(new TextInfo("(" + macAddress + ")", R.id.macAddressTextView));
        }

        PrinterSettingManager settingManager = new PrinterSettingManager(getActivity());
        PrinterSettings       settings       = settingManager.getPrinterSettings();

        if (settings != null && settings.getPortName().equals(portName)) {
            imgList.add(new ImgInfo(R.drawable.ic_app_name, R.id.checkedIconImageView));
        }
        else {
            imgList.add(new ImgInfo(R.drawable.ic_app_name, R.id.checkedIconImageView));
        }

        adapter.add(new ItemList(R.layout.list_printer_info_row, textList, imgList));



//        switchSelectedRow(0);
//
//        mMacAddress = "";


        List<TextInfo> portInfoList = textList;

//        TextView modelNameTextView = clickedItemView.findViewById(R.id.modelNameTextView);
//        String   modelName         = portInfoList.get(0).;
//        int      model             = ModelCapability.getModel(modelName);

        for (TextInfo portInfo : portInfoList) {
            switch (portInfo.getTextResourceID()) {
                case R.id.modelNameTextView:
                    mModelName = portInfo.getText();
                    break;
                case R.id.portNameTextView:
                    mPortName = portInfo.getText();
                    break;
                case R.id.macAddressTextView:
                    mMacAddress = portInfo.getText();
                    if (mMacAddress.startsWith("(") && mMacAddress.endsWith(")")) {
                        mMacAddress = mMacAddress.substring(1, mMacAddress.length() - 1);
                    }
                    break;
            }
        }
        int      model             = ModelCapability.getModel(mModelName);
        if (model == ModelCapability.NONE) {
//            ModelSelectDialogFragment dialog = ModelSelectDialogFragment.newInstance(MODEL_SELECT_DIALOG_0);
//            dialog.show(getChildFragmentManager());
        }
        else {
//            ModelConfirmDialogFragment dialog = ModelConfirmDialogFragment.newInstance(MODEL_CONFIRM_DIALOG, model);
//            dialog.show(getChildFragmentManager());
            registerPrinter();
        }
    }


    /**
     * Register printer information to SharedPreference.
     */
    private void registerPrinter() {
        PrinterSettingManager settingManager = new PrinterSettingManager(getActivity());

//        Toast.makeText(getActivity(),0+" mPrinterSettingIndex",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(),mModelIndex+" mModelIndex",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(),mPortName+"  =mPortName",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(),mPortSettings+"  =mPortSettings",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(),mMacAddress+"  =mMacAddress",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(),mModelName+"  =mModelName",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(),mPortName+"  =PortName",Toast.LENGTH_SHORT).show();
        settingManager.storePrinterSettings(
                0,
                new PrinterSettings(mModelIndex, mPortName, mPortSettings, mMacAddress, mModelName,
                        true, 576)
        );

        PrinterSettings       settings       = settingManager.getPrinterSettings();
        Toast.makeText(getActivity(), "save settings", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "save settings"+settings.getPortName(), Toast.LENGTH_SHORT).show();
        if(!mPortName.isEmpty())
        {


//            Intent intent = new Intent(getActivity(), CommonActivity.class);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_ACTIVITY_LAYOUT, R.layout.activity_printer);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_TOOLBAR_TITLE, "Printer");
//            intent.putExtra(CommonActivity.BUNDLE_KEY_LANGUAGE, 0);
//            intent.putExtra(CommonActivity.BUNDLE_KEY_SHOW_HOME_BUTTON, true);
//
//            startActivity(intent);

            File myDir = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath(),
                    Constants.FILE_NAME + Constants.CAMERA
            );

            String fname = "20148805_"+FILE_NAME_FACSIMILE_PRINT_BITMAP+".jpg"; //"print_bitmap.jpg";
            imgFilePath = new File(myDir, fname);

            if(imgFilePath!=null && imgFilePath.exists()) {
                sendPrinterBitmap(imgFilePath, getActivity(), mFrom, mLabel);
            }


        }
    }




    private Bitmap mBitmap;
    public static File imgFilePath;
    public static String mFrom = "";
    public static String mLabel = "";
    void sendPrinterBitmap(File imgFilePath, Context context, String mFrom, String mLabel)
    {

        Uri imgPath  = Uri.fromFile((imgFilePath.getAbsoluteFile()));
        Bitmap myBitmap = null;
        try {
//            myBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgPath);
            mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgPath);

            print(8);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void print(int selectedIndex) {
//        mProgressDialog.show();

        byte[] commands;

        PrinterSettingManager settingManager = new PrinterSettingManager(getActivity());
        PrinterSettings       settings       = settingManager.getPrinterSettings();

        StarIoExt.Emulation emulation = ModelCapability.getEmulation(settings.getModelIndex());
        int paperSize = settings.getPaperSize();

//        ILocalizeReceipts localizeReceipts = ILocalizeReceipts.createLocalizeReceipts(mLanguage, paperSize);

        switch (selectedIndex) {
            default:
            case 8:
                if (mBitmap != null) {
                    commands = PrinterFunctions.createRasterData(emulation, mBitmap, paperSize, true);
                }
                else {
                    commands = new byte[0];
                }
                break;
        }


        Communication.sendCommands(this, commands, settings.getPortName(), settings.getPortSettings(), 10000, 30000, getActivity(), mCallback);     // 10000mS!!!
    }

    private final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(Communication.CommunicationResult communicationResult) {
//            if (!mIsForeground) {
//                return;
//            }

//            if (mProgressDialog != null) {
//                mProgressDialog.dismiss();
//            }
//            printCallBack.onActionSuccess();

            CommonAlertDialogFragment dialog = CommonAlertDialogFragment.newInstance("CommResultDialog");
            dialog.setTitle("Communication Result");
            dialog.setMessage(Communication.getCommunicationResultMessage(communicationResult));
            dialog.setPositiveButton("OK");
            dialog.show(getChildFragmentManager());
        }
    };

}
