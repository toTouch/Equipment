package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.WorkOrderParts;
import java.util.List;

/**
 * (WorkOrderParts)表服务接口
 *
 * @author Hardy
 * @since 2022-12-13 15:50:11
 */
public interface WorkOrderPartsService {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    WorkOrderParts queryByIdFromDB(Long id);
    
      /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    WorkOrderParts queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<WorkOrderParts> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param workOrderParts 实例对象
     * @return 实例对象
     */
    WorkOrderParts insert(WorkOrderParts workOrderParts);

    /**
     * 修改数据
     *
     * @param workOrderParts 实例对象
     * @return 实例对象
     */
    Integer update(WorkOrderParts workOrderParts);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    List<WorkOrderParts> queryByWorkOrderIdAndServerId(Long workOrderId, Long serverId);

    Integer deleteByOidAndServerId(Long id, Long id1);
}
