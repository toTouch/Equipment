package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.ProductNewTestContent;
import com.xiliulou.core.web.R;
import java.util.List;

/**
 * (ProductNewTestContent)表服务接口
 *
 * @author Hardy
 * @since 2023-04-26 17:22:44
 */
public interface ProductNewTestContentService {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNewTestContent queryByIdFromDB(Long id);
    
      /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNewTestContent queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<ProductNewTestContent> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param productNewTestContent 实例对象
     * @return 实例对象
     */
    ProductNewTestContent insert(ProductNewTestContent productNewTestContent);

    /**
     * 修改数据
     *
     * @param productNewTestContent 实例对象
     * @return 实例对象
     */
    Integer update(ProductNewTestContent productNewTestContent);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    ProductNewTestContent queryByPid(Long pid);

    R queryInfoByPid(Long pid);
}
