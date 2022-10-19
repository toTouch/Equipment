package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.Batch;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (Batch)表数据库访问层
 *
 * @author Hardy
 * @since 2021-08-16 15:50:17
 */
public interface BatchMapper extends BaseMapper<Batch> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Batch queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Batch> queryAllByLimit(@Param("batchNo") String batchNo, @Param("offset") int offset, @Param("limit") int limit, @Param("modelId") Long modelId,  @Param("supplierId") Long supplierId, @Param("buyType")Integer buyType);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param batch 实例对象
     * @return 对象列表
     */
    List<Batch> queryAll(Batch batch);

    /**
     * 新增数据
     *
     * @param batch 实例对象
     * @return 影响行数
     */
    int insertOne(Batch batch);

    /**
     * 修改数据
     *
     * @param batch 实例对象
     * @return 影响行数
     */
    int update(Batch batch);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    Long count(@Param("batchNo") String batchNo, @Param("modelId")Long modelId, @Param("supplierId")Long supplierId, @Param("buyType")Integer buyType);
}
