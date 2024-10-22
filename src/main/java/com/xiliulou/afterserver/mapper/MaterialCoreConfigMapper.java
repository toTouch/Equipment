package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.MaterialCoreConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 物料核心配置(MaterialCoreConfig)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-21 19:03:03
 */
public interface MaterialCoreConfigMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialCoreConfig queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param materialCoreConfig 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<MaterialCoreConfig> queryAllByLimit(MaterialCoreConfig materialCoreConfig, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param materialCoreConfig 查询条件
     * @return 总行数
     */
    long count(MaterialCoreConfig materialCoreConfig);

    /**
     * 新增数据
     *
     * @param materialCoreConfig 实例对象
     * @return 影响行数
     */
    int insert(MaterialCoreConfig materialCoreConfig);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialCoreConfig> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<MaterialCoreConfig> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialCoreConfig> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<MaterialCoreConfig> entities);

    /**
     * 修改数据
     *
     * @param materialCoreConfig 实例对象
     * @return 影响行数
     */
    int update(MaterialCoreConfig materialCoreConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);
    
}

