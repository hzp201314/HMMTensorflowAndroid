package com.nisco.family.common.constant;

/**
 * Created by 李宝军 on 2018/7/23.
 */

public class CommonConstants {
    public static final String USERID = "002180";

    public static final int REFRESH_BEFORE_TIME = 2000;

    public static final int IMAGE_ITEM_ADD = -1;

    public static final int REQUEST_CODE_SELECT = 100;

    public static final String AppKey = "3481300230564745b8664bfa62920c1e";

    public static final String AppSecret = "25b40fd2af1546c79f4a425f6338f916";

    public static final String NONCE = "14709563209567382910756483920";

    public static final String CurTime = String.valueOf(System.currentTimeMillis());

    // 用户信息保存文件名
    public static final String USERINFO_FILE_NAME = "userinfofilename";
    // 保存用户信息键名
    public static final String USERINFO_KEY_NAME = "user";



    public static int sequence = 1;

    public static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";

    public static final String LOGIN_RESULT_SHAREPREFERENCE = "loginResult";

    //个人信息
    public static final int MY_INFO = 4;
    //startActivityForResult登录返回验证码
    public static final int MEFRAGMENT = 10;
    public static final int MNIST = 11;
    //模块列表
    public static final String[] ModelList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};


    //加载数据
    public static final int NETWORK_ERROR = 0;
    public static final int ERROR = 1;
    public static final int EMPTY = 2;
}
