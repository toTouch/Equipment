package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lny
 * @date 2023/8/18 13:38
 * @Description: 工厂压测配置
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_factory_test_conf")
public class FactoryTestConf {
    
    private Long id;
    
    /**
     * 名称
     */
    private String confName;
    
    /**
     * 配置内容JSON
     */
    private String jsonContent;
    
    private Long updateTime;
    
    private Long createTime;
    
    private Integer delFlag;
    
    /**
     * 上架状态，0-上架、1-下架
     */
    private Integer shelfStatus;
    
    /**
     * 所属工厂ids 格式 [1,2,3]
     */
    private String supplierIds;
    
    /**
     * 所属工厂id
     */
    @TableField(exist = false)
    private List<Long> supplierIdList;
    
    /**
     * 所属工厂名称
     */
    @TableField(exist = false)
    private List<String> supplierNameList;
    
    public static final Integer DEL_NORMAL = 0;
    
    public static final Integer DEL_DEL = 1;
}
