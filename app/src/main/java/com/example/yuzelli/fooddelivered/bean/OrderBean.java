package com.example.yuzelli.fooddelivered.bean;

import com.example.yuzelli.fooddelivered.utils.GsonUtils;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 51644 on 2017/7/8.
 */

public class OrderBean implements Serializable {

    private String order_id;
    private String order_status;
    private String username;
    private String mobile;
    private String address;
    private String img_url;
    private String add_time;

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public static ArrayList<OrderBean> getOrderList(String jsonArray){
        return GsonUtils.jsonToArrayList(jsonArray,OrderBean.class);

    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
