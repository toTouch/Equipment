package com.xiliulou.afterserver.controller.factory;

import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author zgw
 * @date 2022/3/3 13:40
 * @mood
 */
@RestController
@RequestMapping("/admin/factory")
public class JsonAdminUserController {

    @Autowired
    SupplierService supplierService;

    @GetMapping("/user/info")
    public R getUserInfo(){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("请使用工厂用户登录");
        }
        return supplierService.getUserInfo();
    }
}
