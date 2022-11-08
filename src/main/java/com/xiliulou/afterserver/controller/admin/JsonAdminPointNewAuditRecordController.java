package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.PointNewAuditRecordService;
import com.xiliulou.core.web.R;
import java.util.Objects;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (PointNewAuditRecord)表控制层
 *
 * @author Hardy
 * @since 2022-11-08 13:37:01
 */
@RestController
public class JsonAdminPointNewAuditRecordController {

    @Resource
    private PointNewAuditRecordService pointNewAuditRecordService;

    @GetMapping("admin/pointNew/auditRecord")
    public R queryList(@RequestParam("size") Long size,
                       @RequestParam("offset") Long offset,
                       @RequestParam("pointId") Long pointId) {
        if(Objects.isNull(offset) || offset < 0) {
            offset = 0L;
        }

        if(Objects.isNull(size) || size < 0 || size > 50) {
            size = 50L;
        }
        return pointNewAuditRecordService.queryList(offset, size, pointId);
    }
}
