package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.ServerAuditEntryService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return serverAuditEntryService.getAdminList();
    }

    @PostMapping("admin/server/auditEntry")
    public R saveOne(@RequestBody @Validated ServerAuditEntryQuery serverAuditEntryQuery){
        return serverAuditEntryService.saveOne(serverAuditEntryQuery);
    }

    @PutMapping("admin/server/auditEntry")
    public R putOne(@RequestBody @Validated ServerAuditEntryQuery serverAuditEntryQuery) {
        return serverAuditEntryService.putOne(serverAuditEntryQuery);
    }

    @DeleteMapping("admin/server/auditEntry/{id}")
    public R removeByid(@PathVariable("id")Long id) {
        return serverAuditEntryService.removeByid(id);
    }
}
