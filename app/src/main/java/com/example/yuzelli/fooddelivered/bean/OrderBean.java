package com.example.yuzelli.fooddelivered.bean;

import com.example.yuzelli.fooddelivered.utils.GsonUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 51644 on 2017/7/8.
 */

public class OrderBean implements Serializable {

    /**
     * order_id : 42
     * order_sn : 180
     * order_status : 0
     * username :
     * mobile : 13133443011
     * img_url : http://plum-public.stor.sinaapp.com/Uploads/596e17de5a9a5.png
     * add_time : 2017-07-18 22:14:54
     * outtime : 123
     * order_type : 3
     */

    private String order_id;
    private String order_sn;
    private String order_status;
    private String username;
    private String mobile;
    private String img_url;
    private String add_time;
    private String outtime;
    private String order_type;

    public static ArrayList<OrderBean> getOrderList(String jsonArray){
        return GsonUtils.jsonToArrayList(jsonArray,OrderBean.class);

    }
    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
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

    public String getOuttime() {
        return outtime;
    }

    public void setOuttime(String outtime) {
        this.outtime = outtime;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }
}
