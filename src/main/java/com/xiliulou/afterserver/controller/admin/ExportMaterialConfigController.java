package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.ExportMaterialConfig;
import com.xiliulou.afterserver.service.ExportMaterialConfigService;
import com.xiliulou.afterserver.util.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.annotation.Resource;

/**
 * 物料导出配置表(ExportMaterialConfig)表控制层
 *
 * @author zhangbozhi
 * @since 2024-08-30 11:21:06
 */
@RestController
@RequestMapping("admin/exportMaterialConfig")
public class ExportMaterialConfigController {
    
    @Resource
    private ExportMaterialConfigService exportMaterialConfigService;
    
    /**
     * 分页查询
     *
     * @param exportMaterialConfig 筛选条件
     * @param offset               查询起始位置
     * @param size                 查询条数
     * @return 查询结果
     */
    @GetMapping
    public R<List<ExportMaterialConfig>> queryByPage(ExportMaterialConfig exportMaterialConfig, Long offset, Long size) {
        if (offset == null) {
            offset = 0L;
        }
        if (size == null) {
            size = 30L;
        }
        return R.ok(this.exportMaterialConfigService.listByLimit(exportMaterialConfig, offset, size));
    }
    
    /**
     * 新增数据
     *
     * @param exportMaterialConfigs 实体
     * @return 新增结果
     */
//    @PostMapping
    public R add(@RequestBody List<ExportMaterialConfig>  exportMaterialConfigs) {
        return this.exportMaterialConfigService.insert(exportMaterialConfigs);
    }
    
    /**
     * 编辑数据
     *
     * @param exportMaterialConfigs 实体
     * @return 编辑结果
     */
    @PutMapping
    public R
    edit(@RequestBody List<ExportMaterialConfig>  exportMaterialConfigs) {
        return this.exportMaterialConfigService.update(exportMaterialConfigs);
    }
    
    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public R<Boolean> deleteById(Integer id) {
        return R.ok(this.exportMaterialConfigService.deleteById(id));
    }
    
}

