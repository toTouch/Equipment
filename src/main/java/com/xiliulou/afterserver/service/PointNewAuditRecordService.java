package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.PointNewAuditRecord;
import com.xiliulou.core.web.R;
import java.util.List;

/**
 * (PointNewAuditRecord)表服务接口
 *
 * @author Hardy
 * @since 2022-11-08 13:37:00
 */
public interface PointNewAuditRecordService {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNewAuditRecord queryByIdFromDB(Long id);
    
      /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNewAuditRecord queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<PointNewAuditRecord> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param pointNewAuditRecord 实例对象
     * @return 实例对象
     */
    PointNewAuditRecord insert(PointNewAuditRecord pointNewAuditRecord);

    /**
     * 修改数据
     *
     * @param pointNewAuditRecord 实例对象
     * @return 实例对象
     */
    Integer update(PointNewAuditRecord pointNewAuditRecord);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    R queryList(Long offset, Long size, Long pointId);
}
