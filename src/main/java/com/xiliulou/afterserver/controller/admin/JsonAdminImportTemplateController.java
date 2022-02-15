package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.ImportTemplate;
import com.xiliulou.afterserver.service.ImportTemplateService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ImportTemplateQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Hardy
 * @date 2022/2/14 16:48
 * @mood
 */
@RestController
@Slf4j
public class JsonAdminImportTemplateController extends BaseController {
    @Autowired
    ImportTemplateService importTemplateService;

    @PostMapping("admin/importTemplate/upload")
    public R upload(@RequestBody ImportTemplateQuery importTemplateQuery){
        return importTemplateService.upload(importTemplateQuery);
    }

    @GetMapping("admin/importTemplate/info")
    public R info(@RequestParam("type") String type, HttpServletResponse response){
         return importTemplateService.infoByType(type, response);
    }
}
