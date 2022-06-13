package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.AuditGroupService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditGroupStrawberryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zgw
 * @date 2022/6/10 11:06
 * @mood
 */
@RestController
@Slf4j
public class JsonAdminAuditGroupController {
    @Autowired
    AuditGroupService auditGroupService;

    @GetMapping("/admin/auditGroup")
    public R queryList(@RequestParam("processType") String processType){
        return auditGroupService.queryList(processType);
    }

    @PostMapping("/admin/auditGroup")
    public R saveOne(@RequestBody @Validated AuditGroupStrawberryQuery auditGroupStrawberryQuery){
        return auditGroupService.saveOne(auditGroupStrawberryQuery);
    }

    @PutMapping("/admin/auditGroup")
    public R putOne(@RequestBody @Validated AuditGroupStrawberryQuery auditGroupStrawberryQuery){
        return auditGroupService.putOne(auditGroupStrawberryQuery);
    }

    @DeleteMapping("/admin/auditGroup/{id}")
    public R reomveOne(@PathVariable("id")Long id){
        return auditGroupService.reomveOne(id);
    }
}
