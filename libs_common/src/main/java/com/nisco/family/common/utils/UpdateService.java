package com.nisco.family.common.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nisco.family.common.R;

//import com.nisco.family.mine.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by cathy on 2016/12/31.
 */

public class UpdateService extends Service {

    private String appName;
    private String url;
    private String target;
    // 通知栏
    private Notification notification = null;
    private NotificationManager notificationManager = null;
    // 通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent pendingIntent = null;
    Notification.Builder builder1;
    /***
     * 创建通知栏
     */
    RemoteViews contentView;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            appName = intent.getStringExtra("Key_App_Name");
            url = intent.getStringExtra("Key_Down_Url");
//            File dir = StorageUtils.getCacheDirectory(getBaseContext());
            File dir = Environment.getExternalStorageDirectory();
//            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File apkFile = new File(dir, "update.apk");
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            target = apkFile.toString();
            //创建下载APK的路径
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            builder1 = new Notification.Builder(this);
            builder1.setSmallIcon( R.mipmap.common_app_icon); //设置图标
            builder1.setTicker("开始下载");
            builder1.setContentTitle(appName); //设置标题
            builder1.setContentText("正在下载:"); //消息内容
            builder1.setWhen( System.currentTimeMillis()); //发送时间
//		builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
            builder1.setAutoCancel(true);//打开程序后图标消失
//		// 设置下载过程中，点击通知栏，回到主界面
//		updateIntent = new Intent(this, MainActivity.class);
//		pendingIntent =PendingIntent.getActivity(this, 0, updateIntent, 0);
//		builder1.setContentIntent(pendingIntent);

            notification = builder1.build();
            notificationManager.notify(124, notification); // 通过通知管理器发送通知
            downLoad(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_REDELIVER_INTENT;
    }

    private void downLoad(String url) {
//        TODO 下载更新服务没有完成
        HttpUtils utils = new HttpUtils();
        utils.download(url, target, new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                builder1.setContentText("正在下载:" + current * 100 / total + "/100"); //消息内容
                if (current == total) {
                    builder1.setContentText("下载完成");
                    builder1.setDefaults( Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
                }
                try {
                    Thread.sleep(2000);
                    notification = builder1.build();
                    notificationManager.notify(124, notification); // 通过通知管理器发送通知
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "下载出错，请稍后再试...", Toast.LENGTH_SHORT).show();
                }
                super.onLoading(total, current, isUploading);
            }

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {

                //判读版本是否在7.0以上
                if (Build.VERSION.SDK_INT >= 24) {
                    Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", arg0.result);//在AndroidManifest中的android:authorities值
                    Intent install = new Intent( Intent.ACTION_VIEW);
                    install.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(install);
                } else {
                    Intent install = new Intent( Intent.ACTION_VIEW);
                    install.setDataAndType( Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
                    install.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(install);
                }

//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
//                //在BroadcastReceicer和Service中startActivity要加上此设置
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                    // 停止服务
//				    stopService(updateIntent);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // 停止服务
//                stopService(updateIntent);
                Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
