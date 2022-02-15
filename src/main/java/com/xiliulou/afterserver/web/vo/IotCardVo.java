package com.xiliulou.afterserver.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/15 11:08
 * @mood
 */
@Data
public class IotCardVo {
    private Long id;
    private String sn;
    private Long batchId;
    private Long supplierId;
    private Integer operator;
    private Long activationTime; //激活时间
    private String packages;
    private Long termOfAlidity; //有效时间
    private Long expirationTime; //过期时间
    private Long createTime;
    private Long updateTime;
    private Integer delFlag;
    private Integer expirationFlag;
    private String batchName;
    private String supplierName;
}
