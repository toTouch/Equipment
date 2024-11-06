package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.SysPageConstant;

import java.util.List;

/**
 * 系统页面常量表(SysPageConstant)表服务接口
 *
 * @author zhangbozhi
 * @since 2024-11-01 17:11:47
 */
public interface SysPageConstantService {
    
    /**
     * 通过ID查询单条数据
     *
     * @param constantKey 主键
     * @return 实例对象
     */
    SysPageConstant queryById(String constantKey);
    
    /**
     * @return 查询结果
     */
    List<String> selectListCabinetAppVersion();
    
    /**
     * 新增数据
     *
     * @param sysPageConstant 实例对象
     * @return 实例对象
     */
    SysPageConstant insert(SysPageConstant sysPageConstant);
    
    /**
     * 修改数据
     *
     * @param sysPageConstant 实例对象
     * @return 实例对象
     */
    int updateCabinetAppVersion(SysPageConstant sysPageConstant);
    
    
}
