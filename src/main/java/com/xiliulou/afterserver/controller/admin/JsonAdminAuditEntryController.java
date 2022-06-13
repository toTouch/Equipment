package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.AuditEntryService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditEntryStrawberryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zgw
 * @date 2022/6/10 14:36
 * @mood
 */
@RestController
@Slf4j
public class JsonAdminAuditEntryController {
    @Autowired
    AuditEntryService auditEntryService;

    @GetMapping("admin/auditEntry")
    public R queryList(@RequestParam("groupId") String groupId){
        return auditEntryService.queryList(groupId);
    }

    @PostMapping("admin/auditEntry")
    public R saveOne(@RequestBody @Validated AuditEntryStrawberryQuery query){
        return auditEntryService.saveOne(query);
    }

    @PutMapping("admin/auditEntry")
    public R putOne(@RequestBody @Validated AuditEntryStrawberryQuery query) {
        return auditEntryService.putOne(query);
    }

    @DeleteMapping("admin/auditEntry/{groupId}/{id}")
    public R removeOne(@PathVariable("groupId")Long groupId, @PathVariable("id")Long id) {
        return auditEntryService.removeOne(groupId, id);
    }
}
