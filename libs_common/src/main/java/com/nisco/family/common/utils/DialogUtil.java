package com.nisco.family.common.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Liuys on 2016/7/8
 */
public class DialogUtil {
    private Context context;
    private ProgressDialog progressDialog;

    public DialogUtil(Context context) {
        this.context = context;
    }

    /**
     * 显示进度对话框
     */
    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
