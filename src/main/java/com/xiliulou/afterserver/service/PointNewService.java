package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.util.R;

import java.util.List;

/**
 * (PointNew)表服务接口
 *
 * @author Hardy
 * @since 2021-08-17 10:28:43
 */
public interface PointNewService extends IService<PointNew> {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNew queryByIdFromDB(Long id);

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNew queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<PointNew> queryAllByLimit(int offset, int limit,String name);

    /**
     * 新增数据
     *
     * @param pointNew 实例对象
     * @return 实例对象
     */
    PointNew insert(PointNew pointNew);

    /**
     * 修改数据
     *
     * @param pointNew 实例对象
     * @return 实例对象
     */
    Integer update(PointNew pointNew);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    R saveAdminPointNew(PointNew pointNew);

    R putAdminPointNew(PointNew pointNew);

    R delAdminPointNew(Long id);

    R putAdminPoint(PointNew pointNew);

    R pointInfo(Long pid);
}
