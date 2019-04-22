package com.nisco.family.common.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.nisco.family.common.constant.CommonConstants;
import com.nisco.family.common.R;
import com.nisco.family.common.model.User;
import com.nisco.family.common.callback.PermissionListener;
import com.nisco.family.common.utils.CommonUtil;
import com.nisco.family.common.utils.LogUtils;
import com.nisco.family.common.utils.SharedPreferenceUtil;
import com.nisco.family.common.utils.ViewManager;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected BaseActivity context;

    protected PermissionListener mListener;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 隐藏标题栏
//        //如果定义的BaseActivity是继承android.support.v7.app.AppCompatActivity，需要通过以下方法进行设置
//        if (getSupportActionBar() != null)
//            getSupportActionBar().hide();
//        //如果定义的BaseActivity是继承android.app.Activity或者android.support.v4.app.FragmentActivity，需要通过以下方法进行设置
////        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 沉浸效果
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        context = this;
        ViewManager.getInstance().addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i(getActivityName(), " onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.i(getActivityName(), " onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(getActivityName(), " onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i(getActivityName(), " onStop()");
    }

    @Override
    protected void onDestroy() {
        ViewManager.getInstance().finishActivity(this);
        super.onDestroy();
    }

    /**
     * 清理缓存数据android4.0前调用
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtils.i(getActivityName(), " onTrimMemory()");
    }

    public abstract String getActivityName();

    /**
     * 界面导航按钮的返回事件
     *
     * @param view
     */
    public void back(View view) {
        LogUtils.i(getActivityName(), " back()");
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 点击手机上的返回键，返回上一层
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            ViewManager.getInstance().finishActivity(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击屏幕后收起键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (CommonUtil.isShouldHideInput(v, ev)) {

                @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 切换界面带有返回状态
     *
     * @param packageContext
     * @param cls
     * @param bundle
     */
    public void pageJumpResultActivity(Context packageContext, Class<?> cls,
                                       Bundle bundle) {
        Intent intent = new Intent(packageContext, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

//    /**
//     * 拨打电话（直接拨打电话）
//     *
//     * @param phoneNum 电话号码
//     */
//    public void callPhone(String phoneNum) {
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
//        } else {
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            Uri data = Uri.parse("tel:" + phoneNum);
//            intent.setData(data);
//            startActivity(intent);
//        }
//    }

    /**
     * 检查和处理运行时权限，并将用户授权的结果通过PermissionListener进行回调。
     *
     * @param permissions 要检查和处理的运行时权限数组
     * @param listener    用于接收授权结果的监听器
     */
    protected void handlePermissions(ArrayList<String> permissions, PermissionListener listener) {
        if (permissions == null || context == null) {
            return;
        }
        mListener = listener;
        ArrayList<String> requestPermissionList = new ArrayList<String>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permission);
            }
        }
        if (!requestPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(context, (String[]) requestPermissionList.toArray(), 1);
        } else {
            listener.onGranted();
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 判断是否是第一次登录
     * @return
     */
    public boolean isFirstLogin() {
        User user = (User) SharedPreferenceUtil.get(CommonConstants.USERINFO_FILE_NAME, CommonConstants.USERINFO_KEY_NAME);
        if (null != user && !user.isFirstIn()) {
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    ArrayList<String> deniedPermissions = new ArrayList<String>();
                    for (int i : grantResults) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                } else {
                }
                break;
            default:
                break;
        }
    }

}
