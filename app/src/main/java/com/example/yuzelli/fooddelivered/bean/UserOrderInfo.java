package com.example.yuzelli.fooddelivered.bean;

import java.io.Serializable;

/**
 * Created by 51644 on 2017/7/15.
 */

public class UserOrderInfo implements Serializable {

    /**
     * mall : 12
     * mfinished : 11
     * mouttime : 0
     * aorder : 12
     * afinished : 11
     * aouttime : 0
     * start : 2017-07-01 00:00:00
     * end : 2017-07-15 02:21:24
     */

    private String mall;
    private String mfinished;
    private String mouttime;
    private String aorder;
    private String afinished;
    private String aouttime;
    private String start;
    private String end;

    public String getMall() {
        return mall;
    }

    public void setMall(String mall) {
        this.mall = mall;
    }

    public String getMfinished() {
        return mfinished;
    }

    public void setMfinished(String mfinished) {
        this.mfinished = mfinished;
    }

    public String getMouttime() {
        return mouttime;
    }

    public void setMouttime(String mouttime) {
        this.mouttime = mouttime;
    }

    public String getAorder() {
        return aorder;
    }

    public void setAorder(String aorder) {
        this.aorder = aorder;
    }

    public String getAfinished() {
        return afinished;
    }

    public void setAfinished(String afinished) {
        this.afinished = afinished;
    }

    public String getAouttime() {
        return aouttime;
    }

    public void setAouttime(String aouttime) {
        this.aouttime = aouttime;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
