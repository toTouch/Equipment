package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hardy
 * @date 2021/12/13 15:34
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_login_info")
public class LoginInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * ip
     */
    private String ip;
    /**
     * uid
     */
    private Long uid;
    /**
     * 登录时间
     */
    private Long loginTime;
    /**
     * 登录类型(1:后台,2:用户)
     */
    private Integer type;
    /**
     * 备注
     */
    private String remark;
}
