package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xiliulou.afterserver.constant.MqConstant;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.entity.mq.notify.MqNotifyCommon;
import com.xiliulou.afterserver.entity.mq.notify.MqPointNewAuditNotify;
import com.xiliulou.afterserver.entity.mq.notify.MqWorkOrderAuditNotify;
import com.xiliulou.afterserver.entity.mq.notify.MqWorkOrderServerNotify;
import com.xiliulou.afterserver.mapper.LoginInfoMapper;
import com.xiliulou.afterserver.mapper.MaintenanceUserNotifyConfigMapper;
import com.xiliulou.afterserver.service.MaintenanceUserNotifyConfigService;
import com.xiliulou.afterserver.web.query.MaintenanceUserNotifyConfigQuery;
import com.xiliulou.afterserver.web.vo.MaintenanceUserNotifyConfigVo;
import com.xiliulou.cache.redis.RedisService;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.mq.service.RocketMqService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zgw
 * @date 2022/5/16 14:29
 * @mood
 */
@Service("maintenanceUserNotifyConfigService")
@Slf4j
public class MaintenanceUserNotifyConfigServiceImpl extends ServiceImpl<MaintenanceUserNotifyConfigMapper, MaintenanceUserNotifyConfig> implements MaintenanceUserNotifyConfigService {

    @Resource
    MaintenanceUserNotifyConfigMapper maintenanceUserNotifyConfigMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    RocketMqService rocketMqService;

    @Override
    public Pair<Boolean, Object> queryConfigInfo(Integer type, Long bindId) {
        if(Objects.equals(type, MaintenanceUserNotifyConfig.TYPE_SERVER)) {
            if(Objects.isNull(bindId)) {
                return Pair.of(false, "请选择服务商");
            }
        }

        if(Objects.equals(type, MaintenanceUserNotifyConfig.TYPE_REVIEW)) {
            bindId = null;
        }

        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = this.queryByPermissions(type, bindId);
        if(Objects.isNull(maintenanceUserNotifyConfig)){
            return Pair.of(true,null);
        }


        MaintenanceUserNotifyConfigVo vo = new MaintenanceUserNotifyConfigVo();
        vo.setId(maintenanceUserNotifyConfig.getId());
        vo.setPermissions(maintenanceUserNotifyConfig.getPermissions());

        if(!StringUtils.isEmpty(maintenanceUserNotifyConfig.getPhones())) {
            vo.setPhones(JsonUtil.fromJsonArray(maintenanceUserNotifyConfig.getPhones(), String.class));
        }

        Map<String, Object> result = new HashMap<>(2);
        result.put("data", vo);
        result.put("qrUrl", MqConstant.QR_URL);
        return Pair.of(true, result);
    }

    @Override
    public Pair<Boolean, Object> saveConfig(MaintenanceUserNotifyConfigQuery query) {
        if(Objects.equals(query.getType(), MaintenanceUserNotifyConfig.TYPE_SERVER)) {
            if(Objects.isNull(query.getBindId())) {
                return Pair.of(false, "请选择服务商");
            }
        }

        if(Objects.equals(query.getType(), MaintenanceUserNotifyConfig.TYPE_REVIEW)) {
            query.setBindId(null);
        }

        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = this.queryByPermissions(query.getType(), query.getBindId());
        if(Objects.nonNull(maintenanceUserNotifyConfig)) {
            return Pair.of(false, "已存在配置，无法重复创建");
        }

        if(!StringUtils.isEmpty(query.getPhones())) {
            List phones =  JsonUtil.fromJsonArray(query.getPhones(), String.class);
            if(phones.size() > 5) {
                return Pair.of(false, "最大可配置五个手机号");
            }
        }

        MaintenanceUserNotifyConfig build = MaintenanceUserNotifyConfig.builder()
                .permissions(query.getPermission())
                .phones(query.getPhones())
                .createTime(System.currentTimeMillis())
                .updateTime(System.currentTimeMillis())
                .type(query.getType())
                .bindId(query.getBindId())
                .build();
        save(build);
        return Pair.of(true, null);
    }

    @Override
    public Pair<Boolean, Object> updateConfig(MaintenanceUserNotifyConfigQuery query) {
        if(Objects.equals(query.getType(), MaintenanceUserNotifyConfig.TYPE_SERVER)) {
            if(Objects.isNull(query.getBindId())) {
                return Pair.of(false, "请选择服务商");
            }
        }

        if(Objects.equals(query.getType(), MaintenanceUserNotifyConfig.TYPE_REVIEW)) {
            query.setBindId(null);
        }

        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = this.queryByPermissions(query.getType(), query.getBindId());
        if(Objects.isNull(maintenanceUserNotifyConfig)) {
            return Pair.of(false, "未查询到相关配置");
        }

        if(!StringUtils.isEmpty(query.getPhones())) {
            List phones =  JsonUtil.fromJsonArray(query.getPhones(), String.class);
            if(phones.size() > 5) {
                return Pair.of(false, "最大可配置五个手机号");
            }
        }

        MaintenanceUserNotifyConfig build = MaintenanceUserNotifyConfig.builder()
                .id(maintenanceUserNotifyConfig.getId())
                .permissions(query.getPermission())
                .phones(query.getPhones())
                .updateTime(System.currentTimeMillis())
                .type(query.getType())
                .bindId(query.getBindId())
                .build();
        this.updateById(build);
        return Pair.of(true, null);
    }

    @Override
    public Pair<Boolean, Object> testSendMsg(Integer type, Long bindId) {
        if(Objects.equals(type, MaintenanceUserNotifyConfig.TYPE_SERVER)) {
            if(Objects.isNull(bindId)) {
                return Pair.of(false, "请选择服务商");
            }
        }

        if(Objects.equals(type, MaintenanceUserNotifyConfig.TYPE_REVIEW)) {
            bindId = null;
        }

        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = this.queryByPermissions(type, bindId);
        if(Objects.isNull(maintenanceUserNotifyConfig) || StringUtils.isEmpty(maintenanceUserNotifyConfig.getPhones())) {
            return Pair.of(false, "请先配置手机号");
        }

        if (!redisService.setNx(MqConstant.CACHE_TENANT_MAINTENANCE_USER_CONFIG_TEST + maintenanceUserNotifyConfig.getId(), "ok", TimeUnit.MINUTES.toMillis(5), false)) {
            return Pair.of(false, "5分钟之内只能测试一次");
        }

        List<String> phones = JsonUtil.fromJsonArray(maintenanceUserNotifyConfig.getPhones(), String.class);

        if(Objects.equals(type, MaintenanceUserNotifyConfig.TYPE_REVIEW)){
            testReviewNotify(phones, maintenanceUserNotifyConfig.getPermissions());
        }else if(Objects.equals(type, MaintenanceUserNotifyConfig.TYPE_SERVER)){
            testServerNotify(phones, maintenanceUserNotifyConfig.getPermissions());
        }

        return Pair.of(true, null);
    }

    @Override
    public List<MaintenanceUserNotifyConfig> queryAll(){
        return maintenanceUserNotifyConfigMapper.selectList(null);
    }

    @Override
    public MaintenanceUserNotifyConfig queryByPermissions(Integer type, Long bindId) {
        LambdaQueryWrapper<MaintenanceUserNotifyConfig> query = new LambdaQueryWrapper<>();
        query.eq(Objects.nonNull(type), MaintenanceUserNotifyConfig::getType, type);
        query.eq(Objects.nonNull(bindId), MaintenanceUserNotifyConfig::getBindId, bindId);

        return maintenanceUserNotifyConfigMapper.selectOne(query);
    }

    public void testServerNotify(List<String> phones, Integer permissions) {

        if(Objects.equals(permissions & MaintenanceUserNotifyConfig.P_SERVER, MaintenanceUserNotifyConfig.P_SERVER)) {
            phones.forEach(p -> {
                MqNotifyCommon<MqWorkOrderServerNotify> query = new MqNotifyCommon<>();
                query.setType(MqNotifyCommon.TYPE_AFTER_SALES_SERVER);
                query.setTime(System.currentTimeMillis());
                query.setPhone(p);

                MqWorkOrderServerNotify mqWorkOrderServerNotify = new MqWorkOrderServerNotify();
                mqWorkOrderServerNotify.setWorkOrderNo("test");
                mqWorkOrderServerNotify.setOrderTypeName("test");
                mqWorkOrderServerNotify.setPointName("test");
                mqWorkOrderServerNotify.setAssignmentTime("1970-01-01 00:00:00");
                query.setData(mqWorkOrderServerNotify);

                Pair<Boolean, String> result = rocketMqService.sendSyncMsg(MqConstant.TOPIC_MAINTENANCE_NOTIFY, JsonUtil.toJson(query), MqConstant.TAG_AFTER_SALES, "", 0);
                if (!result.getLeft()) {
                    log.error("SEND WORKORDER SERVER MQ ERROR! no={}, msg={}", "test", result.getRight());
                }
            });
        }
    }

    public void testReviewNotify(List<String> phones, Integer permissions) {
        if(Objects.equals(permissions & MaintenanceUserNotifyConfig.P_REVIEW, MaintenanceUserNotifyConfig.P_REVIEW)) {
            phones.forEach(p -> {
                MqNotifyCommon<MqWorkOrderAuditNotify> query = new MqNotifyCommon<>();
                query.setType(MqNotifyCommon.TYPE_AFTER_SALES_AUDIT);
                query.setTime(System.currentTimeMillis());
                query.setPhone(p);

                MqWorkOrderAuditNotify mqWorkOrderAuditNotify = new MqWorkOrderAuditNotify();
                mqWorkOrderAuditNotify.setWorkOrderNo("test");
                mqWorkOrderAuditNotify.setSubmitTime("1970-01-01 00:00:00");
                mqWorkOrderAuditNotify.setOrderTypeName("test");
                mqWorkOrderAuditNotify.setPointName("test");
                mqWorkOrderAuditNotify.setSubmitUName("test");
                query.setData(mqWorkOrderAuditNotify);

                Pair<Boolean, String> result = rocketMqService.sendSyncMsg(MqConstant.TOPIC_MAINTENANCE_NOTIFY, JsonUtil.toJson(query), MqConstant.TAG_AFTER_SALES, "", 0);
                if (!result.getLeft()) {
                    log.error("SEND WORKORDER AUDIT MQ ERROR! no={}, msg={}", "test", result.getRight());
                }
            });
        }

        if(Objects.equals(permissions & MaintenanceUserNotifyConfig.P_AUDIT_FAILED, MaintenanceUserNotifyConfig.P_AUDIT_FAILED)) {
            phones.forEach(p -> {
                MqNotifyCommon<MqPointNewAuditNotify> query = new MqNotifyCommon<>();
                query.setType(MqNotifyCommon.TYPE_AFTER_SALES_POINT_AUDIT);
                query.setTime(System.currentTimeMillis());
                query.setPhone(p);

                MqPointNewAuditNotify mqPointNewAuditNotify = new MqPointNewAuditNotify();
                mqPointNewAuditNotify.setPointName("test");
                mqPointNewAuditNotify.setAuditUserName("test");
                mqPointNewAuditNotify.setAuditRemark("test");
                mqPointNewAuditNotify.setAuditResult("test");
                query.setData(mqPointNewAuditNotify);

                Pair<Boolean, String> result = rocketMqService.sendSyncMsg(MqConstant.TOPIC_MAINTENANCE_NOTIFY, JsonUtil.toJson(query), MqConstant.TAG_AFTER_SALES, "", 0);
                if (!result.getLeft()) {
                    log.error("SEND WORKORDER AUDIT MQ ERROR! no={}, msg={}", "test", result.getRight());
                }
            });
        }

    }
}
