package com.parkloyalty.lpr.scan.ui.printer

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.widget.Toast

class UIHelper(var activity: Activity?) {
    var loadingDialog: ProgressDialog? = null
    fun showLoadingDialog(message: String?) {
//        if (activity != null) {
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    loadingDialog = new ProgressDialog(activity,R.style.ButtonAppearance);
//                    loadingDialog.setMessage(message);
//                    loadingDialog.show();
//                    TextView tv1 = (TextView) loadingDialog.findViewById(android.R.id.message);
//                    tv1.setTextAppearance(activity,R.style.ButtonAppearance);
//
//                }
//            });
//        }
    }

    fun showErrorMessage(message: String?) {
        //       if (activity != null) {
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    AlertDialog.Builder builder=new  AlertDialog.Builder(activity,R.style.ErrorButtonAppearance);
//                    builder.setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.dismiss();
//                            dismissLoadingDialog();
//                        }
//
//                    }).create();
//                    Dialog d= builder.show();
//                    TextView tv1 = (TextView) d.findViewById(android.R.id.message);
//                    tv1.setTextAppearance(activity, R.style.ErrorButtonAppearance);
//                }
//            });
        //       }
    }

    fun updateLoadingDialog(message: String?) {
        if (activity != null) {
            activity!!.runOnUiThread {
                //                    loadingDialog.setMessage(message);
            }
        }
    }

    fun isDialogActive(): Boolean {
        return if (loadingDialog != null) {
            loadingDialog!!.isShowing
        } else {
            false
        }
    }

    fun dismissLoadingDialog() {
//        if (activity != null && loadingDialog != null) {
//            loadingDialog.dismiss();
//        }
    }

    fun showErrorDialog(errorMessage: String?) {
//        try {
//            if (activity != null) {
//                AlertDialog.Builder(activity).setMessage(errorMessage).setTitle("Error")
//                    .setPositiveButton("OK") { dialog, id -> dialog.dismiss() }
//                    .create().show()
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
        try {
        Toast.makeText(activity,errorMessage,Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showErrorDialogOnGuiThread(errorMessage: String?) {
//        try {
//            if (activity != null) {
//                activity!!.runOnUiThread {
//                    AlertDialog.Builder(activity).setMessage(errorMessage).setTitle("Error")
//                        .setPositiveButton("OK") { dialog, id ->
//                            dialog.dismiss()
//                            dismissLoadingDialog()
//                        }.create().show()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        try {
            Toast.makeText(activity,errorMessage,Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}