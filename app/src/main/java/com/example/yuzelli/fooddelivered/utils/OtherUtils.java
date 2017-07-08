package com.example.yuzelli.fooddelivered.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 51644 on 2017/7/6.
 */

public class OtherUtils {


    /**
     * 验证电话号码是否符合格式
     * @return true （符合）or false（不符合）
     */
    public static boolean isPhoneEnable(String strPhone) {
        boolean b = false;
        if (strPhone.length() == 11) {
            Pattern pattern = null;
            Matcher matcher = null;
            pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
            matcher = pattern.matcher(strPhone);
            b = matcher.matches();
        }
        return b;
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param format 如：
     * @return
     */
    public static long date2TimeStamp(String date_str){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(date_str).getTime()/1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
