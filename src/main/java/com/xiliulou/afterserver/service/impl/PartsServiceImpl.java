package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.mapper.PartsMapper;
import com.xiliulou.afterserver.service.PartsService;
import com.xiliulou.afterserver.web.query.PartsQuery;
import com.xiliulou.afterserver.web.vo.PartsVo;
import com.xiliulou.core.web.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
/**
 * (Parts)表服务实现类
 *
 * @author Hardy
 * @since 2022-12-15 15:02:05
 */
@Service("partsService")
@Slf4j
public class PartsServiceImpl extends ServiceImpl<PartsMapper, Parts> implements PartsService {
    @Resource
    private PartsMapper partsMapper;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Parts queryByIdFromDB(Long id) {
        return this.partsMapper.queryById(id);
    }
    
        /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public  Parts queryByIdFromCache(Long id) {
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
    public List<Parts> queryAllByLimit(int offset, int limit) {
        return this.partsMapper.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param parts 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Parts insert(Parts parts) {
        this.partsMapper.insertOne(parts);
        return parts;
    }

    /**
     * 修改数据
     *
     * @param parts 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(Parts parts) {
       return this.partsMapper.update(parts);
         
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
        return this.partsMapper.deleteById(id) > 0;
    }

    @Override
    public R queryList(Integer size, Integer offset, String name) {
        List<Parts> parts = this.partsMapper.queryList(size, offset, name);
        Integer count = this.partsMapper.queryCount(size, offset, name);

        Map<String, Object> result = new HashMap<>();
        result.put("data", parts);
        result.put("count", count);
        return R.ok(result);
    }

    @Override
    public R saveOne(PartsQuery partsQuery) {
        Parts partsBySn = queryBySn(partsQuery.getSn());
        if(Objects.nonNull(partsBySn)) {
            return R.fail("物料编码已存在，请检查");
        }

        Parts parts = new Parts();
        BeanUtils.copyProperties(partsQuery, parts);
        parts.setCreateTime(System.currentTimeMillis());
        parts.setUpdateTime(System.currentTimeMillis());
        parts.setDelFlag(Parts.DEL_NORMAL);
        this.partsMapper.insert(parts);
        return R.ok();
    }

    @Override
    public R updateOne(PartsQuery partsQuery) {
        Parts parts = queryByIdFromDB(partsQuery.getId());
        if(Objects.isNull(parts)) {
            return R.fail("未查询到相关物料");
        }

        Parts partsBySn = queryBySn(partsQuery.getSn());
        if(Objects.nonNull(partsBySn) && !Objects.equals(parts.getSn(), partsBySn.getSn())) {
            return R.fail("物料编码已存在，请检查");
        }

        Parts updateParts = new Parts();
        BeanUtils.copyProperties(partsQuery, updateParts);
        parts.setUpdateTime(System.currentTimeMillis());
        this.partsMapper.update(updateParts);
        return R.ok();
    }

    @Override
    public R deleteOne(Long id) {
        Parts updateParts = new Parts();
        updateParts.setId(id);
        updateParts.setUpdateTime(System.currentTimeMillis());
        updateParts.setDelFlag(Parts.DEL_DEL);
        return R.ok(update(updateParts));
    }

    @Override
    public R queryPull(Integer size, Integer offset, String name) {
        return R.ok(Optional.ofNullable(this.partsMapper.queryList(size, offset, name)).orElse(new ArrayList<>()).stream().map(item -> {
            PartsVo vo = new PartsVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList()));
    }

    @Override
    public Parts queryBySn(String sn) {
        return this.partsMapper.queryBySn(sn);
    }
}
