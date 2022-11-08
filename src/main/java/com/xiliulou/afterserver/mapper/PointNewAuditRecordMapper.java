package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.PointNewAuditRecord;
import com.xiliulou.core.web.R;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (PointNewAuditRecord)表数据库访问层
 *
 * @author Hardy
 * @since 2022-11-08 13:37:00
 */
public interface PointNewAuditRecordMapper  extends BaseMapper<PointNewAuditRecord>{

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNewAuditRecord queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<PointNewAuditRecord> queryAllByLimit(@Param("offset") int offset,
        @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param pointNewAuditRecord 实例对象
     * @return 对象列表
     */
    List<PointNewAuditRecord> queryAll(PointNewAuditRecord pointNewAuditRecord);

    /**
     * 新增数据
     *
     * @param pointNewAuditRecord 实例对象
     * @return 影响行数
     */
    int insertOne(PointNewAuditRecord pointNewAuditRecord);

    /**
     * 修改数据
     *
     * @param pointNewAuditRecord 实例对象
     * @return 影响行数
     */
    int update(PointNewAuditRecord pointNewAuditRecord);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    List<PointNewAuditRecord> queryList(@Param("offset") Long offset, @Param("size")Long size, @Param("pointId")Long pointId);
}
