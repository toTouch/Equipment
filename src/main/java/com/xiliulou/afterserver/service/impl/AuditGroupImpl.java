package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.AuditProcessConstans;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.entity.AuditGroup;
import com.xiliulou.afterserver.entity.AuditProcess;
import com.xiliulou.afterserver.mapper.AuditGroupMapper;
import com.xiliulou.afterserver.service.AuditEntryService;
import com.xiliulou.afterserver.service.AuditGroupService;
import com.xiliulou.afterserver.service.AuditProcessService;
import com.xiliulou.afterserver.service.AuditValueService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditGroupStrawberryQuery;
import com.xiliulou.afterserver.web.vo.AuditGroupStrawberryVo;
import com.xiliulou.afterserver.web.vo.AuditGroupVo;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditGroupVo;
import com.xiliulou.core.json.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zgw
 * @date 2022/6/6 18:24
 * @mood
 */
@Service
public class AuditGroupImpl extends ServiceImpl<AuditGroupMapper, AuditGroup> implements AuditGroupService {

    @Resource
    AuditGroupMapper auditGroupMapper;
    @Autowired
    AuditEntryService auditEntryService;
    @Autowired
    AuditValueService auditValueService;
    @Autowired
    AuditProcessService auditProcessService;

    @Override
    public List<AuditGroup> getByProcessId(Long id) {
        return auditGroupMapper.getByProcessId(id);
    }

    @Override
    public AuditGroup getByName(String name){
        return this.baseMapper.selectOne(new QueryWrapper<AuditGroup>().eq("name", name));
    }

    @Override
    public AuditGroup getBySort(BigDecimal sort, Long processId){
        return this.baseMapper.selectOne(new QueryWrapper<AuditGroup>().eq("sort", sort).eq("process_id", processId));
    }

    @Override
    public Integer getGroupStatus(AuditGroup auditGroup, Long ProductNewId) {
        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isEmpty(entryIds)) {
            return AuditGroupVo.STATUS_UNFINISHED;
        }

        Long auditEntryCount = auditEntryService.getCountByIdsAndRequired(entryIds, AuditEntry.REQUIRED);
        Long auditValueRequiredCount = auditValueService.getCountByEntryIdsAndPid(entryIds, ProductNewId, AuditEntry.REQUIRED);
        Long auditValueNotRequiredCount = auditValueService.getCountByEntryIdsAndPid(entryIds, ProductNewId, AuditEntry.NOT_REQUIRED);

        if(Objects.equals(0L, auditEntryCount)) {
            return AuditGroupVo.STATUS_FINISHED;
        }

        if(Objects.equals(0L, auditValueRequiredCount + auditValueNotRequiredCount)) {
            return AuditGroupVo.STATUS_UNFINISHED;
        }

        if(Objects.equals(auditEntryCount, auditValueRequiredCount)) {
            return AuditGroupVo.STATUS_FINISHED;
        }

        return AuditGroupVo.STATUS_EXECUTING;
    }

    @Override
    public KeyProcessAuditGroupVo statusAdjustment(List<KeyProcessAuditGroupVo> keyProcessAuditGroupVos) {
        for(KeyProcessAuditGroupVo vo : keyProcessAuditGroupVos) {
            if(Objects.equals(vo.getStatus(), AuditGroupVo.STATUS_EXECUTING)){
                return vo;
            }

            if(Objects.equals(vo.getStatus(), AuditGroupVo.STATUS_UNFINISHED)){
                vo.setStatus(AuditGroupVo.STATUS_EXECUTING);
                return vo;
            }
        }
        return null;
    }

    @Override
    public R queryList(String type) {
        AuditProcess auditProcess = auditProcessService.getByType(type);
        if(Objects.isNull(auditProcess)) {
            return R.fail("参数错误，未查询到相关流程");
        }

        List<AuditGroupStrawberryVo> data = new ArrayList<>();
        List<AuditGroup> auditGroupList = this.baseMapper.selectList(new QueryWrapper<AuditGroup>().eq("process_id", auditProcess.getId()).orderByAsc("sort"));
        if(CollectionUtils.isEmpty(auditGroupList)) {
            return R.ok(data);
        }

        auditGroupList.forEach(item -> {
            AuditGroupStrawberryVo vo = new AuditGroupStrawberryVo();
            BeanUtils.copyProperties(item, vo);
            data.add(vo);
        });

        return R.ok(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveOne(AuditGroupStrawberryQuery query) {
        AuditProcess auditProcess = auditProcessService.getByType(query.getProcessType());
        if(Objects.isNull(auditProcess)) {
            return R.fail("参数错误，未查询到相关流程");
        }

        AuditGroup auditGroup = this.getByName(query.getName());
        if(Objects.nonNull(auditGroup)) {
            return R.fail("模块名称已存在");
        }

        auditGroup = this.getBySort(query.getSort(), auditProcess.getId());
        if(Objects.nonNull(auditGroup)) {
            return R.fail("排序值重复，请修改");
        }

        auditGroup = new AuditGroup();
        auditGroup.setName(query.getName());
        auditGroup.setSort(query.getSort());
        auditGroup.setProcessId(auditProcess.getId());
        this.baseMapper.insert(auditGroup);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R putOne(AuditGroupStrawberryQuery query) {
        AuditProcess auditProcess = auditProcessService.getByType(query.getProcessType());
        if(Objects.isNull(auditProcess)) {
            return R.fail("参数错误，未查询到相关流程");
        }

        AuditGroup auditGroup = this.baseMapper.selectById(query.getId());
        if(Objects.isNull(auditGroup)) {
            return R.fail("未查询到相关模块，请检查");
        }

        if(!Objects.equals(auditProcess.getId(), auditGroup.getProcessId())) {
            return R.fail("参数错误，模块与流程绑定不一致");
        }

        String fixedgGroup = AuditProcessConstans.getFixedgGroup(query.getId());
        if(!StringUtils.isEmpty(fixedgGroup)) {
            return R.fail("模块不可修改");
        }

        AuditGroup auditGroupOld = this.getByName(query.getName());
        if(Objects.nonNull(auditGroupOld) && !Objects.equals(auditGroupOld.getId(), auditGroup.getId())) {
            return R.fail("模块名称已存在");
        }

        auditGroupOld = this.getBySort(query.getSort(), auditProcess.getId());
        if(Objects.nonNull(auditGroupOld) && !Objects.equals(auditGroupOld.getId(), auditGroup.getId())) {
            return R.fail("排序值重复，请修改");
        }

        auditGroup.setSort(query.getSort());
        auditGroup.setName(query.getName());
        this.baseMapper.updateById(auditGroup);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R reomveOne(Long id) {
        AuditGroup auditGroup = this.baseMapper.selectById(id);
        if(Objects.isNull(auditGroup)) {
            return R.fail("未查询到相关模块，请检查");
        }

        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isNotEmpty(entryIds)) {
            return R.fail("模块有组件绑定，不可删除");
        }

        String fixedgGroup = AuditProcessConstans.getFixedgGroup(id);
        if(!StringUtils.isEmpty(fixedgGroup)) {
            return R.fail("模块不可删除");
        }

        this.baseMapper.deleteById(id);
        return R.ok();
    }


}
