package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.WorkAuditNotifyService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zgw
 * @date 2022/4/2 18:23
 * @mood
 */
@RestController
public class JsonAdminWorkAuditNotifyController {

    @Autowired
    WorkAuditNotifyService workAuditNotifyService;

    @GetMapping("/admin/WorkAuditNotify/list")
    public R queryList(@RequestParam("size") Integer size,
                       @RequestParam("offset") Integer offset,
                       @RequestParam("type") Integer type){
        return workAuditNotifyService.queryList(size, offset, type);
    }

    @GetMapping("/admin/WorkAuditNotify/read/notify/{id}")
    public R readNotify(@PathVariable("id") Long id){
        return workAuditNotifyService.readNotify(id);
    }

    @GetMapping("/admin/WorkAuditNotify/read/notify/all")
    public R readNotifyAll(){
        return workAuditNotifyService.readNotifyAll();
    }
}
