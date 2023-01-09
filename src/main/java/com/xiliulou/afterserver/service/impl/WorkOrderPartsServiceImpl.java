package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.entity.WorkOrderParts;
import com.xiliulou.afterserver.mapper.WorkOrderPartsMapper;
import com.xiliulou.afterserver.service.PartsService;
import com.xiliulou.afterserver.service.WorkOrderPartsService;
import java.util.ArrayList;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
/**
 * (WorkOrderParts)表服务实现类
 *
 * @author Hardy
 * @since 2022-12-13 15:50:12
 */
@Service("workOrderPartsService")
@Slf4j
public class WorkOrderPartsServiceImpl implements WorkOrderPartsService {
    @Resource
    private WorkOrderPartsMapper workOrderPartsMapper;
    @Autowired
    private PartsService partsService;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public WorkOrderParts queryByIdFromDB(Long id) {
        return this.workOrderPartsMapper.queryById(id);
    }
    
        /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public  WorkOrderParts queryByIdFromCache(Long id) {
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
    public List<WorkOrderParts> queryAllByLimit(int offset, int limit) {
        return this.workOrderPartsMapper.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param workOrderParts 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrderParts insert(WorkOrderParts workOrderParts) {
        this.workOrderPartsMapper.insertOne(workOrderParts);
        return workOrderParts;
    }

    /**
     * 修改数据
     *
     * @param workOrderParts 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(WorkOrderParts workOrderParts) {
       return this.workOrderPartsMapper.update(workOrderParts);
         
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
        return this.workOrderPartsMapper.deleteById(id) > 0;
    }

    @Override
    public List<WorkOrderParts> queryByWorkOrderIdAndServerId(Long workOrderId, Long serverId, Integer type) {
        List<WorkOrderParts> workOrderParts = this.workOrderPartsMapper.queryByWorkOrderIdAndServerId(workOrderId, serverId, type);
        if(CollectionUtils.isNotEmpty(workOrderParts)){
            return new ArrayList<>();
        }
        workOrderParts.forEach(item -> {
            Parts parts = partsService.queryByIdFromDB(item.getPartsId());
            if(Objects.isNull(parts)) {
                return;
            }

            item.setPurchasePrice(parts.getPurchasePrice());
            item.setSellPrice(parts.getSellPrice());
        });
        return workOrderParts;
    }

    @Override
    public Integer deleteByOidAndServerId(Long oid, Long sid, Integer type) {
        return this.workOrderPartsMapper.deleteByOidAndServerId(oid, sid, type);
    }

    @Override
    public Long queryPartsMaxCountByWorkOrderId(Long id, Integer type) {
        return this.workOrderPartsMapper.queryPartsMaxCountByWorkOrderId(id, type);
    }
}
