package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.BatchPurchaseOrder;
import com.xiliulou.afterserver.entity.Supplier;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (BatchPurchaseOrder)表数据库访问层
 *
 * @author zhangbozhi
 * @since 2024-12-03 16:29:46
 */
public interface BatchPurchaseOrderMapper extends BaseMapper<BatchPurchaseOrder> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    BatchPurchaseOrder selectById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param batchPurchaseOrder 查询条件
     * @return 对象列表
     */
    List<BatchPurchaseOrder> selectPage(@Param("entity") BatchPurchaseOrder batchPurchaseOrder, @Param("offset")Long offset,@Param("size") Long size );

    /**
     * 统计总行数
     *
     * @param batchPurchaseOrder 查询条件
     * @return 总行数
     */
    long count(BatchPurchaseOrder batchPurchaseOrder);

    /**
     * 新增数据
     *
     * @param batchPurchaseOrder 实例对象
     * @return 影响行数
     */
    int insert(BatchPurchaseOrder batchPurchaseOrder);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<BatchPurchaseOrder> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<BatchPurchaseOrder> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<BatchPurchaseOrder> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<BatchPurchaseOrder> entities);

    /**
     * 修改数据
     *
     * @param batchPurchaseOrder 实例对象
     * @return 影响行数
     */
    int updateById(BatchPurchaseOrder batchPurchaseOrder);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);


    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int removeById(Integer id);
}

