package com.xiliulou.afterserver.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (Parts)实体类
 *
 * @author Eclair
 * @since 2022-12-15 15:02:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_parts")
public class Parts {
    
    private Long id;
    
    private String name;
    
    private BigDecimal price;
    
    private Long createTime;
    
    private Long updateTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}
