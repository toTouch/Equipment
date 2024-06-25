package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.MaterialOperation;
import com.xiliulou.afterserver.service.MaterialOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.annotation.Resource;

/**
 * (MaterialOperation)表控制层
 *
 * @author zhangbozhi
 * @since 2024-06-23 22:21:41
 */
@RestController
@RequestMapping("/admin/materialOperation")
public class MaterialOperationController {
    
    /**
     * 服务对象
     */
    @Resource
    private MaterialOperationService materialOperationService;
    
    /**
     * 分页查询
     *
     * @param materialOperation 筛选条件
     * @param offset            查询起始位置
     * @param size              查询条数
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<List<MaterialOperation>> queryByPage(MaterialOperation materialOperation, Long offset, Long size) {
        return ResponseEntity.ok(this.materialOperationService.listByLimit(materialOperation, offset, size));
    }
    
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaterialOperation> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.materialOperationService.queryById(id));
    }
    
    /**
     * 新增数据
     *
     * @param materialOperation 实体
     * @return 新增结果
     */
    // @PostMapping
    public ResponseEntity<MaterialOperation> add(MaterialOperation materialOperation) {
        return ResponseEntity.ok(this.materialOperationService.insert(materialOperation));
    }
    
    /**
     * 编辑数据
     *
     * @param materialOperation 实体
     * @return 编辑结果
     */
    // @PutMapping
    public ResponseEntity<MaterialOperation> edit(MaterialOperation materialOperation) {
        return ResponseEntity.ok(this.materialOperationService.update(materialOperation));
    }
    
    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.materialOperationService.deleteById(id));
    }
    
}

