package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.PointProductBind;

import java.util.List;

/**
 * (PointProductBind)表服务接口
 *
 * @author Hardy
 * @since 2021-08-17 10:29:11
 */
public interface PointProductBindService {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    PointProductBind queryByIdFromDB(Long id);

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    PointProductBind queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<PointProductBind> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param pointProductBind 实例对象
     * @return 实例对象
     */
    PointProductBind insert(PointProductBind pointProductBind);

    /**
     * 修改数据
     *
     * @param pointProductBind 实例对象
     * @return 实例对象
     */
    Integer update(PointProductBind pointProductBind);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    List<PointProductBind> queryByPointNewId(Long pid);

    List<PointProductBind> queryByPointNewIdAndProductId(Long id, Long item);

    PointProductBind queryByProductId(Long productId);

    List<Long> queryProductIdsByPidAndPtype(Long pointId, Integer pointType);
}
