package com.nisco.family.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.nisco.family.common.constant.NiscoUrl;
import com.nisco.family.common.model.Content;
import com.nisco.family.common.view.x5WebView.X5WebView;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cathy on 2016/12/22.
 */
public class CommonUtil {
    private static String responseStr = "";

    //解析服务器字符串，获取content列表
    public static LinkedList<Content> formatData(Context context, String resString) {
        try {
            JSONObject responseObject = new JSONObject(resString);
            JSONObject statusObject = responseObject.getJSONObject("STATUS");
            LinkedList<Content> resultContents = new LinkedList<>();
            String status = statusObject.isNull("status") ? "" : statusObject.getString("status");
            if ("true".equalsIgnoreCase(status)) {
                Type listType = new TypeToken<LinkedList<Content>>() {
                }.getType();
                Gson gson = new Gson();
                String responseString = responseObject.getJSONArray("DATA").toString();
                if (responseString.equalsIgnoreCase("") || responseString == null) {
                    CustomToast.showToast(context, "暂无该分类信息", 1000);
                } else {
                    resultContents = gson.fromJson(responseString, listType);
                }
            }
            return resultContents;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void initContentView(final X5WebView mWebView, String url, String ua) {
        //启用支持javascript
        WebSettings webSettings = mWebView.getSettings();
        initWebSetting(webSettings, ua);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });
        mWebView.loadUrl(url);
    }


    public static void initWebSetting(WebSettings webSettings, String ua) {
        //缩放功能
        webSettings.setBuiltInZoomControls(true); // 显示放大缩小 controler
        webSettings.setUserAgentString(ua);
        webSettings.setSupportZoom(true); // 可以缩放
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);// 默认缩放模式
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setSupportMultipleWindows(false);
        //webSetting.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        //webSetting.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    public static void initImagePicker(int maxImgCount) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    /**
     * 方法描述：判断某一应用是否正在运行
     *
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /*
    * 获取当前程序的版本号
    */
    public static String getNowVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */

    public static void backgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public static class popupDismissListener implements PopupWindow.OnDismissListener {
        private Context context;

        public popupDismissListener(Context context) {
            this.context = context;
        }

        @Override
        public void onDismiss() {
            CommonUtil.backgroundAlpha((Activity) context, 1f);
        }
    }

    /**
     * list深拷贝
     *
     * @param src
     */
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
//        long begin =System.currentTimeMillis() ;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        out.flush();
        out.close();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        in.close();
//        System.out.println("FileOutputStream执行耗时: " + (System.currentTimeMillis() - begin));
        return dest;
    }

    public static void postMobileModelAccessCount(String modelNo, String userNo) {
        Map<String, String> param = new HashMap<>();
        if (userNo.equals("")) {
            param.put("modelNo", modelNo);
        } else {
            param.put("modelNo", modelNo);
            param.put("userNo", userNo);
        }
        OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();
        mHttpHelper.post(NiscoUrl.POST_MOBILE_MODEL_ACCESS_COUNT_URL, param, new BaseCallback<String>() {
            @Override
            public void onRequestBefore() {
            }

            @Override
            public void onFailure(Request request, Exception e) {
            }

            @Override
            public void onSuccess(Response response, String resString) {

            }

            @Override
            public void onError(Response response, int errorCode, Exception e) {
            }
        });
    }

}
