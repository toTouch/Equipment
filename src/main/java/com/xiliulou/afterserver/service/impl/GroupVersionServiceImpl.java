package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.AuditProcess;
import com.xiliulou.afterserver.entity.GroupVersion;
import com.xiliulou.afterserver.entity.ImportTemplate;
import com.xiliulou.afterserver.mapper.GroupVersionMapper;
import com.xiliulou.afterserver.mapper.ImportTemplateMapper;
import com.xiliulou.afterserver.service.AuditProcessService;
import com.xiliulou.afterserver.service.GroupVersionService;
import com.xiliulou.afterserver.service.ImportTemplateService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.core.exception.CustomBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author zgw
 * @date 2022/6/23 15:56
 * @mood
 */
@Service
public class GroupVersionServiceImpl  extends ServiceImpl<GroupVersionMapper, GroupVersion> implements GroupVersionService {

    @Autowired
    AuditProcessService auditProcessService;

    public static final String UPDATE = "被修改了";
    public static final String INSERT = "被添加了";
    public static final String DELETE = "被删除了";

    @Override
    public GroupVersion queryByGroupId(Long groupId) {
        return this.baseMapper.selectOne(new QueryWrapper<GroupVersion>().eq("group_id", groupId));
    }

    @Override
    public void createOrUpdate(Long groupId, String groupName) {
        GroupVersion groupVersion = this.queryByGroupId(groupId);
        if(Objects.isNull(groupVersion)) {
            GroupVersion insert = new GroupVersion();
            insert.setGroupId(groupId);
            insert.setGroupName(groupName);
            insert.setVersion(new BigDecimal("0"));
            if(!save(insert)) {
                throw  new CustomBusinessException("数据库错误");
            }

            return;
        }

        groupVersion.setVersion(groupVersion.getVersion().add(new BigDecimal("0.01")));
        groupVersion.setGroupName(groupName);
        if(!updateById(groupVersion)) {
            throw  new CustomBusinessException("数据库错误");
        }
    }

    @Override
    public boolean removeByGroupId(Long id) {
        return this.baseMapper.delete(new QueryWrapper<GroupVersion>().eq("group_id", id)) > 0;
    }

    @Override
    public R getList(String type) {
        //查询组件
        AuditProcess auditProcess = auditProcessService.getByType(type);
        if(Objects.isNull(auditProcess)) {
            return R.fail(null, "未查询到相关流程信息");
        }

        return R.ok(this.baseMapper.selectByProcessId(auditProcess.getId()));
    }

    @Override
    public R checkGroupVersion(List<GroupVersion> groupVersionList, String type) {
        AuditProcess auditProcess = auditProcessService.getByType(type);
        if(Objects.isNull(auditProcess)) {
            return R.fail(null, "未查询到相关流程信息");
        }
        List<String> msg = new ArrayList<>();
        List<GroupVersion> groupVersionsOld = this.baseMapper.selectByProcessId(auditProcess.getId());

        if(CollectionUtils.isEmpty(groupVersionList) && CollectionUtils.isNotEmpty(groupVersionList)) {
            return R.ok(msg);
        }

        if(CollectionUtils.isEmpty(groupVersionsOld)) {
            groupVersionList.forEach(item -> {
                msg.add(item.getGroupName() + DELETE);
            });
            return R.ok(msg);
        }

        if(CollectionUtils.isNotEmpty(groupVersionList)) {
            groupVersionsOld.forEach(item -> {
                msg.add(item.getGroupName() + INSERT);
            });
            return R.ok(msg);
        }

        Iterator<GroupVersion> oldIt = groupVersionsOld.iterator();
        Iterator<GroupVersion> it = groupVersionsOld.iterator();

        while(oldIt.hasNext()) {
            GroupVersion groupVersionOld = it.next();
            while(it.hasNext()) {
                GroupVersion groupVersion = it.next();
                if(Objects.equals(groupVersionOld.getGroupId(), groupVersion.getGroupId())) {
                    if(!(groupVersionOld.getVersion().compareTo(groupVersion.getVersion()) == 0)) {
                        msg.add(groupVersionOld.getGroupName() + UPDATE);
                        it.remove();
                        oldIt.remove();
                    }
                }
            }
        }

        if(CollectionUtils.isEmpty(groupVersionsOld)) {
            groupVersionList.forEach(item -> {
                msg.add(item.getGroupName() + DELETE);
            });
            return R.ok(msg);
        }

        if(CollectionUtils.isNotEmpty(groupVersionList)) {
            groupVersionsOld.forEach(item -> {
                msg.add(item.getGroupName() + INSERT);
            });
            return R.ok(msg);
        }

        return R.ok(msg);
    }
}
