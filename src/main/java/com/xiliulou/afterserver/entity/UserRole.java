package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户角色表*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user_role")
public class UserRole {
    /**
     * 编号
     * */
    private  Long id;
    /**
     * 用户id
     * */
    private  Long uid;
    /**
     * 角色id
     * */
    private Long rid;
}
