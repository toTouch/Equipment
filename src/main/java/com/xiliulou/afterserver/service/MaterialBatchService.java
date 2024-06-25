package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.Material;
import com.xiliulou.afterserver.entity.MaterialBatch;
import com.xiliulou.afterserver.util.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * (MaterialBatch)表服务接口
 *
 * @author zhangbozhi
 * @since 2024-06-19 15:06:15
 */
public interface MaterialBatchService {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialBatch queryById(Integer id);
    
    /**
     * 分页查询
     *
     * @param materialBatch 筛选条件
     * @return 查询结果
     */
    List<MaterialBatch> listByLimit(MaterialBatch materialBatch, Long offset, Long size);
    
    /**
     * 分页count
     *
     * @param materialBatch 筛选条件
     * @return 查询结果
     */
    Long count(MaterialBatch materialBatch);
    
    /**
     * 新增数据
     *
     * @param materialBatch 实例对象
     * @return 实例对象
     */
    R insert(MaterialBatch materialBatch);
    
    /**
     * 修改数据
     *
     * @param materialBatch 实例对象
     * @return 实例对象
     */
    R update(MaterialBatch materialBatch);
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    R<Object> deleteById(Long id);
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean removeById(Integer id);
    
    R materialBatchExportExcel(MaterialBatch materialBatch, HttpServletResponse response);
    
    R materialExportUpload(List<Material> material, Long materialBatchId);
    
    List<MaterialBatch> queryByNos(Set<String> strings);
    
    Integer updateByMaterialBatchs(List<MaterialBatch> materialBatchesQuery);
}
