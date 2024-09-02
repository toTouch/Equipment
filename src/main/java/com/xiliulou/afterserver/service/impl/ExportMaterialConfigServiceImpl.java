package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.ExportMaterialConfig;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.ExportMaterialConfigMapper;
import com.xiliulou.afterserver.service.ExportMaterialConfigService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.cache.redis.RedisService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 物料导出配置表(ExportMaterialConfig)表服务实现类
 *
 * @author zhangbozhi
 * @since 2024-08-30 11:21:07
 */
@Service("exportMaterialConfigService")
public class ExportMaterialConfigServiceImpl implements ExportMaterialConfigService {
    
    @Resource
    private ExportMaterialConfigMapper exportMaterialConfigMapper;
    
    @Autowired
    private UserService userService;
    
    @Resource
    private RedisService redisService;
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ExportMaterialConfig queryById(Integer id) {
        return this.exportMaterialConfigMapper.selectById(id);
    }
    
    /**
     * 分页查询
     *
     * @param exportMaterialConfig 筛选条件
     * @param offset               查询起始位置
     * @param size                 查询条数
     * @return 查询结果
     */
    @Override
    public List<ExportMaterialConfig> listByLimit(ExportMaterialConfig exportMaterialConfig, Long offset, Long size) {
        return this.exportMaterialConfigMapper.selectPage(exportMaterialConfig, offset, size);
    }
    
    /**
     * 分页count
     *
     * @param exportMaterialConfig 筛选条件
     * @return 查询结果
     */
    @Override
    public Long count(ExportMaterialConfig exportMaterialConfig) {
        return this.exportMaterialConfigMapper.count(exportMaterialConfig);
    }
    
    /**
     * 新增数据
     *
     * @param exportMaterialConfigs 实例对象
     * @return 实例对象
     */
    @Override
    public Integer insert(List<ExportMaterialConfig> exportMaterialConfigs) {
        if (exportMaterialConfigs.isEmpty()) {
            return null;
        }
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        for (int i = 0; i < exportMaterialConfigs.size(); i++) {
            exportMaterialConfigs.get(i).setSort(i);
            if (!Objects.equals(userById.getUserType(), User.AFTER_USER_ROLE)) {
                exportMaterialConfigs.get(i).setSupplierId(userById.getThirdId());
            }
        }
        return this.exportMaterialConfigMapper.insertBatch(exportMaterialConfigs);
    }
    
    /**
     * 修改数据
     *
     * @param exportMaterialConfigs 实例对象
     * @return 实例对象
     */
    @Override
    public Integer update(List<ExportMaterialConfig> exportMaterialConfigs) {
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        // 导出会用到 此配置 故加锁保持一致性
        redisService.set(ExportMaterialConfig.EXPORT_MATERIAL_CONFIG_CALL_BACK + userById.getThirdId(), String.valueOf(System.currentTimeMillis()));
        exportMaterialConfigMapper.deleteAll();
        Integer result = insert(exportMaterialConfigs);
        
        redisService.delete(ExportMaterialConfig.EXPORT_MATERIAL_CONFIG_CALL_BACK + userById.getThirdId());
        return result;
    }
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.exportMaterialConfigMapper.deleteById(id) > 0;
    }
    
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean removeById(Integer id) {
        return this.exportMaterialConfigMapper.removeById(id) > 0;
    }
}
