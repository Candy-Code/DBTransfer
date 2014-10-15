package com.candy.dbtransfer.connector;

import com.candy.dbtransfer.config.R;
import com.candy.dbtransfer.property.PropertiesReader;
import com.candy.dbtransfer.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class MySqlConnector {
    Log log = Log.getLog(MySqlConnector.class);

    public Connection getConnection(){
        PropertiesReader p = PropertiesReader.getInstance();

        // 驱动程序名
        String driver = p.getValue("mysql.driver",R.Constants.mysql_driver);

        // URL指向要访问的数据库名scutcs
        String url = p.getValue("mysql.url");

        // MySQL配置时的用户名
        String user = p.getValue("mysql.username");

        // MySQL配置时的密码
        String password = p.getValue("mysql.password");

        try {
            // 加载驱动程序
            Class.forName(driver);

            // 连续数据库
            Connection conn = DriverManager.getConnection(url, user, password);
            if (!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            return conn;
        }catch (Exception e){
            log.error(e);
            return null;
        }
    }
}
