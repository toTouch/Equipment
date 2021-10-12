package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.util.R;

import java.util.List;

/**
 * (ProductNew)表服务接口
 *
 * @author Hardy
 * @since 2021-08-17 10:29:14
 */
public interface ProductNewService {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNew queryByIdFromDB(Long id);

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNew queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ProductNew> queryAllByLimit(int offset, int limit,String no,Long modelId,Long startTime,Long endTime, Long pointId);

    /**
     * 新增数据
     *
     * @param productNew 实例对象
     * @return 实例对象
     */
    ProductNew insert(ProductNew productNew);

    /**
     * 修改数据
     *
     * @param productNew 实例对象
     * @return 实例对象
     */
    Integer update(ProductNew productNew);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    R saveAdminProductNew(ProductNew productNew);

    R putAdminProductNew(ProductNew productNew);

    R delAdminProductNew(Long id);

    R updateList(List<ProductNew> productNewList);

    ProductNew prdouctInfoByNo(String no);

    Integer count(String no,Long modelId,Long startTime,Long endTime, Long pointId);

    R getProductFile(Long id);

    R updateStatusFromBatch(List<Long> ids, Integer status);

    R queryProductInfo(String no);

    R queryLikeProductByNo(String no);

    R bindPoint(Long productId, Long pointId);
}
