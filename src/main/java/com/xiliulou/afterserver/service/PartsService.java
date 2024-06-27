package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.web.query.PartsQuery;
import com.xiliulou.core.web.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * (Parts)表服务接口
 *
 * @author Hardy
 * @since 2022-12-15 15:02:05
 */
public interface PartsService extends IService<Parts> {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    Parts queryByIdFromDB(Long id);
    
      /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    Parts queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Parts> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param parts 实例对象
     * @return 实例对象
     */
    Parts insert(Parts parts);

    /**
     * 修改数据
     *
     * @param parts 实例对象
     * @return 实例对象
     */
    Integer update(Parts parts);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    R queryList(Integer size, Integer offset, String name, String sn);

    R saveOne(PartsQuery partsQuery);
    
    R partsExportExcel(String sn, String name, HttpServletResponse response);
    
    R updateOne(PartsQuery partsQuery);

    R deleteOne(Long id);

    R queryPull(Integer size, Integer offset, String name);

    Parts queryBySn(String sn);

    Parts queryByNameAndSpecification(String name, String specification);
    
    R listByName(String name, String sn);
}
