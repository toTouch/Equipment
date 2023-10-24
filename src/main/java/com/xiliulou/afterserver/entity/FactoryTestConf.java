package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author lny
 * @date 2023/8/18 13:38
 * @Description: 工厂压测配置
 *
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
     COMMENT '上架状态，0-上架、1-下架'
     */
    private Integer shelfStatus;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;
}
