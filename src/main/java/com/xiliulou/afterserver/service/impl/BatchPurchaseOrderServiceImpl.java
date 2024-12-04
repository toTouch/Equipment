package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.BatchPurchaseOrder;
import com.xiliulou.afterserver.mapper.BatchPurchaseOrderMapper;
import com.xiliulou.afterserver.service.BatchPurchaseOrderService;
import org.springframework.stereotype.Service;
import java.util.List;

import javax.annotation.Resource;

/**
 * (BatchPurchaseOrder)表服务实现类
 *
 * @author zhangbozhi
 * @since 2024-12-03 16:29:46
 */
@Service("batchPurchaseOrderService")
public class BatchPurchaseOrderServiceImpl implements BatchPurchaseOrderService {
    @Resource
    private BatchPurchaseOrderMapper batchPurchaseOrderMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public BatchPurchaseOrder queryById(Integer id) {
        return this.batchPurchaseOrderMapper.selectById(id);
    }

    /**
     * 分页查询
     *
     * @param batchPurchaseOrder 筛选条件
     * @param offset 查询起始位置
     * @param size   查询条数
     * @return 查询结果
     */
    @Override
    public List<BatchPurchaseOrder> listByLimit(BatchPurchaseOrder batchPurchaseOrder, Long offset, Long size) {
        return this.batchPurchaseOrderMapper.selectPage(batchPurchaseOrder, offset, size);
    }

    /**
     * 分页count
     *
     * @param batchPurchaseOrder 筛选条件
     * @return 查询结果
     */
    @Override
    public Long count(BatchPurchaseOrder batchPurchaseOrder) {
        return this.batchPurchaseOrderMapper.count(batchPurchaseOrder);
    }

    /**
     * 新增数据
     *
     * @param batchPurchaseOrder 实例对象
     * @return 实例对象
     */
    @Override
    public BatchPurchaseOrder insert(BatchPurchaseOrder batchPurchaseOrder) {
        this.batchPurchaseOrderMapper.insert(batchPurchaseOrder);
        return batchPurchaseOrder;
    }

    /**
     * 修改数据
     *
     * @param batchPurchaseOrder 实例对象
     * @return 实例对象
     */
    @Override
    public BatchPurchaseOrder update(BatchPurchaseOrder batchPurchaseOrder) {
        this.batchPurchaseOrderMapper.updateById(batchPurchaseOrder);
        return this.queryById(batchPurchaseOrder.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.batchPurchaseOrderMapper.deleteById(id) > 0;
    }


     /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean removeById(Integer id) {
        return this.batchPurchaseOrderMapper.removeById(id) > 0;
    }
}
