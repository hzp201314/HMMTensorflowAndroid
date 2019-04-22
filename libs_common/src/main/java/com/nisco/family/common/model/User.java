package com.nisco.family.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tianzy on 2018/9/10.
 */

public class User implements Serializable {


    /**
     * _id : 325bb135-b0a0-d580-ed97-9b9e5341b6be
     * username : 020463
     * realName : 王玮玮
     * joinTime : 2017-01-03T06:25:12.407Z
     * timeStamp : 1483424712407
     * nickName : 匿名侠
     * authority : 0
     * avator : userAvator/Default.png
     * role : ["6ace9c89-6cb0-2a55-bb83-6d70bbb8473b"]
     * deviceToken :
     * token : 5d346d94-a73c-61ed-0fd8-f9c11b49f414
     * Ltype : O
     */

    private String _id;
    private String username;
    private String realName;
    private String joinTime;
    private long timeStamp;
    private String nickName;
    private int authority;
    private String avator;
    private String deviceToken;
    private String token;
    private String Ltype;
    private List<String> role;
    private String userNo = "";
    private String pwd;
    private boolean isFirstIn = true;
    private String companyName;
    private String departmentName;
    private String headUrl;
    private String erpToken;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public boolean isFirstIn() {
        return isFirstIn;
    }

    public void setFirstIn(boolean firstIn) {
        isFirstIn = firstIn;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLtype() {
        return Ltype;
    }

    public void setLtype(String Ltype) {
        this.Ltype = Ltype;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public String getErpToken() {
        return erpToken;
    }

    public void setErpToken(String erpToken) {
        this.erpToken = erpToken;
    }
}
