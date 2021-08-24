package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * (Batch)实体类
 *
 * @author Eclair
 * @since 2021-08-16 15:50:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_batch")
public class Batch {

    private Long id;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 点位ID
     */
    private String remarks;
    /**
     * 创建时间
     */
    private Long createTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;


    @TableField(exist = false)
    private List<ProductFile> productFileList;

}
