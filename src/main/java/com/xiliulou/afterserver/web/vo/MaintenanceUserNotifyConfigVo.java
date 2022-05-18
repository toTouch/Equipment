package com.xiliulou.afterserver.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zgw
 * @date 2022/5/16 14:40
 * @mood
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceUserNotifyConfigVo {
     private Integer id;
    /**
     * 权限
     */
    private Integer permissions;
    /**
     * 手机号
     */
    private List<String> phones;
}
