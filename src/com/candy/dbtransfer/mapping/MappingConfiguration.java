package com.candy.dbtransfer.mapping;

import com.candy.dbtransfer.config.R;
import com.candy.dbtransfer.util.StringUtils;
import com.candy.dbtransfer.xml.XmlReader;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class MappingConfiguration {
    private MappingConfiguration(){
       Document document = XmlReader.read();
        Element root = document.getRootElement();
        Node global_maps = root.selectSingleNode("global-maps");
        if(global_maps != null){
            List<Node> columns = global_maps.selectNodes("column");
            if(columns  != null){
                for(Node column : columns){
                    ColumnMapping columnMapping = buildColumnMapping(column);
                    if(columnMapping != null){
                        global_mappings.addColumn(columnMapping);
                    }
                }
            }
        }

        List<Node> entitys = root.selectNodes("entity");
        if(entitys  != null) {
            for (Node entity : entitys) {
                EntityMapping entityMapping = new EntityMapping();
                String src_name = ((Element)entity).attributeValue("src-name");
                String tar_name = ((Element)entity).attributeValue("tar-name");
                String type = ((Element)entity).attributeValue("type");
                if(StringUtils.isNotBlank(type) && R.entity.types.contains(type)){
                    entityMapping.setType(type);
                }

                List<Attribute> attributes = ((Element)entity).attributes();
                for(Attribute attribute : attributes){
                    if(attribute.getQualifiedName().startsWith("var-") && attribute.getQualifiedName().length()>4){
                        entityMapping.addVariable(attribute.getQualifiedName().substring("var-".length()),attribute.getValue());
                    }
                }
                if(StringUtils.isBlank(src_name)){
                    continue;
                }
                if(StringUtils.isBlank(tar_name)){
                    tar_name = src_name;
                }
                entityMapping.setSrc_name(src_name);
                entityMapping.setTar_name(tar_name);

                List<Node> columns = entity.selectNodes("column");
                if(columns  != null){
                    for(Node column : columns){
                        ColumnMapping columnMapping = buildColumnMapping(column);
                        if(columnMapping != null){
                            entityMapping.addColumn(columnMapping);
                        }
                    }
                }
                mappings.put(src_name,entityMapping);
            }
        }
    }
    private ColumnMapping buildColumnMapping(Node column){
        Element column_ele = (Element)column;
        String src_name = column_ele.attributeValue("name");
        if(StringUtils.isBlank(src_name)){
            return null;
        }
        String tar_name = column_ele.attributeValue("tar-name");
        if(StringUtils.isBlank(tar_name)){
            tar_name = src_name;
        }
        ColumnMapping columnMapping = new ColumnMapping(src_name,tar_name);
        String type = column_ele.attributeValue("type");
        if(StringUtils.isNotBlank(type) && R.column.types.contains(type)){
            columnMapping.setType(type);
        }
        String data_type = column_ele.attributeValue("data-type");
        if(StringUtils.isNotBlank(type) && R.column.types.contains(type)){
            columnMapping.setData_type(data_type);
        }
        if(R.entity.type.add.equalsIgnoreCase(type)){
            Element value_ele = (Element)column_ele.selectSingleNode("value");
            columnMapping.setValue(ValueFactory.build(value_ele));
        }
        return columnMapping;
    }
    private static MappingConfiguration configuration;
    public static MappingConfiguration getInstance(){
        if(configuration == null){
            configuration = new MappingConfiguration();
        }
        return configuration;
    }
    private EntityMapping global_mappings = new EntityMapping();
    private Map<String,EntityMapping> mappings = new LinkedHashMap<String, EntityMapping>();

    public EntityMapping getGlobal_mappings() {
        return global_mappings;
    }

    public void setGlobal_mappings(EntityMapping global_mappings) {
        this.global_mappings = global_mappings;
    }

    public Map<String,EntityMapping> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String,EntityMapping> mappings) {
        this.mappings = mappings;
    }
}
