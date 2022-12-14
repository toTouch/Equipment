package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.WorkOrderParts;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (WorkOrderParts)表数据库访问层
 *
 * @author Hardy
 * @since 2022-12-13 15:50:11
 */
public interface WorkOrderPartsMapper  extends BaseMapper<WorkOrderParts>{

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    WorkOrderParts queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<WorkOrderParts> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param workOrderParts 实例对象
     * @return 对象列表
     */
    List<WorkOrderParts> queryAll(WorkOrderParts workOrderParts);

    /**
     * 新增数据
     *
     * @param workOrderParts 实例对象
     * @return 影响行数
     */
    int insertOne(WorkOrderParts workOrderParts);

    /**
     * 修改数据
     *
     * @param workOrderParts 实例对象
     * @return 影响行数
     */
    int update(WorkOrderParts workOrderParts);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    List<WorkOrderParts> queryByWorkOrderIdAndServerId(@Param("workOrderId")Long workOrderId, @Param("serverId")Long serverId);

    Integer deleteByOidAndServerId(@Param("oid")Long oid, @Param("sid")Long sid);
}
