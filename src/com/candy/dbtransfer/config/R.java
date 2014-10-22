package com.candy.dbtransfer.config;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yantingjun on 2014/9/21.
 */
public class R {
    public static class Constants{
        public static final String DEFAULT_ENCODING = "utf-8";
        public static final String DEFAULT_LOCALE = "ZH_CN";
        public static final String default_system_file = "config.properties";
        public static final String default_mapping_file = "mapping.xml";
        public static final String default_prop_type = "String";

        public static final String mysql_driver = "com.mysql.jdbc.Driver";
    }

    public static class template{
        public static final String suffix = ".ftl";
    }
    public static class regex{
        public static final String method = "$([^\\(]+)\\([^\\)+]\\)";
    }
    public static class entity{
        public static List<String> types = Arrays.asList("add","transfer","exclude");
        public static class type{
            public static String add = "add";
            public static String transfer = "transfer";
            public static String exclude = "exclude";
        }

    }
    public static class column{
        public static List<String> types = Arrays.asList("add","transfer","exclude");
        public static String default_type = "transfer";
        public static String default_data_type = "auto";
        public static class type{
            public static String add = "add";
            public static String transfer = "transfer";
            public static String exclude = "exclude";
        }
        public static class data_type{
            public static String auto = "auto";
            public static String integer = "integer";
            public static String string = "string";
            public static String time = "time";
        }

    }
    public static class value{
        public static final String sql = "sql";
        public static final String exp = "exp";
    }
}
