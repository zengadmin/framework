package com.fyqz.frameowrk.util;

import org.apache.logging.log4j.util.Supplier;
import org.slf4j.Logger;

import java.util.Objects;

/**
 * @Title: LogUtil
 * @ProjectName: fyqz
 * @Description: TODO
 * @author: zengchao
 * @date: 2018/11/2 14:16
 */
public class LogUtil {
    private static final LogUtil log=new LogUtil();
    private LogUtil(){

    }
    public static LogUtil getInstance(){
        return  log;
    }
    /**
     * @Description: 打印日誌
     * @author :zengchao
     * @date : 2018/11/2 16:04
     * @param logger : 日誌
     * @param format : 日誌信息
     * @param obj :  参数
     * @return : void
     */
    public static void debug(Logger logger, String format, Object ... obj){
        if(logger.isDebugEnabled()){
            logger.debug(format,obj);
        }
    }

    public static void debug(Logger logger, Supplier<Object> supplier){
        if(logger.isDebugEnabled()){
            logger.debug(Objects.toString(supplier.get()));
        }
    }
    public static void info(Logger logger, String format, Object ... obj){
        if(logger.isInfoEnabled()){
            logger.info(format,obj);
        }
    }

    public static void  info(Logger logger, Supplier<Object> supplier){
        if(logger.isInfoEnabled()){
            logger.info(Objects.toString(supplier.get()));
        }
    }
    public static void error(Logger logger, String format, Object ... obj){
        if(logger.isErrorEnabled()){
            logger.error(format,obj);
        }
    }

    public static void error(Logger logger, Supplier<Object> supplier){
        if(logger.isErrorEnabled()){
            logger.error(Objects.toString(supplier.get()));
        }
    }
    public static void warn(Logger logger, String format, Object ... obj){
        if(logger.isWarnEnabled()){
            logger.warn(format,obj);
        }
    }

    public static void warn(Logger logger, Supplier<Object> supplier){
        if(logger.isWarnEnabled()){
            logger.warn(Objects.toString(supplier.get()));
        }
    }
}
