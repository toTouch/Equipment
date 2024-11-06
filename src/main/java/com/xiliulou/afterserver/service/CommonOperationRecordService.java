package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.CommonOperationRecord;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * (CommonOperationRecord)表服务接口
 *
 * @author zhangbozhi
 * @since 2024-11-06 17:21:09
 */
public interface CommonOperationRecordService {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CommonOperationRecord queryById(Integer id);
    
    /**
     * 分页查询
     *
     * @param commonOperationRecord 筛选条件
     * @return 查询结果
     */
    List<CommonOperationRecord> listByLimit(CommonOperationRecord commonOperationRecord, Long offset, Long size);
    
    /**
     * 分页count
     *
     * @param commonOperationRecord 筛选条件
     * @return 查询结果
     */
    Long count(CommonOperationRecord commonOperationRecord);
    
    /**
     * 新增数据
     *
     * @param commonOperationRecord 实例对象
     * @return 实例对象
     */
    CommonOperationRecord insert(CommonOperationRecord commonOperationRecord);
    
    /**
     * 修改数据
     *
     * @param commonOperationRecord 实例对象
     * @return 实例对象
     */
    CommonOperationRecord update(CommonOperationRecord commonOperationRecord);
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean removeById(Integer id);
    
    void exportExcel(CommonOperationRecord commonOperationRecord, HttpServletResponse response);
}
