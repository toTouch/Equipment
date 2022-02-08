package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.mapper.BatchMapper;
import com.xiliulou.afterserver.service.BatchService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * (Batch)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-16 15:50:17
 */
@Service("batchService")
@Slf4j
public class BatchServiceImpl implements BatchService {
    @Resource
    private BatchMapper batchMapper;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Batch queryByIdFromDB(Long id) {
        return this.batchMapper.queryById(id);
    }

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Batch queryByIdFromCache(Long id) {
        return null;
    }


    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Batch> queryAllByLimit(String batchNo,int offset, int limit) {
        return this.batchMapper.queryAllByLimit(batchNo,offset, limit);
    }

    /**
     * 新增数据
     *
     * @param batch 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Batch insert(Batch batch) {
        batch.setCreateTime(System.currentTimeMillis());
        this.batchMapper.insertOne(batch);
        return batch;
    }

    /**
     * 修改数据
     *
     * @param batch 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(Batch batch) {
        return this.batchMapper.update(batch);

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
        return this.batchMapper.deleteById(id) > 0;
    }

    @Override
    public Long count(String batchNo) {
        return this.batchMapper.count(batchNo);
    }

    @Override
    public Batch queryByName(String batchName) {
        return batchMapper.selectOne(new QueryWrapper<Batch>().eq("name", batchName));
    }
}
