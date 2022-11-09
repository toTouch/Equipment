package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.PointNewAuditRecord;
import com.xiliulou.afterserver.mapper.PointNewAuditRecordMapper;
import com.xiliulou.afterserver.service.PointNewAuditRecordService;
import com.xiliulou.core.web.R;
import java.util.HashMap;
import java.util.Map;
import net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
/**
 * (PointNewAuditRecord)表服务实现类
 *
 * @author Hardy
 * @since 2022-11-08 13:37:01
 */
@Service("pointNewAuditRecordService")
@Slf4j
public class PointNewAuditRecordServiceImpl implements PointNewAuditRecordService {
    @Resource
    private PointNewAuditRecordMapper pointNewAuditRecordMapper;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public PointNewAuditRecord queryByIdFromDB(Long id) {
        return this.pointNewAuditRecordMapper.queryById(id);
    }
    
        /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public  PointNewAuditRecord queryByIdFromCache(Long id) {
        return null;
    }


    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<PointNewAuditRecord> queryAllByLimit(int offset, int limit) {
        return this.pointNewAuditRecordMapper.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param pointNewAuditRecord 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointNewAuditRecord insert(PointNewAuditRecord pointNewAuditRecord) {
        this.pointNewAuditRecordMapper.insertOne(pointNewAuditRecord);
        return pointNewAuditRecord;
    }

    /**
     * 修改数据
     *
     * @param pointNewAuditRecord 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(PointNewAuditRecord pointNewAuditRecord) {
       return this.pointNewAuditRecordMapper.update(pointNewAuditRecord);
         
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        return this.pointNewAuditRecordMapper.deleteById(id) > 0;
    }

    @Override
    public R queryList(Long offset, Long size, Long pointId) {
        List<PointNewAuditRecord> pointNewAuditRecords = pointNewAuditRecordMapper.queryList(offset, size, pointId);
        Long count = pointNewAuditRecordMapper.queryCount(offset, size, pointId);

        Map<String, Object> result = new HashMap<>(2);
        result.put("data", pointNewAuditRecords);
        result.put("count", count);
        return R.ok(result);
    }
}
