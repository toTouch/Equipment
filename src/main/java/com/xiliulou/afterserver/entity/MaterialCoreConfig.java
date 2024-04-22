package com.xiliulou.afterserver.entity;

import java.io.Serializable;

/**
 * 物料核心配置(MaterialCoreConfig)实体类
 *
 * @author makejava
 * @since 2024-03-21 19:03:03
 */
public class MaterialCoreConfig implements Serializable {
    private static final long serialVersionUID = 418492726165406216L;

    private Integer id;
/**
     * 物料核心配置
     */
    private String materialCoreConfig;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaterialCoreConfig() {
        return materialCoreConfig;
    }

    public void setMaterialCoreConfig(String materialCoreConfig) {
        this.materialCoreConfig = materialCoreConfig;
    }

}

