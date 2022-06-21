package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.ServerAuditEntry;
import com.xiliulou.afterserver.entity.ServerAuditValue;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.mapper.ServerAuditValueMapper;
import com.xiliulou.afterserver.service.ServerAuditEntryService;
import com.xiliulou.afterserver.service.ServerAuditValueService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.WechatServerAuditEntryQuery;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
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

        if(Collections.isEmpty(query.getWechatServerEntryValueQueryList())) {
            return R.ok();
        }

        query.getWechatServerEntryValueQueryList().forEach(item -> {
            biandOrUnbindEntry(item.getEntryId(), item.getValue(), user.getThirdId(), query.getWorkOrderId());
        });
        
        return null;
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
