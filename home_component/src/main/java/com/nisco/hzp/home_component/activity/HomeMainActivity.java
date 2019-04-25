package com.nisco.hzp.home_component.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.nisco.family.common.base.BaseActivity;
import com.nisco.family.common.constant.CommonConstants;
import com.nisco.family.common.utils.RouterUtil;
import com.nisco.hzp.home_component.R;

/**
 * 主界面activity
 */
public class HomeMainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = HomeMainActivity.class.getSimpleName();
    private LinearLayout mContainerLl;
    private Button mMnistBtn;
    private Button mStaticClassifierBtn;
    private Button mClassifierBtn;
    private Button mStaticDetectorBtn;
    private Button mDetectorBtn;
    private Button mStylizeBtn;
    private Button mSpeechBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.home_activity_main);
        initViews();
    }

    private void initViews() {
        mContainerLl = (LinearLayout) findViewById(R.id.container_ll);
        mMnistBtn = (Button) findViewById(R.id.mnist_btn);
        mStaticClassifierBtn = (Button) findViewById(R.id.static_classifier_btn);
        mClassifierBtn = (Button) findViewById(R.id.classifier_btn);
        mStaticDetectorBtn = (Button) findViewById(R.id.static_detector_btn);
        mDetectorBtn = (Button) findViewById(R.id.detector_btn);
        mStylizeBtn = (Button) findViewById(R.id.stylize_btn);
        mSpeechBtn = (Button) findViewById(R.id.speech_btn);

        mMnistBtn.setOnClickListener( this );
        mStaticClassifierBtn.setOnClickListener( this );
        mClassifierBtn.setOnClickListener( this );
        mStaticDetectorBtn.setOnClickListener( this );
        mDetectorBtn.setOnClickListener( this );
        mStylizeBtn.setOnClickListener( this );
        mSpeechBtn.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mnist_btn) {
//            ARouter.getInstance().build( RouterUtil.Mnist_Main_Activity).navigation(this, CommonConstants.MNIST);
            ARouter.getInstance().build( RouterUtil.Mnist2_Main_Activity).navigation(this, CommonConstants.MNIST);
        } else if (i == R.id.static_classifier_btn) {//相册图像分类

//            ARouter.getInstance().build( RouterUtil.TFlite_Static_Detector_Activity ).navigation( this,CommonConstants.MNIST );
            ARouter.getInstance().build( RouterUtil.TestTFlite_Main_Activity ).navigation( this,CommonConstants.MNIST );
        } else if (i == R.id.classifier_btn) {
            ARouter.getInstance().build( RouterUtil.TF_Classifier_Activity).navigation(this, CommonConstants.MNIST);
        } else if (i == R.id.static_detector_btn) {//相册图像识别
//            ARouter.getInstance().build( RouterUtil.TFlite_Detector ).navigation( this,CommonConstants.MNIST );
            ARouter.getInstance().build( RouterUtil.TFlite_Detector_Activity ).navigation( this,CommonConstants.MNIST );
//            ARouter.getInstance().build( RouterUtil.TF_Static_Detector_Activity ).navigation( this,CommonConstants.MNIST );
        } else if (i == R.id.detector_btn) {
            ARouter.getInstance().build( RouterUtil.TF_Detector_Activity).navigation(this, CommonConstants.MNIST);
        } else if (i == R.id.stylize_btn) {
            ARouter.getInstance().build( RouterUtil.TF_Stylize_Activity).navigation(this, CommonConstants.MNIST);
        } else if (i == R.id.speech_btn) {
            ARouter.getInstance().build( RouterUtil.TF_Speech_Activity).navigation(this, CommonConstants.MNIST);
        }
    }

    @Override
    public String getActivityName() {
        return TAG;
    }
}
