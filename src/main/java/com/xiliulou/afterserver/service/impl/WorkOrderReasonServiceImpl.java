package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.WorkOrderConstant;
import com.xiliulou.afterserver.entity.WorkOrderReason;
import com.xiliulou.afterserver.entity.WorkOrderType;
import com.xiliulou.afterserver.mapper.WorkOrderReasonMapper;
import com.xiliulou.afterserver.service.WorkOrderReasonService;
import com.xiliulou.afterserver.service.WorkOrderTypeService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.cache.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:49
 **/
@Service
@Slf4j
public class WorkOrderReasonServiceImpl extends ServiceImpl<WorkOrderReasonMapper, WorkOrderReason> implements WorkOrderReasonService {
    @Autowired
    private RedisService redisService;
 

    @Override
    public Integer deleteById(Long id) {
        return this.baseMapper.deleteForID(id);
    }

    @Override
    public R getTreeList() {
        List<WorkOrderReason> selectList = this.baseMapper.selectList(null);


        List<WorkOrderReason> collectMenu1 = selectList.stream().filter(item -> item.getParentId() == -1)
                .map(menu -> {
                    menu.setChird(getChrlidens(menu, selectList));
                    return menu;
                }).collect(Collectors.toList());

        return R.ok(collectMenu1);
    }
    
    @Override
    public WorkOrderReason queryByIdFromCache(Long workOrderReasonId) {
        WorkOrderReason serviceWithHash = redisService.getWithHash(WorkOrderConstant.WORK_ORDER_REASON + workOrderReasonId, WorkOrderReason.class);
        if (Objects.nonNull(serviceWithHash)) {
            return serviceWithHash;
        }
        
        WorkOrderReason workOrderReason = this.getById(workOrderReasonId);
        if (Objects.isNull(workOrderReason)) {
             workOrderReason = this.getById(workOrderReasonId);
            return workOrderReason;
        }
        
        redisService.saveWithHash(WorkOrderConstant.WORK_ORDER_REASON, workOrderReason);
        return workOrderReason;
    }
    
    
    /***
     * 递归查询所有的分类的下级分类
     */
    private List<WorkOrderReason> getChrlidens(WorkOrderReason root, List<WorkOrderReason> allList) {
        List<WorkOrderReason> chrlidens = allList.stream().filter(item -> {
            return item.getParentId().equals(root.getId());
        }).map(item -> {
            item.setChird(getChrlidens(item, allList));
            return item;
        }).collect(Collectors.toList());
        return chrlidens;
    }
    
    
}
