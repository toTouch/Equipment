package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.AuditProcessConstans;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.AuditGroupMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditGroupStrawberryQuery;
import com.xiliulou.afterserver.web.vo.AuditGroupStrawberryVo;
import com.xiliulou.afterserver.web.vo.AuditGroupVo;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditGroupVo;
import com.xiliulou.core.exception.CustomBusinessException;
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
    @Autowired
    AuditGroupUpdateLogService auditGroupUpdateLogService;
    @Autowired
    GroupVersionService groupVersionService;

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
    public Integer getGroupStatus(AuditGroup auditGroup, Long productNewId, Integer preStatus) {
        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isEmpty(entryIds)) {
            return AuditGroupVo.STATUS_UNFINISHED;
        }

        Long auditEntryCount = auditEntryService.getCountByIdsAndRequired(entryIds, AuditEntry.REQUIRED);
        Long auditValueRequiredCount = auditValueService.getCountByEntryIdsAndPid(entryIds, productNewId, AuditEntry.REQUIRED);
        Long auditValueNotRequiredCount = auditValueService.getCountByEntryIdsAndPid(entryIds, productNewId, AuditEntry.NOT_REQUIRED);

        //必填组件为零，说明页面都为非必填
        //当为第一个时默认返回绿色， 其他时判断上一个的颜色 如果是绿色则为绿色，其他色置灰
        if(Objects.equals(auditEntryCount, 0L)) {
//            List<AuditGroupUpdateLog> auditGroupUpdateLogs = auditGroupUpdateLogService.list(new QueryWrapper<AuditGroupUpdateLog>().eq("group_id", auditGroup.getId()).eq("pid", productNewId));
//            if(CollectionUtils.isNotEmpty(auditGroupUpdateLogs)) {
//                return AuditGroupVo.STATUS_FINISHED;
//            }
//            return AuditGroupVo.STATUS_UNFINISHED;
            if(Objects.isNull(preStatus)) {
                return AuditGroupVo.STATUS_FINISHED;
            }

            if(Objects.equals(preStatus, AuditGroupVo.STATUS_FINISHED)) {
                return AuditGroupVo.STATUS_FINISHED;
            }

            return AuditGroupVo.STATUS_UNFINISHED;
        }

        //当一个也没填
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
        if( this.baseMapper.insert(auditGroup) < 1) {
            throw  new CustomBusinessException("数据库错误");
        }

        //添加group版本
        groupVersionService.createOrUpdate(auditGroup.getId(), auditGroup.getName());
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

        if(AuditProcessConstans.getFixedgGroup(query.getId())) {
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

        //添加group版本
        groupVersionService.createOrUpdate(auditGroup.getId(), auditGroup.getName());
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

        if(AuditProcessConstans.getFixedgGroup(id)) {
            return R.fail("模块不可删除");
        }

        this.baseMapper.deleteById(id);

        //删除group版本
        groupVersionService.removeByGroupId(id);
        return R.ok();
    }


}
