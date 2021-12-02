package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.Role;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.RoleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin")
public class AdminJsonUserRoleController extends BaseController {

    @Autowired
    RoleService roleService;

    @GetMapping("/role/list")
    public R getRoleList(){
        return (R<List<Role>>) roleService.findRoleAll();
    }

    @PostMapping("role/add")
    public R getRoleAdd(@RequestBody RoleQuery roleQuery){
        return roleService.addRole(roleQuery);
    }

    @PutMapping("/role/update")
    public R getRoleUpdate(@RequestBody RoleQuery roleQuery){
        if (Objects.isNull(roleQuery.getId())) {
            return R.fail("SYSTEM.0002", "id不能为空");
        }

        return roleService.updateRole(roleQuery);
    }


    @DeleteMapping("/role/delete/{id}")
    public R deleteRole(@PathVariable Long id) {
        if (Objects.isNull(id)) {
            return R.fail("SYSTEM.0002", "id不能为空");
        }

        return returnPairResult(roleService.deleteRole(id));
    }


    @GetMapping("/role/uid/info/{uid}")
    public R queryRoleIds(@PathVariable("uid") Long uid) {
        if (Objects.isNull(uid)) {
            return R.fail("SYSTEM.0002", "参数错误");
        }

        return returnPairResult(roleService.findBindUidRids(uid));
    }
}
