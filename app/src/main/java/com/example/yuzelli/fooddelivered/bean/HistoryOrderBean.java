package com.example.yuzelli.fooddelivered.bean;

import java.io.Serializable;

/**
 * Created by 51644 on 2017/7/9.
 */

public class HistoryOrderBean implements Serializable {

    /**
     * order_id : 5
     * order_status : 2
     * stId : 4
     * tel : 13133443006
     * mobile : 4
     * username : 4
     * address : 4
     * img_url :
     * add_time : 2017-07-08 22:10:08
     * confirm_time : 2017-07-09 00:54:07
     * finished_time : 2017-07-09 02:02:06
     */

    private String order_id;
    private String order_status;
    private String stId;
    private String tel;
    private String mobile;
    private String username;
    private String address;
    private String img_url;
    private String add_time;
    private String confirm_time;
    private String finished_time;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(String confirm_time) {
        this.confirm_time = confirm_time;
    }

    public String getFinished_time() {
        return finished_time;
    }

    public void setFinished_time(String finished_time) {
        this.finished_time = finished_time;
    }
}
