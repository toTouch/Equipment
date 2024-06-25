package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.MaterialOperation;

import java.util.List;

/**
 * (MaterialOperation)表服务接口
 *
 * @author zhangbozhi
 * @since 2024-06-23 22:21:42
 */
public interface MaterialOperationService {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialOperation queryById(Integer id);
    
    /**
     * 分页查询
     *
     * @param materialOperation 筛选条件
     * @return 查询结果
     */
    List<MaterialOperation> listByLimit(MaterialOperation materialOperation, Long offset, Long size);
    
    /**
     * 分页count
     *
     * @param materialOperation 筛选条件
     * @return 查询结果
     */
    Long count(MaterialOperation materialOperation);
    
    /**
     * 新增数据
     *
     * @param materialOperation 实例对象
     * @return 实例对象
     */
    MaterialOperation insert(MaterialOperation materialOperation);
    
    /**
     * 修改数据
     *
     * @param materialOperation 实例对象
     * @return 实例对象
     */
    MaterialOperation update(MaterialOperation materialOperation);
    
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
    
}
