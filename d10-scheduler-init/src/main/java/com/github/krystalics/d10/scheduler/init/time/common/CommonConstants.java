package com.github.krystalics.d10.scheduler.init.time.common;

import java.nio.charset.Charset;

/**
 * 常量
 * @author krysta
 */

public class CommonConstants {

    /**
     * 普通
     */
    public final static int ZERO = 0;

    public final static int FIFTY_NINE = 59;

    public static final String UTF8 = "UTF-8";                                               //UTF-8

    public static final Charset UTF8_CHARSET =  Charset.forName(CommonConstants.UTF8);       //UTF-8

    /**
     * java 进程退出
     */
    public static final int SYSTEM_EXIT_SUCCESS = 0;

    public static final int SYSTEM_EXIT_ERROR = -1;
    /**
     * 常见字符
     */
    public static final String BLANK_SPACE = " ";                                             //空格

    public static final String COMMA = ",";                                                   //逗号

    public static final String POINT = ".";                                                   //句号

    public static final String COLON = ":";                                                   //冒号

    public static final String SEMICOLON = ";";                                               //分号

    public static final String LINE = "-";                                                    //横线

    public static final String DOUBLE_LINE = "--";                                            //双横线

    public static final String UNDERLINE = "_";                                               //下划线

    public static final String FILE_SPLIT = "/";                                              //文件分隔符

    public static final String EQUAL = "=";                                                   //等号

    public static final String QUESTION = "?";                                                //问号

    public static final String ASTERISK = "*";                                                //星号

    /**
     * url相关
     */
    public static final String URL_QUESTION = QUESTION;                                       //问号

    public static final String URL_PARAM_SPLIT = "&";                                         //url参数间分隔符

    public static final String URL_PROTOCOL_SPLIT = "://";                                    //url协议分隔符

}
