package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.MaterialCoreConfig;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.MaterialCoreConfigService;
import com.xiliulou.afterserver.service.MaterialTraceabilityService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.MaterialQuery;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.PUT;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 物料追溯表(Material)表控制层
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */
@RestController
@RequestMapping("admin/material")
public class JsonAdminMaterialTraceabilityController {
    
    /**
     * 服务对象
     */
    @Resource
    private MaterialTraceabilityService materialTraceabilityService;
    
    @Resource
    private MaterialCoreConfigService materialCoreConfigService;
    
    /**
     * 校验柜机sn pda
     *
     * @param sn
     * @return
     */
    @GetMapping("/checkSn")
    public R checkSn(@RequestParam("sn") String sn) {
        if (StringUtils.isEmpty(sn)) {
            return R.failMsg("sn不能为空");
        }
        return this.materialTraceabilityService.checkSn(sn);
    }
    
    /**
     * 新增数据 pda
     *
     * @param materialTraceability 实体
     * @return 新增结果
     */
    @PostMapping("/save")
    public R add(@RequestBody MaterialQuery materialTraceability) throws Exception {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return this.materialTraceabilityService.insert(materialTraceability);
    }
    
    /**
     * 物料解绑 pda
     *
     * @param materialTraceability 实体
     * @return 编辑结果
     */
    @PutMapping("/unbund")
    public R materialUnbundling(@RequestBody MaterialQuery materialTraceability) throws Exception {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return this.materialTraceabilityService.materialUnbundling(materialTraceability);
    }
    
    @GetMapping("/pda/page")
    public R queryByPagePDA(MaterialQuery materialTraceability, @RequestParam("offset") Long offset, @RequestParam("size") Long size) {
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
    @GetMapping("/pda/count")
    public R queryByPageCountPDA(MaterialQuery materialTraceability) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        return R.ok(this.materialTraceabilityService.queryByPageCount(materialTraceability));
    }
    
    /**
     * 分页查询
     *
     * @param materialTraceability 筛选条件
     * @return 查询结果
     */
    @GetMapping("/page")
    public R queryByPage(MaterialQuery materialTraceability, @RequestParam("offset") Long offset, @RequestParam("size") Long size) {
        return R.ok(this.materialTraceabilityService.queryByPage(materialTraceability, offset, size));
    }
    
    /**
     * 分页count
     *
     * @param materialTraceability 筛选条件
     * @return 查询结果
     */
    @GetMapping("/count")
    public R queryByPageCount(MaterialQuery materialTraceability) {
        return R.ok(this.materialTraceabilityService.queryByPageCount(materialTraceability));
    }
    
    /**
     * 导出物料数据
     */
    @GetMapping("/exportExcel")
    public R exportMaterialData(MaterialQuery materialTraceability, HttpServletResponse response) {
        return this.materialTraceabilityService.exportExcel(materialTraceability, response);
    }
    
    /**
     * 编辑数据
     *
     * @param materialTraceability 实体
     * @return 编辑结果
     */
    @PutMapping
    public R edit(@RequestBody MaterialQuery materialTraceability) throws Exception {
        return this.materialTraceabilityService.update(materialTraceability);
    }
    
    /**
     * 更新物料核心配置
     */
    @PutMapping("/config")
    public R updateConfig(@RequestBody MaterialCoreConfig materialConfig) {
        materialConfig.setId(1);
        return this.materialCoreConfigService.update(materialConfig);
    }
    
    /**
     * 添加物料核心配置
     */
    @PostMapping("/config")
    public R addConfig(@RequestBody MaterialCoreConfig materialConfig) {
        return this.materialCoreConfigService.insert(materialConfig);
    }
    
    /**
     * 查询物料核心配置
     */
    @GetMapping("/config")
    public R queryConfig() {
        return R.ok(this.materialCoreConfigService.queryById(1));
    }
    
    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("/delete")
    public R deleteById( @RequestParam(required = false) List<Long> ids) throws Exception {
        return this.materialTraceabilityService.deleteByIds(ids);
    }
    
    /**
     * 批量改换物料状态
     */
    @PutMapping("/changeMaterialState")
    public R changeMaterialState(@RequestBody List<Long> ids, Integer confirm, Integer status, String remark) throws Exception {
        return this.materialTraceabilityService.changeMaterialState(ids, status, confirm,remark);
    }
    
}

