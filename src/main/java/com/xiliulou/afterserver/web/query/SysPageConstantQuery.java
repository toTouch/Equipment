package com.xiliulou.afterserver.web.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 系统页面常量表(SysPageConstant)实体类
 *
 * @author zhangbozhi
 * @since 2024-11-01 17:11:47
 */
public class SysPageConstantQuery implements Serializable {
    
    /**
     * APP版本号列表
     */
    private List<String> cabinetAppVersions;
    
}

