package com.example.yuzelli.fooddelivered.bean;

import java.io.Serializable;

/**
 * Created by 51644 on 2017/7/6.
 */

public class UserInfo implements Serializable {
    private String mobile;
    private String password;
    private String stId;
    private String userName;

    public UserInfo(String mobile, String password,String stId,String userName) {
        this.mobile = mobile;
        this.password = password;
        this.stId = stId;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
