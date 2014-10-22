package com.candy.dbtransfer.mapping;

import com.candy.dbtransfer.config.R;
import com.candy.dbtransfer.util.StringUtils;
import org.dom4j.Element;

/**
 * Created by yantingjun on 2014/10/22.
 */
public class ValueFactory {
    public static Value build(Element value_ele){
        String type = "";
        if(value_ele!=null){
            type = value_ele.attributeValue("type");
        }
        if(StringUtils.isBlank(type)){
            return new ExpValue();
        }
        if(R.value.sql.equals(type)){
            SqlValue sqlValue = new SqlValue();
            Element select_ele = (Element)value_ele.selectSingleNode("select");
            if(select_ele != null){
                String sql = select_ele.getText();
                if(StringUtils.isNotBlank(sql)){
                    sqlValue.setValue(sql);
                }
                String result_type = select_ele.attributeValue("result-type");
                if(StringUtils.isNotBlank(result_type)){
                    sqlValue.setResultType(SqlValue.ResultType.valueOf(result_type));
                }
            }
            return sqlValue;
        }else{
            Value value = new ExpValue();
            if(value_ele.isTextOnly()){
                String expression = value_ele.getText();
                if(StringUtils.isNotBlank(expression)){
                    value.setValue(expression);
                }
            }
            return value;
        }
    }
}
