package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.ServerAuditEntryService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import org.springframework.beans.factory.annotation.Autowired;
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

    public R saveOne(ServerAuditEntryQuery serverAuditEntryQuery){
        return serverAuditEntryService.saveOne(serverAuditEntryQuery);
    }
}
