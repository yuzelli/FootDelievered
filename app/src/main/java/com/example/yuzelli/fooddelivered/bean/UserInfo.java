package com.example.yuzelli.fooddelivered.bean;

import java.io.Serializable;

/**
 * Created by 51644 on 2017/7/6.
 */

public class UserInfo implements Serializable {
    private String mobile;
    private String password;

    public UserInfo(String mobile, String password) {
        this.mobile = mobile;
        this.password = password;
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
