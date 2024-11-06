package com.xiliulou.afterserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.constant.SysPageConstants;
import com.xiliulou.afterserver.entity.SysPageConstant;
import com.xiliulou.afterserver.mapper.SysPageConstantMapper;
import com.xiliulou.afterserver.service.SysPageConstantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统页面常量表(SysPageConstant)表服务实现类
 *
 * @author zhangbozhi
 * @since 2024-11-01 17:11:48
 */
@Service("sysPageConstantService")
public class SysPageConstantServiceImpl implements SysPageConstantService {
    
    @Resource
    private SysPageConstantMapper sysPageConstantMapper;
    
    /**
     * 通过ID查询单条数据
     *
     * @param constantKey 主键
     * @return 实例对象
     */
    @Override
    public SysPageConstant queryById(String constantKey) {
        return this.sysPageConstantMapper.selectById(constantKey);
    }
    
    /**
     * @return 查询结果
     */
    @Override
    public List<String> selectListCabinetAppVersion() {
        SysPageConstant sysPageConstant = new SysPageConstant();
        sysPageConstant.setConstantKey(SysPageConstants.CABINET_APP_VERSION);
        sysPageConstant.setConstantType(SysPageConstants.CABINET_APP_VERSION);
        SysPageConstant appVersions = this.sysPageConstantMapper.selectOne(sysPageConstant);
        
        if (appVersions == null) {
            return List.of();
        }
        
        String constantName = appVersions.getConstantName();
        try {
            return JSON.parseArray(constantName, String.class);
        } catch (Exception e) {
            return List.of();
        }
        
    }
    
    /**
     * 修改数据
     *
     * @param sysPageConstant 实例对象
     * @return 实例对象
     */
    @Override
    public int updateCabinetAppVersion(SysPageConstant sysPageConstant) {
        return this.sysPageConstantMapper.updateByconstantTypeAndName(sysPageConstant, SysPageConstants.CABINET_APP_VERSION, SysPageConstants.CABINET_APP_VERSION);
    }
    
    
    /**
     * 新增数据
     *
     * @param sysPageConstant 实例对象
     * @return 实例对象
     */
    @Override
    public SysPageConstant insert(SysPageConstant sysPageConstant) {
        this.sysPageConstantMapper.insert(sysPageConstant);
        return sysPageConstant;
    }
    
    
}
