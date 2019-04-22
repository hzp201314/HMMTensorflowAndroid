package com.nisco.family.common.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.nisco.family.common.R;
import com.nisco.family.common.utils.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;

import okhttp3.Cache;

public class BaseApplication extends Application {
    public static Cache cache;
    private String cookie;
    private boolean isDismiss;
    public RequestOptions options;

    public static final String ROOT_PACKAGE = "com.nisco.family";

    private static BaseApplication instance;
    private String userNo="";
    private String equipTreeNodeStr = "";

    public String getEquipTreeNodeStr() {
        return equipTreeNodeStr;
    }

    public void setEquipTreeNodeStr(String equipTreeNodeStr) {
        this.equipTreeNodeStr = equipTreeNodeStr;
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.color_f9f8f7, R.color.color_333333);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Utils.init(this);
        initRouter(this);

        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), null);

        if (Build.VERSION.SDK_INT != 19) {
            File cacheFile = new File(getCacheDir().toString(), "cache");
            int cacheSize = 2 * 1024 * 1024;
            cache = new Cache(cacheFile, cacheSize);
        }

        setOption();
    }

    private void initRouter(BaseApplication application) {
        if (Utils.isAppDebug()) {
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(application);
    }

    /*
    设置图片圆角
     */
    private void setOption() {
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(8);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        options = RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public boolean isDismiss() {
        return isDismiss;
    }

    public void setDismiss(boolean dismiss) {
        isDismiss = dismiss;
    }
}
