package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.ExportMaterialConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料导出配置表(ExportMaterialConfig)表数据库访问层
 *
 * @author zhangbozhi
 * @since 2024-08-30 11:21:06
 */
public interface ExportMaterialConfigMapper {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ExportMaterialConfig selectById(Integer id);
    
    /**
     * 查询指定行数据
     *
     * @param exportMaterialConfig 查询条件
     * @return 对象列表
     */
    List<ExportMaterialConfig> selectPage(@Param("entity") ExportMaterialConfig exportMaterialConfig, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 统计总行数
     *
     * @param exportMaterialConfig 查询条件
     * @return 总行数
     */
    long count(ExportMaterialConfig exportMaterialConfig);
    
    /**
     * 新增数据
     *
     * @param exportMaterialConfig 实例对象
     * @return 影响行数
     */
    int insert(ExportMaterialConfig exportMaterialConfig);
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ExportMaterialConfig> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ExportMaterialConfig> entities);
    
    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ExportMaterialConfig> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<ExportMaterialConfig> entities);
    
    /**
     * 修改数据
     *
     * @param exportMaterialConfig 实例对象
     * @return 影响行数
     */
    int update(ExportMaterialConfig exportMaterialConfig);
    
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
    
    int deleteAll();
}

