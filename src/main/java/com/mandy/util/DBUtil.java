package com.mandy.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Created by MandyOu on 2019/11/14
 */
public class DBUtil {

    private static Properties props;

    static{
        try{
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
            props = new Properties();
            props.load(in);//把in中的消息经过处理加载到props中
            in.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConn() throws ClassNotFoundException, SQLException {
        String url = props.getProperty("spring.datasource.url");
        String username = props.getProperty("spring.datasource.username");
        String password = props.getProperty("spring.datasource.password");
        String driver = props.getProperty("spring.datasource.driver-class-name");
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
    }

}
