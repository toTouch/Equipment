package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.CompressionRecordService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/compressionRecord")
public class AdminJsonCompressionRecordController {
    @Autowired
    CompressionRecordService compressionRecordService;
    @GetMapping("/list")
    public R list(@RequestParam(value = "id", required = true)Long sn){
        return compressionRecordService.queryRecordList(sn);
    }

}
