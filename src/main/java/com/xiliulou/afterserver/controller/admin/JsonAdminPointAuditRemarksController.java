package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.PointAuditRemarksService;
import com.xiliulou.afterserver.util.R;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class JsonAdminPointAuditRemarksController {

    @Autowired
    PointAuditRemarksService pointAuditRemarksService;

    @PostMapping("admin/point/auditRemarks")
    public R saveOne(@RequestParam("remarks") String remarks){
        return pointAuditRemarksService.saveOne(remarks);
    }

    @GetMapping("admin/point/auditRemarks")
    public R getList(){
        return pointAuditRemarksService.getList();
    }

    @Delete("admin/point/auditRemarks/{id}")
    public R deleteOne(@PathVariable("id") Long id){
        return pointAuditRemarksService.deleteOne(id);
    }

    @Update("admin/point/auditRemarks")
    public R updateOne(@RequestParam("remarks") String remarks, @RequestParam("id") Long id){
        return pointAuditRemarksService.updateOne(remarks, id);
    }
}
