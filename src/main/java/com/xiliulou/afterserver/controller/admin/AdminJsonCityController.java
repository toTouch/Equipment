package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hardy
 * @date 2021/4/25 0025 16:45
 * @Description: 地区表
 */

@RestController
@RequestMapping("/admin/city")
public class AdminJsonCityController {
    @Autowired
    private CityService cityService;

    @RequestMapping("/get/tree")
    public R cityTree(){
        return cityService.cityTree();
    }
}
