package com.nisco.family.common.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SharedPreferences工具类，可以保存object对象
 * <p>
 * 存储时以object存储到本地，获取时返回的也是object对象，需要自己进行强制转换
 * <p>
 * 也就是说，存的人和取的人要是同一个人才知道取出来的东西到底是个啥 ^_^
 *
 * @version: 1.0
 */

public class SharedPreferenceUtil {
    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     *
     * @param object 待加密的转换为String的对象
     * @return String   加密后的String
     */
    private static String Object2String(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        objectOutputStream.close();
        return string;
    }

    /**
     * 使用Base64解密String，返回Object对象
     *
     * @param objectString 待解密的String
     * @return object      解密后的object
     */
    private static Object String2Object(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 使用SharedPreference保存对象
     *
     * @param fileKey    储存文件的key
     * @param key        储存对象的key
     * @param saveObject 储存的对象
     */
    public static void save(String fileKey, String key, Object saveObject) throws IOException {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String string = Object2String(saveObject);
        editor.putString(key, string);
        editor.commit();
    }

    /**
     * 获取SharedPreference保存的对象
     *
     * @param fileKey 储存文件的key
     * @param key     储存对象的key
     * @return object 返回根据key得到的对象
     */
    public static Object get(String fileKey, String key) {
        SharedPreferences sharedPreferences = Utils.getContext().getApplicationContext().getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, null);
        if (string != null) {
            Object object = String2Object(string);
            return object;
        } else {
            return null;
        }
    }

    /**
     * 获取保存的string
     *
     * @param fileKey
     * @param key
     * @return
     */
    public static String getStr(String fileKey, String key) {
        SharedPreferences sharedPreferences = Utils.getContext().getApplicationContext().getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        String strVal = sharedPreferences.getString(key, null);
        if (strVal != null) {
            return strVal;
        } else {
            return "";
        }
    }

    /**
     * 保存string
     *
     * @param fileKey
     * @param key
     * @param saveStr
     */
    public static void saveStr(String fileKey, String key, String saveStr) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, saveStr);
        editor.commit();
    }

    /**
     * 获取保存的boolean
     *
     * @param fileKey
     * @param key
     * @return
     */
    public static Boolean getBoolean(String fileKey, String key) {
        SharedPreferences sharedPreferences = Utils.getContext().getApplicationContext().getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        Boolean booleanVal = sharedPreferences.getBoolean(key, true);
        if (booleanVal != true) {
            return booleanVal;
        } else {
            return true;
        }
    }

    /**
     * 保存boolean
     *
     * @param fileKey
     * @param key
     * @param saveBoolean
     */
    public static void saveBoolean(String fileKey, String key, Boolean saveBoolean) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, saveBoolean);
        editor.commit();
    }

    /**
     * 清除SP
     * @param fileKey
     */
    public static void clear(String fileKey){
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }
}