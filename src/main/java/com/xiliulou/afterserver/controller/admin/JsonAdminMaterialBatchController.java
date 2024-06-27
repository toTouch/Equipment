package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xiliulou.afterserver.entity.MaterialBatch;
import com.xiliulou.afterserver.service.MaterialBatchService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.MaterialQuery;
import com.xiliulou.afterserver.web.query.MaterialUploadQuery;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shaded.org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * (MaterialBatch)表控制层
 *
 * @author zhangbozhi
 * @since 2024-06-19 14:53:27
 */
@RestController
@RequestMapping("admin/materialBatch")
public class JsonAdminMaterialBatchController {
    
    /**
     * 服务对象
     */
    @Resource
    private MaterialBatchService materialBatchService;
    
    /**
     * 分页查询
     *
     * @param materialBatch 筛选条件
     * @param offset        查询起始位置
     * @param size          查询条数
     * @return 查询结果
     */
    @GetMapping("/page")
    public R queryByPage(MaterialBatch materialBatch, Long offset, Long size) {
        return R.ok(this.materialBatchService.listByLimit(materialBatch, offset, size));
    }
    
    
    @GetMapping("/count")
    public R queryCount(MaterialBatch materialBatch) {
        return R.ok(this.materialBatchService.count(materialBatch));
    }
    
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public R queryById(@PathVariable("id") Integer id) {
        return R.ok(this.materialBatchService.queryById(id));
    }
    
    /**
     * 新增物料批次
     *
     * @param materialBatch 实体
     * @return 新增结果
     */
    @PostMapping("/save")
    public R add(@RequestBody MaterialBatch materialBatch) {
        if (StringUtils.isBlank(materialBatch.getMaterialBatchNo())) {
            return R.failMsg("批次号不能为空");
        }
        if (Objects.isNull(materialBatch.getMaterialId())) {
            return R.failMsg("物料编号不能为空");
        }
        if (Objects.isNull(materialBatch.getSupplierId())) {
            return R.failMsg("供应商不能为空");
        }
        
        return this.materialBatchService.insert(materialBatch);
    }
    
    /**
     * 编辑数据
     *
     * @param materialBatch 实体
     * @return 编辑结果
     */
    @PutMapping("/edit")
    public R edit(@RequestBody MaterialBatch materialBatch) {
        if (StringUtils.isBlank(materialBatch.getMaterialBatchNo())) {
            return R.failMsg("批次号不能为空");
        }
        
        return this.materialBatchService.update(materialBatch);
    }
    
    /**
     * 物料状态跳转 TODO
     */
    
    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("/delete")
    public R deleteById(Long id) {
        return this.materialBatchService.deleteById(id);
    }
    
    /**
     * 导出物料批次
     */
    @GetMapping("/exportExcel")
    public R materialBatchExportExcel(MaterialBatch materialBatch, HttpServletResponse response) {
        return this.materialBatchService.materialBatchExportExcel(materialBatch, response);
    }
    
    /**
     * 导入物料批次
     */
    @PostMapping("/admin/supplier/upload")
    public R upload(@RequestBody MaterialUploadQuery materialUploadQuery) throws IOException {
        if (Objects.isNull(materialUploadQuery)) {
            return R.ok();
        }
        List<MaterialQuery> materials = materialUploadQuery.getMaterials();
        long materialBatchId = materialUploadQuery.getMaterialBatchId();
        if (CollectionUtils.isEmpty(materials)) {
            return R.failMsg("文件内容为空");
        }
        if (materials.size() > 3000) {
            return R.failMsg("文件内容不能超过3000条");
        }
        return this.materialBatchService.materialExportUpload(materials, materialBatchId);
    }
}

