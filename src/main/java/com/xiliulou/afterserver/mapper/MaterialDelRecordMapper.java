package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.MaterialDelRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料删除追溯表(MaterialDelRecord)表数据库访问层
 *
 * @author zhangbozhi
 * @since 2024-06-27 20:44:29
 */
public interface MaterialDelRecordMapper {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialDelRecord selectById(Long id);
    
    /**
     * 查询指定行数据
     *
     * @param materialDelRecord 查询条件
     * @return 对象列表
     */
    List<MaterialDelRecord> selectPage(@Param("entity") MaterialDelRecord materialDelRecord, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 统计总行数
     *
     * @param materialDelRecord 查询条件
     * @return 总行数
     */
    long count(MaterialDelRecord materialDelRecord);
    
    /**
     * 新增数据
     *
     * @param materialDelRecord 实例对象
     * @return 影响行数
     */
    int insert(MaterialDelRecord materialDelRecord);
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialDelRecord> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<MaterialDelRecord> entities);
    
    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialDelRecord> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<MaterialDelRecord> entities);
    
    /**
     * 修改数据
     *
     * @param materialDelRecord 实例对象
     * @return 影响行数
     */
    int update(MaterialDelRecord materialDelRecord);
    
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
    int removeById(Long id);
}

