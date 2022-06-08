package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.ProductNewStatusSortConstants;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.AuditProcessMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.KeyProcessQuery;
import com.xiliulou.afterserver.web.vo.AuditProcessVo;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditEntryVo;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditGroupVo;
import com.xiliulou.afterserver.web.vo.KeyProcessVo;
import com.xiliulou.core.json.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zgw
 * @date 2022/6/6 18:26
 * @mood
 */
@Service
public class AuditProcessServiceImpl extends ServiceImpl<AuditProcessMapper, AuditProcess> implements AuditProcessService {

    @Resource
    AuditProcessMapper auditProcessMapper;
    @Autowired
    AuditGroupService auditGroupService;
    @Autowired
    AuditEntryService auditEntryService;
    @Autowired
    AuditValueService auditValueService;
    @Autowired
    ProductNewService productNewService;
    @Autowired
    UserService userService;
    @Autowired
    BatchService batchService;

    @Override
    public AuditProcessVo createTestAuditProcessVo() {
        AuditProcessVo auditProcessVo = new AuditProcessVo();
        auditProcessVo.setName("老化测试");
        auditProcessVo.setType("test");
        return auditProcessVo;
    }

    @Override
    public Integer getAuditProcessStatus(AuditProcess auditProcess, ProductNew productNew) {

        List<AuditGroup> auditGroupList = auditGroupService.getByProcessId(auditProcess.getId());
        //如果分组为空，那么流程状态根随柜机状态
        if(CollectionUtils.isEmpty(auditGroupList)) {
            return getStatusByProductNewStatus(auditProcess.getType(), productNew.getStatus());
        }

        //统计所有分组状态的个数
        Set<Integer> groupStatusSet = new HashSet<>(3);

        auditGroupList.parallelStream().forEach(group -> {
            Integer status = auditGroupService.getGroupStatus(group, productNew.getId());
            groupStatusSet.add(status);
        });

        //当分组状态为多个一定是 正在执行 状态
        if(groupStatusSet.size() > 1) {
            return AuditProcessVo.STATUS_EXECUTING;
        }

        if(groupStatusSet.contains(AuditProcessVo.STATUS_UNFINISHED) ){
            return AuditProcessVo.STATUS_UNFINISHED;
        }

        return AuditProcessVo.STATUS_FINISHED;
    }

    @Override
    public void processStatusAdjustment(List<AuditProcessVo> auditProcessVos) {
        if(CollectionUtils.isEmpty(auditProcessVos)){
            return;
        }

        for(AuditProcessVo item : auditProcessVos){
            if (Objects.equals(item.getType(), AuditProcess.TYPE_PRE)) {
                item.setStatus(AuditProcessVo.STATUS_EXECUTING);
                return;
            }
        }
    }

    @Override
    public R getKeyProcess(String no, String type, Long groupId) {
        ProductNew productNew = productNewService.queryByNo(no);
        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)) {
            return R.fail("未查询到相关用户");
        }

        if (Objects.isNull(productNew)) {
            return R.fail("资产编码不存在");
        }

        Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(batch)) {
            return R.fail(null, "柜机未绑定批次，请重新登陆");
        }

        User user = userService.getUserById(uid);
        if (Objects.isNull(user)) {
            return R.fail("未查询到相关用户");
        }

        if (!Objects.equals(productNew.getSupplierId(), user.getThirdId())) {
            return R.fail(null, "柜机厂家与登录厂家不一致，请重新登陆");
        }

        //查询组件
        AuditProcess auditProcess = getByType(type);
        if(Objects.isNull(auditProcess)) {
            return R.fail(null, "未查询到相关流程信息");
        }

        KeyProcessVo keyProcessVo = new KeyProcessVo();
        keyProcessVo.setNo(productNew.getNo());
        keyProcessVo.setBatchId(batch.getId());
        keyProcessVo.setBatchNo(batch.getBatchNo());

        //如果流程分组为空，直接返回
        List<AuditGroup> byProcessId = auditGroupService.getByProcessId(auditProcess.getId());
        if(CollectionUtils.isEmpty(byProcessId)) {
            return R.ok(keyProcessVo);
        }

        //统计分组状态
        List<KeyProcessAuditGroupVo> keyProcessAuditGroupVos = new ArrayList(10);
        byProcessId.parallelStream().forEachOrdered(item -> {
            KeyProcessAuditGroupVo keyProcessAuditGroupVo = new KeyProcessAuditGroupVo();
            keyProcessAuditGroupVos.add(keyProcessAuditGroupVo);

            BeanUtils.copyProperties(item, keyProcessAuditGroupVo);
            Integer status = auditGroupService.getGroupStatus(item, productNew.getId());
            keyProcessAuditGroupVo.setStatus(status);
        });

        //调整分组状态（如果不存在正在执行，调整第一个未完成为正在执行，没有未完成返回空）
        KeyProcessAuditGroupVo executingGroup = auditGroupService.statusAdjustment(keyProcessAuditGroupVos);
        keyProcessVo.setKeyProcessAuditGroupList(keyProcessAuditGroupVos);

        //如果正在执行分组为空，并且不查询已完成分组则直接返回
        if(Objects.isNull(executingGroup) && Objects.isNull(groupId)) {
            return R.ok(keyProcessVo);
        }

        if(Objects.isNull(groupId)) {
            groupId = executingGroup.getId();
        }
        keyProcessVo.setGroupId(groupId);

        AuditGroup groupById = auditGroupService.getById(groupId);
        if(Objects.isNull(groupById)) {
            return R.ok(keyProcessVo);
        }

        //查询分组下的组件和值
        List<KeyProcessAuditEntryVo> keyProcessAuditEntryVos = auditEntryService.getVoByEntryIds(JsonUtil.fromJsonArray(groupById.getEntryIds(), Long.class), productNew.getId());
        keyProcessVo.setKeyProcessAuditEntryList(keyProcessAuditEntryVos);

        return R.ok(keyProcessVo);
    }

    @Override
    public R putKeyProcess(KeyProcessQuery keyProcessQuery) {
        AuditGroup groupById = auditGroupService.getById(keyProcessQuery.getGroupId());
        if(Objects.isNull(groupById)) {
            return R.fail(null, "未查询到相关分组信息");
        }

        if(CollectionUtils.isEmpty(keyProcessQuery.getAuditEntryQueryList())) {
            return R.ok();
        }

        List<>
    }

    public AuditProcess getByType(String type) {
        return auditProcessMapper.selectOne(new QueryWrapper<AuditProcess>().eq("type", type));
    }

    private Integer getStatusByProductNewStatus(String type, Integer status){
        Integer auditProcessStatus = null;
        if(Objects.equals(type, AuditProcess.TYPE_PRE)) {
            auditProcessStatus = 7;
        }else{
            auditProcessStatus = 8;
        }
        Double auditProcessValue = ProductNewStatusSortConstants.acquireStatusValue(auditProcessStatus);
        Double statusValue = ProductNewStatusSortConstants.acquireStatusValue(status);
        if( auditProcessValue < statusValue) {
            return AuditProcessVo.STATUS_FINISHED;
        }
        if(auditProcessValue > statusValue) {
            return AuditProcessVo.STATUS_UNFINISHED;
        }

        return AuditProcessVo.STATUS_EXECUTING;
    }
}
