package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.MaterialCoreConfig;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.MaterialCoreConfigService;
import com.xiliulou.afterserver.service.MaterialTraceabilityService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.MaterialTraceabilityQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 物料追溯表(MaterialTraceability)表控制层
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */
@RestController
@RequestMapping("admin/materialTraceability")
public class JsonAdminMaterialTraceabilityController {
    
    /**
     * 服务对象
     */
    @Resource
    private MaterialTraceabilityService materialTraceabilityService;
    
    @Resource
    private MaterialCoreConfigService materialCoreConfigService;
    
    /**
     * 校验柜机sn
     *
     * @param sn
     * @return
     */
    @GetMapping("/checkSn")
    public R checkSn(@RequestParam("sn") String sn) {
        return this.materialTraceabilityService.checkSn(sn);
    }
    
    /**
     * 新增数据
     *
     * @param materialTraceability 实体
     * @return 新增结果
     */
    @PostMapping("/save")
    public R add(@RequestBody MaterialTraceabilityQuery materialTraceability) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return this.materialTraceabilityService.insert(materialTraceability);
    }
    
    /**
     * 分页查询
     *
     * @param materialTraceability 筛选条件
     * @return 查询结果
     */
    @GetMapping("/page")
    public R queryByPage(MaterialTraceabilityQuery materialTraceability, @RequestParam("offset") Long offset, @RequestParam("size") Long size) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return R.ok(this.materialTraceabilityService.queryByPage(materialTraceability, offset, size));
    }
    
    /**
     * 分页count
     *
     * @param materialTraceability 筛选条件
     * @return 查询结果
     */
    @GetMapping("/count")
    public R queryByPageCount(MaterialTraceabilityQuery materialTraceability) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return R.ok(this.materialTraceabilityService.queryByPageCount(materialTraceability));
    }
    
    /**
     * 物料解绑
     *
     * @param materialTraceability 实体
     * @return 编辑结果
     */
    @PutMapping("/Unbund")
    public R materialUnbundling(MaterialTraceabilityQuery materialTraceability) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return this.materialTraceabilityService.materialUnbundling(materialTraceability);
    }
    
    /**
     * 导出物料数据
     */
    @GetMapping("/exportExcel")
    public void exportMaterialData(MaterialTraceabilityQuery materialTraceability, HttpServletResponse response) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return;
        }
        this.materialTraceabilityService.exportExcel(materialTraceability, response);
    }
    
    /**
     * 编辑数据
     *
     * @param materialTraceability 实体
     * @return 编辑结果
     */
    @PutMapping
    public R edit(@RequestBody MaterialTraceabilityQuery materialTraceability) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return this.materialTraceabilityService.update(materialTraceability);
    }
    
    /**
     * 更新物料核心配置
     */
    @PutMapping("/Config")
    public R updateConfig(String substance) {
        MaterialCoreConfig materialCoreConfig = new MaterialCoreConfig();
        materialCoreConfig.setMaterialCoreConfig(substance);
        materialCoreConfig.setId(1);
        return R.ok(this.materialCoreConfigService.update(materialCoreConfig));
    }
    
    /**
     * 添加物料核心配置
     */
    @PostMapping("/Config")
    public R addConfig(String substance) {
        MaterialCoreConfig materialCoreConfig = new MaterialCoreConfig();
        materialCoreConfig.setMaterialCoreConfig(substance);
        materialCoreConfig.setId(1);
        return R.ok(this.materialCoreConfigService.insert(materialCoreConfig));
    }
    
    /**
     * 查询物料核心配置
     */
    @GetMapping("/Config")
    public R queryConfig() {
        return R.ok(this.materialCoreConfigService.queryById(1));
    }
    
    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    // @DeleteMapping
    public R deleteById(Long id) {
        return R.ok(this.materialTraceabilityService.deleteById(id));
    }
    
}

