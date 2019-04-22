package com.nisco.family.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tianzy on 2019/1/29.
 */

public class UserApp implements Parcelable {
    private String appName;//app名称
    private int appIcon;//app图标

    private String appDescription;//app描述
    private String downloadUrl;//app下载链接
    private String packageName;//应用包名

    public UserApp(String appName, int appIcon) {
        this.appName = appName;
        this.appIcon = appIcon;
    }

    public UserApp(String appName, int appIcon, String appDescription, String downloadUrl, String packageName) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.appDescription = appDescription;
        this.downloadUrl = downloadUrl;
        this.packageName = packageName;
    }

    protected UserApp(Parcel in) {
        appName = in.readString();
        appIcon = in.readInt();
        appDescription = in.readString();
        downloadUrl = in.readString();
        packageName = in.readString();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(int appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public static final Creator<UserApp> CREATOR = new Creator<UserApp>() {
        @Override
        public UserApp createFromParcel(Parcel in) {
            return new UserApp( in );
        }

        @Override
        public UserApp[] newArray(int size) {
            return new UserApp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( appName );
        dest.writeInt( appIcon );
        dest.writeString( appDescription );
        dest.writeString( downloadUrl );
        dest.writeString( packageName );
    }

}
