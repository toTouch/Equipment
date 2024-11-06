package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.SysPageConstant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统页面常量表(SysPageConstant)表数据库访问层
 *
 * @author zhangbozhi
 * @since 2024-11-01 17:11:47
 */
public interface SysPageConstantMapper {
    
    /**
     * 通过ID查询单条数据
     *
     * @param constantKey 主键
     * @return 实例对象
     */
    SysPageConstant selectById(String constantKey);
    
    /**
     * 查询指定行数据
     *
     * @param sysPageConstant 查询条件
     * @return 对象列表
     */
    List<SysPageConstant> selectList(@Param("entity") SysPageConstant sysPageConstant, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 统计总行数
     *
     * @param sysPageConstant 查询条件
     * @return 总行数
     */
    long count(SysPageConstant sysPageConstant);
    
    /**
     * 新增数据
     *
     * @param sysPageConstant 实例对象
     * @return 影响行数
     */
    int insert(SysPageConstant sysPageConstant);
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysPageConstant> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SysPageConstant> entities);
    
    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysPageConstant> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<SysPageConstant> entities);
    
    /**
     * 修改数据
     *
     * @param sysPageConstant 实例对象
     * @param constantKey
     * @param constantType
     * @return 影响行数
     */
    int updateByconstantTypeAndName(@Param("sysPageConstant") SysPageConstant sysPageConstant, @Param("constantKey") String constantKey, @Param("constantType") String constantType);
    
    /**
     * 通过主键删除数据
     *
     * @param constantKey 主键
     * @return 影响行数
     */
    int deleteById(String constantKey);
    
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param constantKey 主键
     * @return 影响行数
     */
    int removeById(String constantKey);
    
    SysPageConstant selectOne(SysPageConstant sysPageConstant);
}

