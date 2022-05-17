package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.LoginInfo;
import com.xiliulou.afterserver.entity.MaintenanceUserNotifyConfig;
import com.xiliulou.afterserver.web.query.MaintenanceUserNotifyConfigQuery;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author zgw
 * @date 2022/5/16 14:28
 * @mood
 */
public interface MaintenanceUserNotifyConfigService extends IService<MaintenanceUserNotifyConfig> {

    Pair<Boolean, Object> queryConfigInfo();

    Pair<Boolean, Object> saveConfig(MaintenanceUserNotifyConfigQuery query);

    Pair<Boolean, Object> updateConfig(MaintenanceUserNotifyConfigQuery query);

    Pair<Boolean, Object> testSendMsg(Integer type, Long bindId);

    List<MaintenanceUserNotifyConfig> queryAll();

    MaintenanceUserNotifyConfig queryByPermissions(Integer type, Long bindId);
}
