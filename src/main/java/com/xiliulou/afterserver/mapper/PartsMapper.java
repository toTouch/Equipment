package com.xiliulou.afterserver.mapper;


import com.xiliulou.afterserver.entity.Parts;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (Parts)表数据库访问层
 *
 * @author Hardy
 * @since 2022-12-15 15:02:04
 */
public interface PartsMapper  extends BaseMapper<Parts>{

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Parts queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Parts> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param parts 实例对象
     * @return 对象列表
     */
    List<Parts> queryAll(Parts parts);

    /**
     * 新增数据
     *
     * @param parts 实例对象
     * @return 影响行数
     */
    int insertOne(Parts parts);

    /**
     * 修改数据
     *
     * @param parts 实例对象
     * @return 影响行数
     */
    int update(Parts parts);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    List<Parts> queryList(@Param("size")Integer size,@Param("offset") Integer offset, @Param("name")String name);

    Parts queryBySn(@Param("sn")String sn);

    Integer queryCount(@Param("size")Integer size,@Param("offset") Integer offset, @Param("name")String name);

    Parts queryByNameAndSpecification(@Param("name")String name, @Param("specification")String specification);
}
