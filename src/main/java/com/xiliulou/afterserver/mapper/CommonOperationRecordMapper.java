package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.CommonOperationRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (CommonOperationRecord)表数据库访问层
 *
 * @author zhangbozhi
 * @since 2024-11-06 17:21:06
 */
public interface CommonOperationRecordMapper {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CommonOperationRecord selectById(Integer id);
    
    /**
     * 查询指定行数据
     *
     * @param commonOperationRecord 查询条件
     * @return 对象列表
     */
    List<CommonOperationRecord> selectPage(@Param("entity") CommonOperationRecord commonOperationRecord, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 统计总行数
     *
     * @param commonOperationRecord 查询条件
     * @return 总行数
     */
    long count(CommonOperationRecord commonOperationRecord);
    
    /**
     * 新增数据
     *
     * @param commonOperationRecord 实例对象
     * @return 影响行数
     */
    int insert(CommonOperationRecord commonOperationRecord);
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<CommonOperationRecord> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<CommonOperationRecord> entities);
    
    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<CommonOperationRecord> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<CommonOperationRecord> entities);
    
    /**
     * 修改数据
     *
     * @param commonOperationRecord 实例对象
     * @return 影响行数
     */
    int update(CommonOperationRecord commonOperationRecord);
    
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

