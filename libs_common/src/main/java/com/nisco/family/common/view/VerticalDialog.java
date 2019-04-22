package com.nisco.family.common.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.nisco.family.common.R;

/**
 * Created by 李宝军 on 2018/7/12.
 */

public class VerticalDialog extends Dialog {
    public VerticalDialog(Context context) {
        super(context);
    }
    public VerticalDialog(Context context, int theme) {
        super(context, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.common_dialog_vertical_layout);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }
    @Override
    public void show() {
        super.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }
}
