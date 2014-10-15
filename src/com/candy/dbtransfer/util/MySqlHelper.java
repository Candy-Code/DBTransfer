package com.candy.dbtransfer.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class MySqlHelper {
    static Log log = Log.getLog(MySqlHelper.class);

    public static List<String> getTables(Connection connection){
        List<String> table_names = new ArrayList<String>();
        try {
            ResultSet tables = connection.getMetaData().getTables(null,null,null,new String[]{"TABLE"});
            while(tables.next()){
                table_names.add(tables.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table_names;
    }
    public static ResultSet selectAll(Connection conn,String table_name){
        String sql = "select * from "+table_name;
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }
}
