package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.MaterialOperation;
import com.xiliulou.afterserver.service.MaterialOperationService;
import com.xiliulou.afterserver.service.impl.MaterialDelRecordServiceImpl;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private MaterialDelRecordServiceImpl materialDelRecordService;
    
    /**
     * 分页查询
     *
     * @param materialOperation 筛选条件
     * @param offset            查询起始位置
     * @param size              查询条数
     * @return 查询结果
     */
    @GetMapping
    public R queryByPage(MaterialOperation materialOperation, Long offset, Long size) {
        return R.ok(this.materialOperationService.listByLimit(materialOperation, offset, size));
    }
    @GetMapping("/delete")
    public R queryDelListByPage(Long offset, Long size) {
        return R.ok(materialDelRecordService.listByLimit(null,offset, size));
    }
    
    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public R<MaterialOperation> queryById(@PathVariable("id") Integer id) {
        return R.ok(this.materialOperationService.queryById(id));
    }
    
   
    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public R<Boolean> deleteById(Integer id) {
        return R.ok(this.materialOperationService.deleteById(id));
    }
    
}

