package com.nisco.family.common.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.lzy.imagepicker.bean.ImageItem;
import com.nisco.family.common.constant.CommonConstants;
import com.nisco.family.common.base.BaseApplication;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by cathy on 2016/11/18.
 */
public class OkHttpHelper {
    /**
     * 采用单例模式使用OkHttpClient
     */
    private static OkHttpHelper mOkHttpHelperInstance;
    private static OkHttpClient mClientInstance;
    private Handler mHandler;
    private Gson mGson;

    /**
     * 单例模式，私有构造函数，构造函数里面进行一些初始化
     */
    private OkHttpHelper() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        builder.readTimeout(30000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(3000, TimeUnit.MILLISECONDS);
        if (Build.VERSION.SDK_INT != 19) {
            builder.cache(BaseApplication.cache);
        }
        mClientInstance = builder.build();

        mGson = new Gson();

        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static OkHttpHelper getInstance() {

        if (mOkHttpHelperInstance == null) {

            synchronized (OkHttpHelper.class) {
                if (mOkHttpHelperInstance == null) {
                    mOkHttpHelperInstance = new OkHttpHelper();
                }
            }
        }
        return mOkHttpHelperInstance;
    }

    /**
     * 封装一个request方法，不管post或者get方法中都会用到
     */
    public void request(final Request request, final BaseCallback callback) {

        //在请求之前所做的事，比如弹出对话框等
        callback.onRequestBefore();

        mClientInstance.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //返回失败
                callbackFailure(request, callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
//                    //返回成功回调
                    String resString = response.body().string();
                    if (BaseApplication.getInstance().getCookie() == null && response.header("Set-Cookie") != null) {
                        String[] cookie = response.header("Set-Cookie").split(";");
                        BaseApplication.getInstance().setCookie(cookie[0]);
                    }
                    if (callback.mType == String.class) {
                        //如果n我们需要返回String类型
                        callbackSuccess(response, resString, callback);
                    } else {
                        //如果返回的是其他类型，则利用Gson去解析
                        try {
                            Object o = mGson.fromJson(resString, callback.mType);
                            callbackSuccess(response, o, callback);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            callbackError(response, callback, e);
                        }
                    }
                } else {
                    //返回错误
                    callbackError(response, callback, null);
                }
            }
        });
    }

    /**
     * 在主线程中执行的回调
     *
     * @param response
     * @param o
     * @param callback
     */
    private void callbackSuccess(final Response response, final Object o, final BaseCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, o);
            }
        });
    }

    /**
     * 在主线程中执行的回调
     *
     * @param response
     * @param callback
     * @param e
     */
    private void callbackError(final Response response, final BaseCallback callback, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response, response.code(), e);
            }
        });
    }

    /**
     * 在主线程中执行的回调
     *
     * @param request
     * @param callback
     * @param e
     */
    private void callbackFailure(final Request request, final BaseCallback callback, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }

    /**
     * 对外公开的get方法
     *
     * @param url
     * @param callback
     */
    public void JinJiuGet(String url, BaseCallback callback) {
        Request request = buildRequest(url, null, HttpMethodType.GET);
        request(request, callback);
    }

    /**
     * 对外公开的get方法
     *
     * @param url
     * @param callback
     */
    public void get(String url, BaseCallback callback) {
        String cookie = BaseApplication.getInstance().getCookie();
        Request otherRequest = new Request.Builder()
                .addHeader("Cookie", cookie == null ? "" : cookie)
                .header("Q-Type", "m")
                .header("X-Requested-With", "XMLHTTPREQUEST")
                .url(url)
                .build();
        request(otherRequest, callback);
    }

    public void erkGet(String url, BaseCallback callback) {
        Request otherRequest = new Request.Builder()
                .header("Q-Type", "m")
                .header("X-Requested-With", "XMLHTTPREQUEST")
                .url(url)
                .build();
        request(otherRequest, callback);
    }

    /**
     * 对外公开的post方法
     *
     * @param url
     * @param params
     * @param callback
     */
    public void post(String url, Map<String, String> params, BaseCallback callback) {
        Request request = buildRequest(url, params, HttpMethodType.POST);
        request(request, callback);
    }

    /**
     * 对外公开的post方法
     *
     * @param url
     * @param params
     * @param callback
     */
    public void videoPost(String url, JSONObject params, BaseCallback callback) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/json; charset=utf-8");
        Request loginRequest = new Request.Builder()
                .addHeader("AppKey", CommonConstants.AppKey)
                .addHeader("Nonce", CommonConstants.NONCE)
                .addHeader("CurTime", CommonConstants.CurTime)
                .addHeader("CheckSum", CheckSumBuilder.getCheckSum(CommonConstants.AppSecret, CommonConstants.NONCE, CommonConstants.CurTime))
                .post(RequestBody.create(MEDIA_TYPE_TEXT, params.toString()))
                .url(url)
                .build();
        request(loginRequest, callback);
    }

    /**
     * 对外公开的post方法
     *
     * @param url
     * @param params
     * @param callback
     */
    public void loginPost(String url, String params, BaseCallback callback) {
        // post 传参类型编码
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        Request loginRequest = new Request.Builder()
                .header("X-Requested-With", "XMLHTTPREQUEST")
                .post(RequestBody.create(MEDIA_TYPE_TEXT, params.getBytes()))
                .url(url)
                .build();
        request(loginRequest, callback);
    }

    public void picPost(String url, ArrayList<ImageItem> params, Map<String, String> params2, BaseCallback callback) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("image/png");
        MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, String> entity : params2.entrySet()) {
                builder.addFormDataPart(entity.getKey(), entity.getValue());
            }
        }
        for (ImageItem imageItem : params) {
            File file = new File(imageItem.path);
            if (file != null) {
                builder.addFormDataPart("strFilePath", file.getName(), RequestBody.create(MEDIA_TYPE_TEXT, file));
            }
        }
        //构建请求体
        RequestBody requestBody = builder.build();
        Request otherRequest = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        request(otherRequest, callback);
    }

    public void picPost(String url, ArrayList<ImageItem> params, BaseCallback callback) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("image/png");
        MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (ImageItem imageItem : params) {
            File file = new File(imageItem.path);
            if (file != null) {
                builder.addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_TEXT, file));
            }
        }
        //构建请求体
        RequestBody requestBody = builder.build();
        Request otherRequest = new Request.Builder()
                .post(requestBody)
//                .addHeader("charset", "utf-8")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "binary/octet-stream")
                .url(url)
                .build();
        request(otherRequest, callback);
    }

    /**
     * 对外公开的delete方法
     *
     * @param url
     * @param callback
     */
    public void delete(String url, BaseCallback callback) {
        String cookie = BaseApplication.getInstance().getCookie();
        Request deleteRequest = new Request.Builder()
                .addHeader("Cookie", cookie == null ? "" : cookie)
                .addHeader("Q-Type", "m")
                .addHeader("X-Requested-With", "XMLHTTPREQUEST")
                .delete()
                .url(url)
                .build();
        request(deleteRequest, callback);
    }

    /**
     * 构建请求对象
     *
     * @param url
     * @param params
     * @param type
     * @return
     */
    private Request buildRequest(String url, Map<String, String> params, HttpMethodType type) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (type == HttpMethodType.GET) {
            builder.get();
        } else if (type == HttpMethodType.POST) {
            builder.post(buildRequestBody(params));
        }
        return builder.build();
    }

    /**
     * 通过Map的键值对构建请求对象的body
     *
     * @param params
     * @return
     */
    private RequestBody buildRequestBody(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entity : params.entrySet()) {
                builder.add(entity.getKey(), entity.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 这个枚举用于指明是哪一种提交方式
     */
    enum HttpMethodType {
        GET,
        POST
    }

}
