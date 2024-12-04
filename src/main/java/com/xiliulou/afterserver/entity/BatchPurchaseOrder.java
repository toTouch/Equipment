package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * (BatchPurchaseOrder)实体类
 *
 * @author zhangbozhi
 * @since 2024-12-03 16:29:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_batch_purchase_order")
public class BatchPurchaseOrder implements Serializable {
    
    private static final long serialVersionUID = -35596153172167192L;
    
    private Integer id;
    
    /**
     * 采购单
     */
    private String purchaseOrder;
    
    /**
     * 项次
     */
    private String item;
    
    /**
     * 成品料号
     */
    private String materialNo;
    
    /**
     * 批次号
     */
    private Long batchId;
    
    
}

