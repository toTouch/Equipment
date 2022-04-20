package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.WorkAuditNotifyMapper;
import com.xiliulou.afterserver.mapper.WorkOrderReasonMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.WorkAuditNotifyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zgw
 * @date 2022/4/2 17:10
 * @mood
 */
@Service
@Slf4j
public class WorkAuditNotifyServiceImpl extends ServiceImpl<WorkAuditNotifyMapper, WorkAuditNotify> implements WorkAuditNotifyService {

    @Autowired
    WorkAuditNotifyMapper workAuditNotifyMapper;
    @Autowired
    UserService userService;
    @Autowired
    WorkOrderReasonService workOrderReasonService;
    @Autowired
    WorkOrderService workOrderService;
    @Autowired
    WorkOrderTypeService workOrderTypeService;

    @Override
    public R queryList(Integer size, Integer offset, Integer type) {
        List<WorkAuditNotify> list = workAuditNotifyMapper.queryList(size, offset, type);
        List<WorkAuditNotifyVo> data = new ArrayList(10);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(item -> {
                WorkAuditNotifyVo workAuditNotifyVo = new WorkAuditNotifyVo();
                BeanUtils.copyProperties(item, workAuditNotifyVo);

                User user = userService.getUserById(workAuditNotifyVo.getSubmitUid());
                if(Objects.nonNull(user)){
                    workAuditNotifyVo.setSubmitUserName(user.getUserName());
                }

                WorkOrderReason workOrderReason = workOrderReasonService.getById(workAuditNotifyVo.getWorkOrderReasonId());
                if(Objects.nonNull(workOrderReason)){
                    workAuditNotifyVo.setWorkOrderReasonName(workOrderReason.getName());
                    String name = workOrderService.getParentWorkOrderReason(workOrderReason.getId());
                    workAuditNotifyVo.setParentWorkOrderReasonName(name);
                }

                WorkOrderType workOrderType = workOrderTypeService.getById(workAuditNotifyVo.getOrderType());
                if(Objects.nonNull(workOrderType)){
                    workAuditNotifyVo.setOrderTypeName(workOrderType.getType());
                }

                data.add(workAuditNotifyVo);
            });
        }
        Long count = this.queryCount(type);

        Map<String, Object> result = new HashMap<>(2);
        result.put("data", data);
        result.put("total", count);
        return R.ok(result);
    }

    @Override
    public Long queryCount(Integer type) {
        return workAuditNotifyMapper.queryCount(type);
    }

    @Override
    public R readNotify(Long id) {
        WorkAuditNotify workAuditNotify = this.getById(id);
        if(Objects.nonNull(workAuditNotify)){
            workAuditNotify.setType(WorkAuditNotify.TYPE_UNFINISHED);
            workAuditNotify.setUpdateTime(System.currentTimeMillis());
            this.updateById(workAuditNotify);
        }
        return R.ok();
    }

    @Override
    public R readNotifyAll() {
        workAuditNotifyMapper.readNotifyAll(System.currentTimeMillis());
        return R.ok();
    }
}
