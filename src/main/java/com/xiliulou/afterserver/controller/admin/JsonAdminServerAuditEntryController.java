package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.ServerAuditEntryService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgw
 * @date 2022/6/20 11:30
 * @mood
 */
@RestController
public class JsonAdminServerAuditEntryController {
    @Autowired
    ServerAuditEntryService serverAuditEntryService;

    @GetMapping("admin/server/auditEntry")
    public R getList(){
        return serverAuditEntryService.getList();
    }

    @PostMapping("admin/server/auditEntry")
    public R saveOne(ServerAuditEntryQuery serverAuditEntryQuery){
        return serverAuditEntryService.saveOne(serverAuditEntryQuery);
    }

    @PutMapping("admin/server/auditEntry")
    public R putOne(ServerAuditEntryQuery serverAuditEntryQuery) {
        return serverAuditEntryService.putOne(serverAuditEntryQuery);
    }
}
