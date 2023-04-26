package com.xiliulou.afterserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (ProductNewTestContent)实体类
 *
 * @author Eclair
 * @since 2023-04-26 17:22:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_product_new_test_content")
public class ProductNewTestContent {
    
    private Long id;
    
    private Long pid;
    
    private String content;

    private Long createTime;

    private Long updateTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}
