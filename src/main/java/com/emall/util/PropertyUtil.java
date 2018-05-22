package com.emall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
public class PropertyUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private static Properties props;


    static {
        props = new Properties();
        try {
            //load emall.properties file, this method get the path as relative to the root of classpath set in applicationContext
            props.load(new InputStreamReader(PropertyUtil.class.getClassLoader().getResourceAsStream("emall.properties")));
        } catch (IOException e) {
            logger.error("IOException PropertyUtil.java", e);
            e.printStackTrace();
        }
    }

    public static String getValue(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return props.getProperty(key).trim();
    }
}
