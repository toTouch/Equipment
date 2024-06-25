package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.MaterialOperation;
import com.xiliulou.afterserver.mapper.MaterialOperationMapper;
import com.xiliulou.afterserver.service.MaterialOperationService;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * (MaterialOperation)表服务实现类
 *
 * @author zhangbozhi
 * @since 2024-06-23 22:21:42
 */
@Service("materialOperationService")
public class MaterialOperationServiceImpl implements MaterialOperationService {
    
    @Resource
    private MaterialOperationMapper materialOperationMapper;
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public MaterialOperation queryById(Integer id) {
        return this.materialOperationMapper.selectById(id);
    }
    
    /**
     * 分页查询
     *
     * @param materialOperation 筛选条件
     * @param offset            查询起始位置
     * @param size              查询条数
     * @return 查询结果
     */
    @Override
    public List<MaterialOperation> listByLimit(MaterialOperation materialOperation, Long offset, Long size) {
        return this.materialOperationMapper.selectPage(materialOperation, offset, size);
    }
    
    /**
     * 分页count
     *
     * @param materialOperation 筛选条件
     * @return 查询结果
     */
    @Override
    public Long count(MaterialOperation materialOperation) {
        return this.materialOperationMapper.count(materialOperation);
    }
    
    /**
     * 新增数据
     *
     * @param materialOperation 实例对象
     * @return 实例对象
     */
    @Override
    public MaterialOperation insert(MaterialOperation materialOperation) {
        this.materialOperationMapper.insert(materialOperation);
        return materialOperation;
    }
    
    /**
     * 修改数据
     *
     * @param materialOperation 实例对象
     * @return 实例对象
     */
    @Override
    public MaterialOperation update(MaterialOperation materialOperation) {
        this.materialOperationMapper.update(materialOperation);
        return this.queryById(materialOperation.getId());
    }
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.materialOperationMapper.deleteById(id) > 0;
    }
    
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean removeById(Integer id) {
        return this.materialOperationMapper.removeById(id) > 0;
    }
}
