package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xiliulou.afterserver.constant.MqConstant;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.entity.mq.notify.MqNotifyCommon;
import com.xiliulou.afterserver.entity.mq.notify.MqWorkOrderAuditNotify;
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
    public Pair<Boolean, Object> queryConfigInfo() {
        List<MaintenanceUserNotifyConfig> maintenanceUserNotifyConfigList = this.queryAll();
        if(CollectionUtils.isEmpty(maintenanceUserNotifyConfigList)){
            return Pair.of(true,null);
        }

        List<MaintenanceUserNotifyConfigVo> data = new ArrayList<>();
        maintenanceUserNotifyConfigList.forEach(item -> {
            MaintenanceUserNotifyConfigVo vo = new MaintenanceUserNotifyConfigVo();
            vo.setId(item.getId());
            vo.setPermissions(item.getPermissions());
            if(StringUtils.isEmpty(item.getPhones())) {
               vo.setPhones(Lists.newArrayList());
               return;
            }
            vo.setPhones(JsonUtil.fromJsonArray(item.getPhones(), String.class));
            data.add(vo);
        });

        Map<String, Object> result = new HashMap<>(2);
        result.put("data", data);
        result.put("qrUrl", MqConstant.QR_URL);
        return Pair.of(true, result);
    }

    @Override
    public Pair<Boolean, Object> saveConfig(MaintenanceUserNotifyConfigQuery query) {
        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = this.queryByPermissions(query.getPermission());
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
                .build();
        save(build);
        return Pair.of(true, null);
    }

    @Override
    public Pair<Boolean, Object> updateConfig(MaintenanceUserNotifyConfigQuery query) {
        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = this.queryByPermissions(query.getPermission());
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
                .id(maintenanceUserNotifyConfig.getId())
                .permissions(query.getPermission())
                .phones(query.getPhones())
                .updateTime(System.currentTimeMillis())
                .build();
        this.updateById(build);
        return Pair.of(true, null);
    }

    @Override
    public Pair<Boolean, Object> testSendMsg(Integer permission) {
        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = this.queryByPermissions(permission);
        if(Objects.isNull(maintenanceUserNotifyConfig) || StringUtils.isEmpty(maintenanceUserNotifyConfig.getPhones())) {
            return Pair.of(false, "请先配置手机号");
        }

        if (!redisService.setNx(MqConstant.CACHE_TENANT_MAINTENANCE_USER_CONFIG_TEST + permission, "ok", TimeUnit.MINUTES.toMillis(5), false)) {
            return Pair.of(false, "5分钟之内只能测试一次");
        }

        List<String> phones = JsonUtil.fromJsonArray(maintenanceUserNotifyConfig.getPhones(), String.class);
        if(Objects.equals(permission, MaintenanceUserNotifyConfig.P_REVIEW)){
            testReviewNotify(phones);
        }
        return null;
    }

    @Override
    public List<MaintenanceUserNotifyConfig> queryAll(){
        return maintenanceUserNotifyConfigMapper.selectList(null);
    }

    @Override
    public MaintenanceUserNotifyConfig queryByPermissions(Integer permissions) {
        return maintenanceUserNotifyConfigMapper.selectOne(new QueryWrapper<MaintenanceUserNotifyConfig>().eq("permissions", permissions));
    }

    public void testReviewNotify(List<String> phones) {
        Long time = System.currentTimeMillis();

        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        phones.forEach(p -> {
            MqNotifyCommon<MqWorkOrderAuditNotify> query = new MqNotifyCommon<>();
            query.setType(MqNotifyCommon.TYPE_AFTER_SALES_AUDIT);
            query.setTime(time);
            query.setPhone(p);

            MqWorkOrderAuditNotify mqWorkOrderAuditNotify = new MqWorkOrderAuditNotify();
            mqWorkOrderAuditNotify.setWorkOrderNo("test");
            mqWorkOrderAuditNotify.setSubmitTime(simp.format(new Date(time)));
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
}
