package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.ColorCard;
import com.xiliulou.afterserver.service.ColorCardService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgw
 * @date 2022/3/30 19:29
 * @mood
 */
@RestController
public class JsonAdminColorCardController {

    @Autowired
    ColorCardService colorCardService;

    @GetMapping("admin/colorCard")
    public R queryPull(@RequestParam("name") String name){
        return  colorCardService.queryPull(name);
    }

}
