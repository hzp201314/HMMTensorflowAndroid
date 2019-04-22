package com.nisco.family.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by cathy on 2016/12/24.
 */
public class Video implements Serializable {
    private String snapshotUrl;
    private String videoName;
    private String description;
    private String sdMp4Url;
    private String createTime;
    private String newSnapshotUrl;


    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSdMp4Url() {
        return sdMp4Url;
    }

    public void setSdMp4Url(String sdMp4Url) {
        this.sdMp4Url = sdMp4Url;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getNewSnapshotUrl() {
        return newSnapshotUrl;
    }

    public void setNewSnapshotUrl(String newSnapshotUrl) {
        this.newSnapshotUrl = newSnapshotUrl;
    }

}
