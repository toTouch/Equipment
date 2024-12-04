package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.BatchPurchaseOrder;
import java.util.List;

/**
 * (BatchPurchaseOrder)表服务接口
 *
 * @author zhangbozhi
 * @since 2024-12-03 16:29:46
 */
public interface BatchPurchaseOrderService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    BatchPurchaseOrder queryById(Integer id);

    /**
     * 分页查询
     *
     * @param batchPurchaseOrder 筛选条件
     * @return 查询结果
     */
    List<BatchPurchaseOrder> listByLimit(BatchPurchaseOrder batchPurchaseOrder, Long offset, Long size);

    /**
     * 分页count
     *
     * @param batchPurchaseOrder 筛选条件
     * @return 查询结果
     */
    Long count(BatchPurchaseOrder batchPurchaseOrder);

    /**
     * 新增数据
     *
     * @param batchPurchaseOrder 实例对象
     * @return 实例对象
     */
    BatchPurchaseOrder insert(BatchPurchaseOrder batchPurchaseOrder);

    /**
     * 修改数据
     *
     * @param batchPurchaseOrder 实例对象
     * @return 实例对象
     */
    BatchPurchaseOrder update(BatchPurchaseOrder batchPurchaseOrder);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean removeById(Integer id);

}
