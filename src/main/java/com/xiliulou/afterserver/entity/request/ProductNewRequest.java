package com.xiliulou.afterserver.entity.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xiliulou.afterserver.constant.ProductNewStatusSortConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductNewRequest {
    
    private Long id;
    
    /**
     * 产品编号
     */
    private String no;
    
    /**
     * 型号ID
     */
    private Long modelId;
    
    /**
     * 点位ID
     */
    private Long pointId;
    
    private Integer pointType;
    
    private Long startTime;
    
    private Long endTime;
    
    /**
     * 测试类型 0--前置 1--后置
     */
    private String testType;
    
    /**
     * 柜机编码
     */
    private String cabinetSn;
    
    /**
     * 产品Name
     */
    private String batchName;
    /**
     * 产品批次ID
     */
    private Long batchId;
    
}
