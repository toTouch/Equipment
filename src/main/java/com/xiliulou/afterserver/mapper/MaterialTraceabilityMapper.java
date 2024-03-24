package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.MaterialTraceability;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface MaterialTraceabilityMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialTraceability selectById(@Param("id") Long id);

    /**
     * 查询指定行数据
     *
     * @param materialTraceability 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<MaterialTraceability> selectListByLimit(@Param("query") MaterialTraceability materialTraceability, @Param("offset") Long offset, @Param("size") Long size);

    /**
     * 统计总行数
     *
     * @param materialTraceability 查询条件
     * @return 总行数
     */
    long count(MaterialTraceability materialTraceability);

    /**
     * 新增数据
     *
     * @param materialTraceability 实例对象
     * @return 影响行数
     */
    int insert(MaterialTraceability materialTraceability);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialTraceability> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<MaterialTraceability> entities);
    
    /**
     * 修改数据
     *
     * @param materialTraceability 实例对象
     * @return 影响行数
     */
    int update(MaterialTraceability materialTraceability);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    MaterialTraceability selectByParameter(MaterialTraceability materialTraceability);
    
    int materialUnbundling(MaterialTraceability materialTraceability);
}

