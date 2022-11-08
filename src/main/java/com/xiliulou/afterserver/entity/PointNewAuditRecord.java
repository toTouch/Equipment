package com.xiliulou.afterserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (PointNewAuditRecord)实体类
 *
 * @author Eclair
 * @since 2022-11-08 13:36:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_point_new_audit_record")
public class PointNewAuditRecord {
    
    private Long id;
    
    private Long pointId;
    
    private Long uid;
    
    private String userName;
    /**
    * 1 待审核 2 未通过 3 已通过
    */
    private Integer auditStatus;
    
    private Long createTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}
