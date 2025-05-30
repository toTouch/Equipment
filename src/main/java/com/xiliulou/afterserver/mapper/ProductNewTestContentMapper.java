package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.ProductNewTestContent;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (ProductNewTestContent)表数据库访问层
 *
 * @author Hardy
 * @since 2023-04-26 17:22:44
 */
public interface ProductNewTestContentMapper  extends BaseMapper<ProductNewTestContent>{

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNewTestContent queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<ProductNewTestContent> queryAllByLimit(@Param("offset") int offset,
        @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param productNewTestContent 实例对象
     * @return 对象列表
     */
    List<ProductNewTestContent> queryAll(ProductNewTestContent productNewTestContent);

    /**
     * 新增数据
     *
     * @param productNewTestContent 实例对象
     * @return 影响行数
     */
    int insertOne(ProductNewTestContent productNewTestContent);

    /**
     * 修改数据
     *
     * @param productNewTestContent 实例对象
     * @return 影响行数
     */
    int update(ProductNewTestContent productNewTestContent);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    ProductNewTestContent queryByPid(@Param("pid") Long pid);
}
