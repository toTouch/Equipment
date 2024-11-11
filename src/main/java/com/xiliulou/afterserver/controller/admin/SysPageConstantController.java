package com.xiliulou.afterserver.controller.admin;

import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.constant.SysPageConstants;
import com.xiliulou.afterserver.entity.SysPageConstant;
import com.xiliulou.afterserver.service.SysPageConstantService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SysPageConstantQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统页面常量表(SysPageConstant)表控制层
 * <br/> 页面上的单个配置写在这里 如 压测柜机版本校验
 *
 * @author zhangbozhi
 * @since 2024-11-01 17:11:47
 */
@RestController
@RequestMapping("/admin/sysPageConstant")
public class SysPageConstantController {
    
    /**
     * 服务对象
     */
    @Resource
    private SysPageConstantService sysPageConstantService;
    
    /**
     * 分页查询 压测柜机版本
     *
     * @return 查询结果
     */
    @GetMapping
    public R queryByPage() {
        return R.ok(this.sysPageConstantService.selectListCabinetAppVersion());
    }
    
    
    /**
     * 编辑数据
     *
     * @param sysPageConstantQuery 实体
     * @return 编辑结果
     */
    @PutMapping
    public R edit(@RequestBody SysPageConstantQuery sysPageConstantQuery) {
        List<String> cabinetAppVersions = sysPageConstantQuery.getCabinetAppVersions();
        if (cabinetAppVersions == null) {
            return R.failMsg("参数错误");
        }
        if (cabinetAppVersions.size() > 3) {
            return R.failMsg("最多可配置3个版本");
        }
        for (String cabinetAppVersion : cabinetAppVersions) {
            // 长度大于 10
            if (cabinetAppVersion.length() > 8) {
                return R.failMsg("版本号长度不能超过8位");
            }
        }
        SysPageConstant sysPageConstant = new SysPageConstant();
        sysPageConstant.setConstantKey(SysPageConstants.CABINET_APP_VERSION);
        sysPageConstant.setConstantType(SysPageConstants.CABINET_APP_VERSION);
        sysPageConstant.setConstantName(JSON.toJSONString(cabinetAppVersions));
        return R.ok(this.sysPageConstantService.updateCabinetAppVersion(sysPageConstant));
    }
    
    
}

