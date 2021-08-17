package com.xiliulou.afterserver.mapper;

import java.util.List;

import com.xiliulou.afterserver.entity.PointProductBind;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (PointProductBind)表数据库访问层
 *
 * @author Hardy
 * @since 2021-08-17 10:25:55
 */
public interface PointProductBindMapper extends BaseMapper<PointProductBind> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    PointProductBind queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<PointProductBind> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param pointProductBind 实例对象
     * @return 对象列表
     */
    List<PointProductBind> queryAll(PointProductBind pointProductBind);

    /**
     * 新增数据
     *
     * @param pointProductBind 实例对象
     * @return 影响行数
     */
    int insertOne(PointProductBind pointProductBind);

    /**
     * 修改数据
     *
     * @param pointProductBind 实例对象
     * @return 影响行数
     */
    int update(PointProductBind pointProductBind);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}
