package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.entity.AuditValue;
import com.xiliulou.afterserver.mapper.AuditValueMapper;
import com.xiliulou.afterserver.service.AuditEntryService;
import com.xiliulou.afterserver.service.AuditGroupLogContentService;
import com.xiliulou.afterserver.service.AuditValueService;
import com.xiliulou.core.json.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/6/6 18:06
 * @mood
 */
@Service
public class AuditValueServiceImpl extends ServiceImpl<AuditValueMapper, AuditValue> implements AuditValueService {

    @Resource
    AuditValueMapper auditValueMapper;
    @Autowired
    AuditEntryService auditEntryService;
    @Autowired
    AuditGroupLogContentService auditGroupLogContentService;

    @Override
    public Long getCountByEntryIdsAndPid(List<Long> entryIds, Long productNewId, Integer required) {
        return auditValueMapper.getCountByEntryIdsAndPid(entryIds, productNewId, required);
    }

    @Override
    public boolean biandOrUnbindEntry(Long entryId, String value, Long pid) {
        AuditEntry auditEntryOld = auditEntryService.getById(entryId);
        if(Objects.isNull(auditEntryOld)) {
            return false;
        }

        //添加日志
//        if(Objects.nonNull(logId)) {
//            auditGroupLogContentService.saveOne(logId, auditEntryOld.getName(), auditEntryOld.getType(), value);
//        }

        AuditValue auditValue = auditValueMapper.selectByEntryId(entryId, pid);
        //log.error("测试pda上传 -----> " + JsonUtil.toJson(auditValue) + "value = " + value  +",pid=" + pid + ",entryId="+entryId);
        // 如果为空value 看entryId绑定的值是否为空.为空跳过，不为空删除
        if(StringUtils.isBlank(value) || Objects.equals(value, "[]")) {
            if(!Objects.isNull(auditValue)) {
                auditValueMapper.deleteById(auditValue.getId());
            }
            return true;
        }

        //如果不为空value 看entryId绑定的值是否为空 不为空修改
        if(!Objects.isNull(auditValue)) {
            auditValue.setValue(value);
            auditValue.setUpdateTime(System.currentTimeMillis());
            auditValueMapper.updateById(auditValue);
            return true;
        }

        //为空创建
        auditValue = new AuditValue();
        auditValue.setPid(pid);
        auditValue.setEntryId(entryId);
        auditValue.setValue(value);
        auditValue.setCreateTime(System.currentTimeMillis());
        auditValue.setUpdateTime(System.currentTimeMillis());
        auditValueMapper.insert(auditValue);
        return true;
    }

    @Override
    public boolean isValueEmpty(Long entryId, Long pid){
        AuditValue auditValue = auditValueMapper.selectByEntryId(entryId, pid);
        if(Objects.isNull(auditValue)) {
            return true;
        }
        return false;
    }

    @Override
    public String getValue(Long entryId, Long pid){
        AuditValue auditValue = auditValueMapper.selectByEntryId(entryId, pid);
        if(Objects.isNull(auditValue)) {
            return null;
        }
        return auditValue.getValue();
    }

    @Override
    public List<AuditValue> getByPidAndEntryIds(List<Long> entryIds, Long pid){
        LambdaQueryWrapper<AuditValue> query = new LambdaQueryWrapper<>();
        query.eq(AuditValue::getPid, pid).in(CollectionUtils.isNotEmpty(entryIds), AuditValue::getEntryId, entryIds);
        return auditValueMapper.selectList(query);
    }

    @Override
    public void copyValueToTargetValueIsNoll(List<AuditValue> source, List<AuditValue> target){
        if(CollectionUtils.isEmpty(source) || CollectionUtils.isEmpty(target)){
            return;
        }

        source.forEach(s -> {
            for (AuditValue t : target) {
                if(Objects.equals(s.getEntryId(), t.getEntryId())) {
                    copyValueToTargetValueIsNoll(s, t);
                    break;
                }
            }
        });
    }

    @Override
    public void copyValueToTargetValueIsNoll(AuditValue source, AuditValue target){
        if(Objects.isNull(source)){
            return;
        }

        if(Objects.nonNull(target) && StringUtils.isNotBlank(target.getValue())){
            return;
        }

        biandOrUnbindEntry(target.getEntryId(), source.getValue(), target.getPid());
    }
}
