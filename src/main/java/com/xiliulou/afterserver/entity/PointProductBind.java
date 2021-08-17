package com.xiliulou.afterserver.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (PointProductBind)实体类
 *
 * @author Eclair
 * @since 2021-08-17 10:28:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_point_product_bind")
public class PointProductBind {

    private Long id;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 点位id
     */
    private Long pointId;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}
