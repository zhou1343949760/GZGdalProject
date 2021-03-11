package com.hzgzsoft.gzgdallib.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context


object AlertDialogUtil {

    private var progressDialog: ProgressDialog? = null

    /**
     * AlertDialog
     * @param context 注意:必须是Activity的Context,不能是GZApp.getContext
     * @param title 标题
     * @param message  文本
     */
    fun showAlertDialog(context: Context, title: String, message: String) {
        (context as Activity).runOnUiThread {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("确定") { _, _ ->

                    }
                    .show()
        }
    }


    fun showProgressDialog(context: Context, message: String) {
        (context as Activity).runOnUiThread {
            //使用重新登录功能后,之前的progressDialog不属于之后的activity
            //                if (progressDialog == null) {
            //                    progressDialog = new ProgressDialog(context);
            //                } else {
            //                    if (progressDialog.isShowing()) {
            //                        progressDialog.dismiss();
            //                    }
            //                }
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            progressDialog = ProgressDialog(context)
            progressDialog!!.setMessage(message)
            progressDialog!!.setTitle("请稍候")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }
    }

    fun cacelProgressDialog(context: Context) {
        (context as Activity).runOnUiThread {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }
    }

    fun showAlertDialogNoBackGround(context: Context, title: String, message: String) {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
        AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("确定") { _, _ ->

                }
                .show()
    }


}