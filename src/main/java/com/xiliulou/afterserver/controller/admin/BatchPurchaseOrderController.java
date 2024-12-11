package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.BatchPurchaseOrder;
import com.xiliulou.afterserver.service.BatchPurchaseOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import javax.annotation.Resource;

/**
 * (BatchPurchaseOrder)表控制层
 *
 * @author zhangbozhi
 * @since 2024-12-03 16:29:46
 */
@RestController
@RequestMapping("/batchPurchaseOrder")
public class BatchPurchaseOrderController {
    /**
     * 服务对象
     */
    @Resource
    private BatchPurchaseOrderService batchPurchaseOrderService;

    /**
     * 分页查询
     *
     * @param batchPurchaseOrder 筛选条件
     * @param offset 查询起始位置
     * @param size   查询条数
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<List<BatchPurchaseOrder>> queryByPage(BatchPurchaseOrder batchPurchaseOrder, Long offset, Long size) {
        return ResponseEntity.ok(this.batchPurchaseOrderService.listByLimit(batchPurchaseOrder, offset, size));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<BatchPurchaseOrder> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.batchPurchaseOrderService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param batchPurchaseOrder 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<BatchPurchaseOrder> add(BatchPurchaseOrder batchPurchaseOrder) {
        return ResponseEntity.ok(this.batchPurchaseOrderService.insert(batchPurchaseOrder));
    }

    /**
     * 编辑数据
     *
     * @param batchPurchaseOrder 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<BatchPurchaseOrder> edit(BatchPurchaseOrder batchPurchaseOrder) {
        return ResponseEntity.ok(this.batchPurchaseOrderService.update(batchPurchaseOrder));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.batchPurchaseOrderService.deleteById(id));
    }

}

