package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.MaterialDelRecord;
import com.xiliulou.afterserver.mapper.MaterialDelRecordMapper;
import com.xiliulou.afterserver.service.MaterialDelRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * 物料删除追溯表(MaterialDelRecord)表服务实现类
 *
 * @author zhangbozhi
 * @since 2024-06-27 20:44:35
 */
@Service("materialDelRecordService")
public class MaterialDelRecordServiceImpl implements MaterialDelRecordService {
    
    @Resource
    private MaterialDelRecordMapper materialDelRecordMapper;
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public MaterialDelRecord queryById(Long id) {
        return this.materialDelRecordMapper.selectById(id);
    }
    
    /**
     * 分页查询
     *
     * @param materialDelRecord 筛选条件
     * @param offset            查询起始位置
     * @param size              查询条数
     * @return 查询结果
     */
    @Override
    public List<MaterialDelRecord> listByLimit(MaterialDelRecord materialDelRecord, Long offset, Long size) {
        return this.materialDelRecordMapper.selectPage(materialDelRecord, offset, size);
    }
    
    /**
     * 分页count
     *
     * @param materialDelRecord 筛选条件
     * @return 查询结果
     */
    @Override
    public Long count(MaterialDelRecord materialDelRecord) {
        return this.materialDelRecordMapper.count(materialDelRecord);
    }
    
    /**
     * 新增数据
     *
     * @param materialDelRecord 实例对象
     * @return 实例对象
     */
    @Override
    public MaterialDelRecord insert(MaterialDelRecord materialDelRecord) {
        this.materialDelRecordMapper.insert(materialDelRecord);
        return materialDelRecord;
    }
    
    /**
     * 修改数据
     *
     * @param materialDelRecord 实例对象
     * @return 实例对象
     */
    @Override
    public MaterialDelRecord update(MaterialDelRecord materialDelRecord) {
        this.materialDelRecordMapper.update(materialDelRecord);
        return this.queryById(materialDelRecord.getId());
    }
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.materialDelRecordMapper.deleteById(id) > 0;
    }
    
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean removeById(Long id) {
        return this.materialDelRecordMapper.removeById(id) > 0;
    }
}
