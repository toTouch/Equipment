package com.xiliulou.afterserver.web.query;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 物料追溯表(MaterialTraceability)
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */

@Data
public class MaterialTraceabilityQuery implements Serializable {
    
    private static final long serialVersionUID = 891201978381378054L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 物料sn
     */
    private String materialSn;
    
    /**
     * 产品编号/资产编码
     */
    private String productNo;
    
    @TableField(exist = false)
    private Long startTime;
    
    @TableField(exist = false)
    private Long endTime;
}

