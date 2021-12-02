package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**角色权限表*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_role_permission")
public class RolePermission {
    /**编号*/
    private Long id;
    /**角色id*/
    private Long rid;
    /**权限id*/
    private Long cid;
}
