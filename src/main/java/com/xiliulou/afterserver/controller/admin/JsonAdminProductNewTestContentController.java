package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.ProductNewTestContentService;
import com.xiliulou.core.web.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (ProductNewTestContent)表控制层
 *
 * @author Hardy
 * @since 2023-04-26 17:22:45
 */
@RestController
public class JsonAdminProductNewTestContentController {
    /**
     * 服务对象
     */
    @Resource
    private ProductNewTestContentService productNewTestContentService;

    @GetMapping()
    public R queryInfoByPid(@RequestParam("pid")Long pid) {
        return productNewTestContentService.queryInfoByPid(pid);
    }

}
