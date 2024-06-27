package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.MaterialBatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * (MaterialBatch)表数据库访问层
 *
 * @author zhangbozhi
 * @since 2024-06-19 15:06:15
 */
public interface MaterialBatchMapper {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialBatch selectById(Integer id);
    
    /**
     * 查询指定行数据
     *
     * @param materialBatch 查询条件
     * @return 对象列表
     */
    List<MaterialBatch> selectPage(@Param("entity") MaterialBatch materialBatch, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 统计总行数
     *
     * @param materialBatch 查询条件
     * @return 总行数
     */
    long count(@Param("entity") MaterialBatch materialBatch);
    
    /**
     * 新增数据
     *
     * @param materialBatch 实例对象
     * @return 影响行数
     */
    int insert(MaterialBatch materialBatch);
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialBatch> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<MaterialBatch> entities);
    
    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialBatch> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<MaterialBatch> entities);
    
    /**
     * 修改数据
     *
     * @param materialBatch 实例对象
     * @return 影响行数
     */
    int update(MaterialBatch materialBatch);
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);
    
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int removeById(Integer id);
    
    MaterialBatch existsByPartsId(Long id);
    
    int deleteByBatcherId(Long batchId);
    
    List<MaterialBatch> queryByNos(@Param("nos") Set<String> nos);
    
    Integer updateByMaterialBatchs(List<MaterialBatch> materialBatchesQuery);
    
    MaterialBatch selectByMaterialBatchNo(@Param("materialBatchNo") String materialBatchNo);
    
    MaterialBatch existsByBatchNo(@Param("batchNo") String batchNo);
    
    MaterialBatch selectByPartsId(@Param("partsId") Long partsId);
}

