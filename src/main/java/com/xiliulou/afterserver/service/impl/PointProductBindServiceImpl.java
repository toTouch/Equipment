package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.PointProductBind;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.service.PointProductBindService;
import java.util.Objects;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * (PointProductBind)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-17 10:29:11
 */
@Service("pointProductBindService")
@Slf4j
public class PointProductBindServiceImpl implements PointProductBindService {
    @Resource
    private PointProductBindMapper pointProductBindMapper;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public PointProductBind queryByIdFromDB(Long id) {
        return this.pointProductBindMapper.queryById(id);
    }

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public PointProductBind queryByIdFromCache(Long id) {
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
    public List<PointProductBind> queryAllByLimit(int offset, int limit) {
        return this.pointProductBindMapper.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param pointProductBind 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointProductBind insert(PointProductBind pointProductBind) {
        this.pointProductBindMapper.insertOne(pointProductBind);
        return pointProductBind;
    }

    /**
     * 修改数据
     *
     * @param pointProductBind 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(PointProductBind pointProductBind) {
        return this.pointProductBindMapper.update(pointProductBind);

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
        return this.pointProductBindMapper.deleteById(id) > 0;
    }

    @Override
    public List<PointProductBind> queryByPointNewIdAndBindType(Long pid, Integer type) {
        LambdaQueryWrapper<PointProductBind> queryWrapper = new LambdaQueryWrapper<PointProductBind>()
            .eq(PointProductBind::getPointId, pid)
            .eq(Objects.nonNull(type), PointProductBind::getPointType, type);
        return this.pointProductBindMapper.selectList(queryWrapper);
    }

    @Override
    public List<PointProductBind> queryByPointNewIdAndProductId(Long id, Long item) {
        LambdaQueryWrapper<PointProductBind> queryWrapper = new LambdaQueryWrapper<PointProductBind>()
                .eq(PointProductBind::getPointId, id).eq(PointProductBind::getProductId,item);
        return this.pointProductBindMapper.selectList(queryWrapper);
    }

    @Override
    public PointProductBind queryByProductId(Long productId) {
        QueryWrapper<PointProductBind> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id",productId);
        return pointProductBindMapper.selectOne(wrapper);
    }

    @Override
    public List<Long> queryProductIdsByPidAndPtype(Long pointId, Integer pointType) {
        return pointProductBindMapper.queryProductIdsByPidAndPtype(pointId, pointType);
    }


}
