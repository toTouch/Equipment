package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.MaintenanceUserNotifyConfigService;
import com.xiliulou.afterserver.web.query.MaintenanceUserNotifyConfigQuery;
import com.xiliulou.core.controller.BaseController;
import com.xiliulou.core.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zgw
 * @date 2022/5/16 14:31
 * @mood
 */
@RestController
@Slf4j
public class JsonAdminMaintenanceUserNotifyConfigController extends BaseController {

    @Autowired
    MaintenanceUserNotifyConfigService maintenanceUserNotifyConfigService;

    @GetMapping("/admin/maintenance/notify/config")
    public R configInfo(@RequestParam("type")Integer type, @RequestParam(value = "bindId", required = false)Long bindId) {
        return returnPairResult(maintenanceUserNotifyConfigService.queryConfigInfo(type, bindId));
    }

    @PostMapping("/admin/maintenance/notify/config")
    public R saveConfig(@Validated @RequestBody MaintenanceUserNotifyConfigQuery query) {
        return returnPairResult(maintenanceUserNotifyConfigService.saveConfig(query));
    }

    @PutMapping("/admin/maintenance/notify/config")
    public R updateConfig(@RequestBody MaintenanceUserNotifyConfigQuery query) {
        return returnPairResult(maintenanceUserNotifyConfigService.updateConfig(query));
    }

    @PostMapping("/admin/maintenace/test")
    public R testSendMsg(@RequestParam("type") Integer type, @RequestParam("bindId") Long bindId) {
        return returnPairResult(maintenanceUserNotifyConfigService.testSendMsg(type, bindId));
    }
}
