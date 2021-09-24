package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2021/9/17 18:00
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("product_lending_application")
public class ProductIendingApplication {
    private Long id;

    private String user;

    private Long createTime;

    private Long returnTime;

    private Long status;

    private Long wareHouseId;

    private String no;

    /**
     * 待审核
     */
    public static final Long STATUS_PENDING_REVIEW = 0L;
    /**
     * 执行中
     */
    public static final Long STATUS_EXECUTING = 1L;
    /**
     *已完结
     */
    public static final Long STATUS_CLOSED = 2L;
}
