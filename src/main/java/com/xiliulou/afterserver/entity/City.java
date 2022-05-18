package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (City)实体类
 *
 * @author Eclair
 * @since 2021-01-21 18:05:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_city")
public class City {
    
    private Integer id;
    /**
    * 市code
    */
    private String code;
    /**
    * pid
    */
    private Integer pid;
    /**
    * 城市名称
    */
    private String name;
    /**
     *城市纬度
     */
    private String coordX;
    /**
     *城市经度
     */
    private String coordY;


    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}