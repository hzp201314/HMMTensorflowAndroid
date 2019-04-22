package com.nisco.family.common.utils;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cathy on 2016/11/18.
 */
public abstract class BaseCallback<T> {
    /**
     * type用于方便JSON的解析
     */
    public Type mType;

    /**
     * 把type转换成对应的类，这里不用看明白也行。
     *
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * 构造的时候获得type的class
     */
    public BaseCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * 请求之前调用
     */
    public abstract void onRequestBefore();

    /**
     * 请求失败调用（网络问题）
     *
     * @param request
     * @param e
     */
    public abstract void onFailure(Request request, Exception e);

    /**
     * 请求成功而且没有错误的时候调用
     *
     * @param response
     * @param t
     */
    public abstract void onSuccess(Response response, T t);

    /**
     * 请求成功但是有错误的时候调用，例如Gson解析错误等
     *
     * @param response
     * @param errorCode
     * @param e
     */
    public abstract void onError(Response response, int errorCode, Exception e);
}
