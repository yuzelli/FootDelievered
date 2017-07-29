package com.example.yuzelli.fooddelivered.constants;

/**
 * Created by 51644 on 2017/7/5.
 */

public class ConstantsUtils {

    public static final int SPLASH_START_ACTIVITY = 0x00001001;
    //用户登录成功
    public static final int LOGIN_GET_DATA = 0x00001002;
    public static final int REGISTER_GET_DATA = 0x00001003;
    public static final int NEW_ORDER_LIST_GET_DATA = 0x00001004;
    public static final int RECEIVE_ORDER_LIST_GET_DATA = 0x00001005;
    public static final int GET_NOW_ORDER_LIST_GET_DATA = 0x00001006;
    public static final int FINISH_NOW_ORDER_LIST_GET_DATA = 0x00001007;
    public static final int GET_HISTORY_ORDER_DATA = 0x00001008;
    public static final int GET_HISTORY_COOUNT_DATA = 0x00001009;




    //发送倒计时
    public static final int SENG_COUNT_DOWN_MESSAGE = 0x00002001;
    public static final int SENG_GET_ORDER_MESSAGE = 0x00002002;
    public static final int SENG_GET_NOW_ORDER_MESSAGE = 0x00002003;





    public static final String ADDRESS_URL = "http://1.mssd.applinzi.com/";
    public static final String USER_LOGIN = "appapi";
    public static final String REGISTER_USER = "appapi/register";
    public static final String NEW_ORDER_LIST = "appapi/get_new_order";
    public static final String RECEIVE_ORDER = "appapi/staff_order_taking";
    public static final String GET_NOW_ORDER = "appapi/staff_current_order";
    public static final String FINISH_NOW_ORDER = "appapi/staff_finish_order";
    public static final String GET_HISTORY_ORDER_LIST= "appapi/staff_history_order";
    public static final String GET_HISTORY_UHSER_COUNT_INFO= "appapi/count_info";


    //保持登录用户信息
    public static final String SP_LOGIN_USER_INFO = "UserInfo";
    public static final String SP_NOW_ORDER_INFO = "NOW_ORDER";
    public static final String SP_TOAST_USER_INFO = "TOAST_USER_INFO";


}
