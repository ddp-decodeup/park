package com.parkloyalty.lpr.scan.startprinterfull;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Toast;

import com.parkloyalty.lpr.scan.BuildConfig;
import com.parkloyalty.lpr.scan.R;
import com.parkloyalty.lpr.scan.interfaces.Constants;
import com.parkloyalty.lpr.scan.interfaces.PrintInterface;
import com.parkloyalty.lpr.scan.startprinterfull.functions.PrinterFunctions;
import com.parkloyalty.lpr.scan.util.AppUtils;
import com.parkloyalty.lpr.scan.util.LogUtil;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.StarIoExt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SimplifiableIfStatement")
public class StarPrinterActivity extends CommonActivity {

    private PrintInterface printCallBack = null;
    private Bitmap mBitmap;
    public  File imgFilePath;
    public String mFrom = "";
    public String mLabel = "";
    private int       mModelIndex = 19  ;
    private String    mPortName= "test";
    private String    mPortSettings = "Portable";
    private String    mMacAddress="";
    private String    mModelName="";
    private boolean isOnCreate = true;
    private String isErrorUploading = "";
    private  int width = 0;
    private int height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.printCallBack = (com.parkloyalty.lpr.scan.interfaces.PrintInterface) this;
        isOnCreate = true;
        setScreenResolution();
//        updateList();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {                 // Do not close StarPrinterActivity.
            return super.onKeyDown(keyCode, event);
        }
        else {
            return false;
        }
    }

    public void printDailySummery(File imgFilePath , Context context, String mFrom, String mLabel,
                                  String mTicketNumber) {
      this.imgFilePath = imgFilePath;
      this.mFrom = mFrom;
      this.mLabel = mLabel;
        isOnCreate = false;
        updateList();

        sendPrinterBitmap(imgFilePath, StarPrinterActivity.this, mFrom, mLabel);
    }
    public void mPrintFacsimileImage(File imgFilePath , Context context, String mFrom, String mLabel,
                                  String mTicketNumber,String mAmount,String state,String lprNumber
            ,String mErrorUploading,StringBuilder printerCommand) {
      this.imgFilePath = imgFilePath;
      this.mFrom = mFrom;
      this.mLabel = mLabel;
      this.isErrorUploading = mErrorUploading;
        isOnCreate = false;
        updateList();
        LogUtil.printLog("label", mLabel);

        sendPrinterBitmap(imgFilePath, StarPrinterActivity.this, mFrom, mLabel);
//        sendPrinterBitmap(imgFilePath, StarPrinterActivity.this, mFrom, "Cancel");
    }
    public void mPrintDownloadFacsimileImage(File imgFilePath , Context context, String mFrom, String mLabel,
                                  String mTicketNumber,String mAmount,String state,String lprNumber
            ,String mErrorUploading) {
      this.imgFilePath = imgFilePath;
      this.mFrom = mFrom;
      this.mLabel = mLabel;
      this.isErrorUploading = mErrorUploading;
        isOnCreate = false;
        sendPrinterBitmap(imgFilePath, StarPrinterActivity.this, mFrom, mLabel);
        updateList();
        LogUtil.printLog("label", mLabel);

//        sendPrinterBitmap(imgFilePath, StarPrinterActivity.this, mFrom, "Cancel");
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateList() {

        PrinterSettingManager settingManager = new PrinterSettingManager(this);
        addPrinterInfo(settingManager.getPrinterSettingsList());
    }

    private void addPrinterInfo(List<PrinterSettings> settingsList) {
//        if (settingsList.size() == 0) {
            List<TextInfo> mainTextList = new ArrayList<>();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    if (settingsList!=null && settingsList.size() == 0) {
                        SearchTask searchTask = new SearchTask();
                        searchTask.execute(PrinterSettingConstant.IF_TYPE_BLUETOOTH);
//                    }
                }
            }, 500);

//        } else {
//            List<TextInfo> textList = new ArrayList<>();
//
//            // Get a port name, MAC address and model name of the destination printer.
//            String portName = settingsList.get(0).getPortName();
//            String macAddress = settingsList.get(0).getMacAddress();
//            String modelName = settingsList.get(0).getModelName();
//
//            if (portName.startsWith(PrinterSettingConstant.IF_TYPE_ETHERNET) ||
//                    portName.startsWith(PrinterSettingConstant.IF_TYPE_BLUETOOTH)) {
//                textList.add(new TextInfo(modelName, R.id.deviceTextView));
//                if (macAddress.isEmpty()) {
//                    textList.add(new TextInfo(portName, R.id.deviceDetailTextView));
//                } else {
//                    textList.add(new TextInfo(portName + " (" + macAddress + ")", R.id.deviceDetailTextView));
//                }
//            } else {  // USB interface
//                textList.add(new TextInfo(modelName, R.id.deviceTextView));
//                textList.add(new TextInfo(portName, R.id.deviceDetailTextView));
//            }
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (settingsList!=null && settingsList.size() == 0) {
//                        SearchTask searchTask = new SearchTask();
//                        searchTask.execute(PrinterSettingConstant.IF_TYPE_BLUETOOTH);
//                    }else{
//                        if(imgFilePath!=null && imgFilePath.exists()) {
//                            sendPrinterBitmap(imgFilePath, StarPrinterActivity.this, mFrom, mLabel);
//                        }
//                    }
//                }
//            }, 500);
//
//        }
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
                mPortList = StarIOPort.searchPrinter(interfaceType[0], StarPrinterActivity.this);
            }
            catch (StarIOPortException | SecurityException e) {
                mPortList = new ArrayList<>();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void doNotUse) {
            if(mPortList!=null && mPortList.size()==0&& !isOnCreate)
            {
                Toast.makeText(StarPrinterActivity.this,getString(R.string.star_printer_error),Toast.LENGTH_SHORT).show();
                printCallBack.onActionSuccess(isErrorUploading);
            }else{
                for (PortInfo info : mPortList) {
                    addItem(info);
                }
            }
        }
    }

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

        PrinterSettingManager settingManager = new PrinterSettingManager(StarPrinterActivity.this);
        PrinterSettings       settings       = settingManager.getPrinterSettings();

        if (settings != null && settings.getPortName().equals(portName)) {
            imgList.add(new ImgInfo(R.drawable.ic_app_name, R.id.checkedIconImageView));
        }
        else {
            imgList.add(new ImgInfo(R.drawable.ic_app_name, R.id.checkedIconImageView));
        }

        List<TextInfo> portInfoList = textList;

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
        PrinterSettingManager settingManager = new PrinterSettingManager(StarPrinterActivity.this);

        settingManager.storePrinterSettings(
                0,
                new PrinterSettings(mModelIndex, mPortName, mPortSettings, mMacAddress, mModelName,
                        false, PrinterSettingConstant.PAPER_SIZE_THREE_INCH)
        );
//        Toast.makeText(this,mPortName+" "+imgFilePath.exists(),Toast.LENGTH_SHORT).show();

        if(!isOnCreate) {
            if (!mPortName.isEmpty()) {
                if (imgFilePath != null && imgFilePath.exists()) {
                    sendPrinterBitmap(imgFilePath, StarPrinterActivity.this, mFrom, mLabel);
                }
            } else {
                Toast.makeText(this,getString(R.string.star_printer_error),Toast.LENGTH_SHORT).show();
                printCallBack.onActionSuccess(isErrorUploading);
            }
        }
    }


    private Bitmap drawTextToBitmap(Bitmap bitmap,  String mText) {
        try {
            Bitmap.Config bitmapConfig =
                    bitmap.getConfig();
            if(bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);



            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(0, 0, 0));
            // text size in pixels
            paint.setTextSize((int) (10 * 2));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

//            paint.getTypeface(context.getResources().getFont(R.font.sf_pro_text_semibold));
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    paint.setTypeface(Typeface.create(getResources().getFont(R.font.sf_pro_text_semibold), Typeface.NORMAL));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth());
            if(mText.equalsIgnoreCase("R")) {
                canvas.drawText(mText, x - 190, printLabelHeight(bitmap.getHeight()), paint);
            }else{
                canvas.drawText(mText, x - 200, printLabelHeight(bitmap.getHeight()), paint);
//                canvas.drawText(mText, x - 200, AppUtils.printLabelHeight().toFloat(), paint);
            }


            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }

    }



    private int printLabelHeight(int height) {
        try {
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD)){
                return (int)(height*0.16);
           }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA)) {
                return (int)(height*0.13);
           } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA)) {
                return (int)(height*0.29);
           } else {
                return (int)(height*0.15);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 130;
    }

    void sendPrinterBitmap(File imgFilePath, Context context, String mFrom, String mLabel)
    {

        Uri imgPath  = Uri.fromFile((imgFilePath.getAbsoluteFile()));
        Bitmap myBitmap = null;
        try {
//            mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
            myBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
            if(mLabel!=null && !mLabel.isEmpty())
            {
                mBitmap = drawTextToBitmap(myBitmap, mLabel);
            }
            else{
                mBitmap = myBitmap;
            }

            print(8);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void print(int selectedIndex) {
//        mProgressDialog.show();

        try {
            byte[] commands;

            PrinterSettingManager settingManager = new PrinterSettingManager(StarPrinterActivity.this);
            PrinterSettings       settings       = settingManager.getPrinterSettings();

            if(settings!=null) {
                StarIoExt.Emulation emulation = ModelCapability.getEmulation(settings.getModelIndex());
                int paperSize = settings.getPaperSize();

//        ILocalizeReceipts localizeReceipts = ILocalizeReceipts.createLocalizeReceipts(mLanguage, paperSize);

                switch (selectedIndex) {
                    default:
                    case 8:
                        if (mBitmap != null) {
                            commands = PrinterFunctions.createRasterData(emulation, mBitmap, paperSize, true);
                        } else {
                            commands = new byte[0];
                        }
                        break;
                }


                Communication.sendCommands(this, commands, settings.getPortName(), settings.getPortSettings(), 10000, 30000, StarPrinterActivity.this, mCallback);     // 10000mS!!!
            }
            } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            printCallBack.onActionSuccess(isErrorUploading);

//            Toast.makeText(StarPrinterActivity.this, "Print bitmap success", Toast.LENGTH_SHORT).show();
        }
    };

    private void setScreenResolution()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         height = displayMetrics.heightPixels;
//         width = displayMetrics.widthPixels;
//        double topSpace = width*0.22;
    }

}
