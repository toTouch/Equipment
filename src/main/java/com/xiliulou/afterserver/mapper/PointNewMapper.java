package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.PointNew;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (PointNew)表数据库访问层
 *
 * @author Hardy
 * @since 2021-08-17 10:28:43
 */
public interface PointNewMapper extends BaseMapper<PointNew> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNew queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<PointNew> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit, @Param("name") String name,
                                   @Param("cid") Integer cid,
                                   @Param("status") Integer status,
                                   @Param("customerId") Long customerId,
                                   @Param("startTime") Long startTime,
                                   @Param("endTime") Long endTime);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param pointNew 实例对象
     * @return 对象列表
     */
    List<PointNew> queryAll(PointNew pointNew);

    /**
     * 新增数据
     *
     * @param pointNew 实例对象
     * @return 影响行数
     */
    int insertOne(PointNew pointNew);

    /**
     * 修改数据
     *
     * @param pointNew 实例对象
     * @return 影响行数
     */
    int update(PointNew pointNew);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    Integer countPoint(@Param("name") String name,
                       @Param("cid") Integer cid,
                       @Param("status") Integer status,
                       @Param("customerId") Long customerId,
                       @Param("startTime") Long startTime,
                       @Param("endTime") Long endTime);
}
