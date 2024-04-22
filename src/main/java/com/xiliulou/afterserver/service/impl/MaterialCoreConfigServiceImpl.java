package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.MaterialCoreConfig;
import com.xiliulou.afterserver.mapper.MaterialCoreConfigMapper;
import com.xiliulou.afterserver.service.MaterialCoreConfigService;
import com.xiliulou.afterserver.util.R;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 物料核心配置(MaterialCoreConfig)表服务实现类
 *
 * @author makejava
 * @since 2024-03-21 19:03:03
 */
@Service("materialCoreConfigService")
public class MaterialCoreConfigServiceImpl implements MaterialCoreConfigService {
    @Resource
    private MaterialCoreConfigMapper materialCoreConfigMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public MaterialCoreConfig queryById(Integer id) {
        return this.materialCoreConfigMapper.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param materialCoreConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<MaterialCoreConfig> queryByPage(MaterialCoreConfig materialCoreConfig, PageRequest pageRequest) {
        long total = this.materialCoreConfigMapper.count(materialCoreConfig);
        return new PageImpl<>(this.materialCoreConfigMapper.queryAllByLimit(materialCoreConfig, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param materialCoreConfig 实例对象
     * @return 实例对象
     */
    @Override
    public R insert(MaterialCoreConfig materialCoreConfig) {
        if (materialCoreConfig.getMaterialCoreConfig().length()>300) {
            return R.failMsg("配置内容不能超过300个字符");
        }
        if (StringUtils.isEmpty(materialCoreConfig.getMaterialCoreConfig())) {
            return R.failMsg("配置内容不能为空");
        }
        this.materialCoreConfigMapper.insert(materialCoreConfig);
        return R.ok();
    }

    /**
     * 修改数据
     *
     * @param materialCoreConfig 实例对象
     * @return 实例对象
     */
    @Override
    public R<Object> update(MaterialCoreConfig materialCoreConfig) {
        if (materialCoreConfig.getMaterialCoreConfig().length()>300) {
            return R.failMsg("配置内容不能超过300个字符");
        }
        if (StringUtils.isEmpty(materialCoreConfig.getMaterialCoreConfig())) {
            return R.failMsg("配置内容不能为空");
        }
        this.materialCoreConfigMapper.update(materialCoreConfig);
        return R.ok();
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.materialCoreConfigMapper.deleteById(id) > 0;
    }
    
}
