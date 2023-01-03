package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.ServerAuditValueMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.WechatServerAuditEntryQuery;
import com.xiliulou.afterserver.web.query.WorkOrderServerQuery;
import io.jsonwebtoken.lang.Collections;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/6/20 11:24
 * @mood
 */
@Service
public class ServerAuditValueServiceImpl extends ServiceImpl<ServerAuditValueMapper, ServerAuditValue> implements ServerAuditValueService {

    @Resource
    ServerAuditValueMapper serverAuditValueMapper;
    @Autowired
    UserService userService;
    @Autowired
    WorkOrderService workOrderService;
    @Autowired
    ServerAuditEntryService serverAuditEntryService ;
    @Autowired
    WorkOrderServerService workOrderServerService;
    @Autowired
    FileService fileService;

    @Override
    public ServerAuditValue queryByOrderIdAndServerId(Long entryId, Long workOrderId, Long thirdId) {
        return this.baseMapper.selectOne(new QueryWrapper<ServerAuditValue>().eq("entry_id", entryId).eq("order_id", workOrderId).eq("server_id", thirdId));
    }

    @Override
    public R saveOne(WechatServerAuditEntryQuery query) {
        Long uid = SecurityUtils.getUid();

        if (Objects.isNull(uid)) {
            return R.fail("未查询到相关用户");
        }

        User user = userService.getUserById(uid);
        if (Objects.isNull(user)) {
            return R.fail("未查询到相关用户");
        }

        WorkOrder workOrderOld = workOrderService.getById(query.getWorkOrderId());
        if (Objects.isNull(workOrderOld)) {
            return R.fail("未查询到工单相关信息");
        }

        if (Objects.equals(workOrderOld.getStatus(), WorkOrder.STATUS_SUSPEND)) {
            return R.fail("工单已暂停，不可上传");
        }

        List<WorkOrderServerQuery> workOrderServer = workOrderServerService.queryByWorkOrderIdAndServerId(query.getWorkOrderId(), user.getThirdId());
        if (CollectionUtils.isEmpty(workOrderServer) || workOrderServer.size() > 1) {
            return R.fail("未查询出或查询出多条服务商工单信息");
        }

        if (Objects.nonNull(workOrderServer.get(0).getSolutionTime())) {
            return R.fail("服务商工单已提交，不可修改");
        }

        if (StringUtils.isBlank(query.getSolution())) {
            return R.fail("请填写解决方案");
        }

        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("type", File.TYPE_WORK_ORDER);
        wrapper.eq("bind_id", query.getWorkOrderId());
        wrapper.ge("file_type", 1)
                .lt("file_type", 90000)
                .eq("server_id", user.getThirdId());


        List<File> list = fileService.getBaseMapper().selectList(wrapper);

        if (CollectionUtils.isEmpty(list)) {
            return R.fail("请上传处理图片");
        }

        if (Objects.equals(query.getHasParts(), WorkOrderServer.HAS_PARTS)) {
            if (!workOrderService.checkAndclearEntry(query.getWorkOrderParts())) {
                return R.fail("请添加相关物件");
            }
        }

        WorkOrderServer updateWorkOrderServer = new WorkOrderServer();
        updateWorkOrderServer.setId(workOrderServer.get(0).getId());
        updateWorkOrderServer.setSolution(query.getSolution());
        updateWorkOrderServer.setSolutionTime(System.currentTimeMillis());
        updateWorkOrderServer.setPrescription(updateWorkOrderServer.getSolutionTime() - workOrderOld.getAssignmentTime());
        workOrderServerService.updateById(updateWorkOrderServer);

        //全部完成修改工单为已处理
        if (workOrderService.checkServerProcess(query.getWorkOrderId())) {
            WorkOrder workOrder = new WorkOrder();
            workOrder.setId(workOrderOld.getId());
            workOrder.setStatus(WorkOrder.STATUS_PROCESSING);
            workOrder.setProcessTime(System.currentTimeMillis());
            workOrderService.updateById(workOrder);
        }

        workOrderService.clareAndAddWorkOrderParts(query.getWorkOrderId(), user.getThirdId(), query.getWorkOrderParts(), WorkOrderParts.TYPE_SERVER_PARTS);

        if(Collections.isEmpty(query.getWechatServerEntryValueQueryList())) {
            return R.ok();
        }

        query.getWechatServerEntryValueQueryList().forEach(item -> {
            biandOrUnbindEntry(item.getEntryId(), item.getValue(), user.getThirdId(), query.getWorkOrderId());
        });
        return R.ok();
    }

    private boolean biandOrUnbindEntry(Long entryId, String value, Long serverId, Long workOrderId){
        ServerAuditEntry serverAuditEntryOld = serverAuditEntryService.getById(entryId);
        if(Objects.isNull(serverAuditEntryOld)) {
            return false;
        }

        ServerAuditValue serverAuditValue = queryByEntryIdAndOrderId(entryId, serverId, workOrderId);
        if(StringUtils.isBlank(value)) {
            if(!Objects.isNull(serverAuditValue)) {
                this.removeById(serverAuditValue.getId());
            }
            return true;
        }

        //如果不为空value 看entryId绑定的值是否为空 不为空修改
        if(!Objects.isNull(serverAuditValue)) {
            serverAuditValue.setValue(value);
            serverAuditValue.setUpdateTime(System.currentTimeMillis());
            this.updateById(serverAuditValue);
            return true;
        }

        serverAuditValue = new ServerAuditValue();
        serverAuditValue.setEntryId(entryId);
        serverAuditValue.setOrderId(workOrderId);
        serverAuditValue.setServerId(serverId);
        serverAuditValue.setValue(value);
        serverAuditValue.setCreateTime(System.currentTimeMillis());
        serverAuditValue.setUpdateTime(System.currentTimeMillis());
        this.save(serverAuditValue);
        return true;
    }
    
    private ServerAuditValue queryByEntryIdAndOrderId(Long entryId, Long serverId, Long workOrderId){
        return this.baseMapper.selectOne(new QueryWrapper<ServerAuditValue>().eq("entry_id", entryId).eq("order_id", workOrderId).eq("server_id", serverId));
    }
}
