package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.AuditProcessService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgw
 * @date 2022/6/13 13:39
 * @mood
 */
@RestController
@Slf4j
public class JsonAdminAuditProcessController {
    @Autowired
    private AuditProcessService auditProcessService;

    /**
     * 获取关键流程内容
     */
    @GetMapping("admin/audit/process/info")
    public R getKeyProcess(@RequestParam("no")String no, @RequestParam("type")String type, @RequestParam(value = "groupId", required = false)Long groupId){
        return auditProcessService.getKeyProcess(no, type, groupId);
    }
}
