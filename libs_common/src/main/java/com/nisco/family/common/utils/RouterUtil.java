package com.nisco.family.common.utils;

/**
 * @author : cathy
 * @package : com.nisco.family.common.utils
 * @time : 2019/03/08
 * @desc : 统一配置跳转类
 * @version: 1.0
 */

public class RouterUtil {

    //首页
    public static final String Home_Fragment = "/home/fragment/HomeFragment";
    //办公
    public static final String Office_Fragment = "/home/fragment/HomeOfficeFragment";
    //服务
    public static final String EService_Fragment = "/home/fragment/HomeEServiceFragment";
    //我的
    public static final String Me_Fragment = "/mine/fragment/MineMeFragment";

    //登录
    public static final String Login_Activity = "/home/activity/auth/HomeLoginActivity";

    //mnist模块
    public static final String Mnist_Main_Activity = "/mnist/activity/MainActivity";
    public static final String Mnist2_Main_Activity = "/mnist2/activity/MainActivity";

    //tensorflow_demo模块
    public static final String TF_Classifier_Activity = "/tensorflow/activity/ClassifierActivity";
    public static final String TF_Detector_Activity = "/tensorflow/activity/DetectorActivity";
    public static final String TF_Stylize_Activity = "/tensorflow/activity/StylizeActivity";
    public static final String TF_Speech_Activity = "/tensorflow/activity/SpeechActivity";
    public static final String TF_Static_Detector_Activity="/tensorflow/activity/StaticDetectorActivity";


    //staticdetector_tflite模块
    public static final String TFlite_Static_Detector_Activity="/tensorflowlite/activity/staticDetectorActivity";
    //detectortflite模块
    public static final String TFlite_Detector_Activity="/tensorflowlite2/activity/TFDetectorActivity";

    //testtflite模块
    public static final String TestTFlite_Main_Activity="/testtflite/activity/MainActivity";

}
