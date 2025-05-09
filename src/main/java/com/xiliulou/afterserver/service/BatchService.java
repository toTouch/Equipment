package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.util.R;

import java.util.ArrayList;
import java.util.List;

/**
 * (Batch)表服务接口
 *
 * @author Hardy
 * @since 2021-08-16 15:50:17
 */
public interface BatchService {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    Batch queryByIdFromDB(Long id);

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    Batch queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param productType
     * @param offset      查询起始位置
     * @param limit       查询条数
     * @param notShipped
     * @param remarks
     * @return 对象列表
     */
    List<Batch> queryAllByLimit(String batchNo, String productType, int offset, int limit, Long modelId, Long supplierId, Integer notShipped, String remarks);

    /**
     * 新增数据
     *
     * @param batch 实例对象
     * @return 实例对象
     */
    Batch insert(Batch batch);

    /**
     * 修改数据
     *
     * @param batch 实例对象
     * @return 实例对象
     */
    Integer update(Batch batch);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    Long count(String batchNo, String productType, Long modelId, Long supplierId, Integer notShipped, String remarks);

    List<Batch> queryByName(String batch);

    R saveBatch(Batch batch);

    R delOne(Long id);

    R queryByfactory(Long offset, Long size, Integer notShipped, String batchNo);
    Batch getByNameAndModeId(String batchNo, Long modelId);
    
    List<Batch> ListBatchByProductId(Long id);
    
    Integer batchUpdateById(ArrayList<Batch> batches);
}
