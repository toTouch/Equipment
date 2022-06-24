package com.xiliulou.afterserver.controller.factory;

import com.xiliulou.afterserver.entity.GroupVersion;
import com.xiliulou.afterserver.service.GroupVersionService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/23 16:24
 * @mood
 */
@RestController
@RequestMapping
@Slf4j
//  "/admin/factory"
public class JsonAdminGroupVersionController {

    @Autowired
    GroupVersionService groupVersionService;

    @GetMapping("/admin/factory/group/version")
    public R getList(@RequestParam("type") String type){
        return groupVersionService.getList(type);
    }

    @PostMapping("/admin/factory/check/group/version")
    public R checkGroupVersion(@RequestParam("groupVersionList")String groupVersionList, @RequestParam("type") String type){
        return groupVersionService.checkGroupVersion(JsonUtil.fromJsonArray(groupVersionList, GroupVersion.class), type);
    }
}
