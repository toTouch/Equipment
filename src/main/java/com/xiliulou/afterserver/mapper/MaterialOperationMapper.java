package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.MaterialOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (MaterialOperation)表数据库访问层
 *
 * @author zhangbozhi
 * @since 2024-06-23 22:21:41
 */
public interface MaterialOperationMapper {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialOperation selectById(Integer id);
    
    /**
     * 查询指定行数据
     *
     * @param materialOperation 查询条件
     * @return 对象列表
     */
    List<MaterialOperation> selectPage(@Param("entity") MaterialOperation materialOperation, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 统计总行数
     *
     * @param materialOperation 查询条件
     * @return 总行数
     */
    long count(MaterialOperation materialOperation);
    
    /**
     * 新增数据
     *
     * @param materialOperation 实例对象
     * @return 影响行数
     */
    int insert(MaterialOperation materialOperation);
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialOperation> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<MaterialOperation> entities);
    
    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<MaterialOperation> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<MaterialOperation> entities);
    
    /**
     * 修改数据
     *
     * @param materialOperation 实例对象
     * @return 影响行数
     */
    int update(MaterialOperation materialOperation);
    
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

