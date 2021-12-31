package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.service.PermissionResourceService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.PermissionResourceQuery;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.core.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin")
public class JsonAdminUserPermissionController extends BaseController {

    @Autowired
    PermissionResourceService permissionResourceService;

    @GetMapping("/permission/list")
    public R getList() {
        return returnPairResult(permissionResourceService.getList());
    }

    @PostMapping("/permission/add")
    public R addPermission(@RequestBody PermissionResourceQuery permissionResourceQuery) {
        return returnPairResult(permissionResourceService.addPermissionResource(permissionResourceQuery));
    }

    @PutMapping("/permission/update")
    public R updatePermission(@RequestBody PermissionResourceQuery permissionResourceQuery) {
        if (permissionResourceQuery.getId() == 0) {
            return R.fail("SYSTEM.0002", "id不能为空！");
        }
        return returnPairResult(permissionResourceService.updatePermissionResource(permissionResourceQuery));
    }

    @DeleteMapping("/permission/delete/{id}")
    public R deletePermission(@PathVariable("id") Long permissionId) {
        if (Objects.isNull(permissionId)) {
            return R.fail("SYSTEM.0002", "id不能为空");
        }

        return returnPairResult(permissionResourceService.deletePermission(permissionId));
    }


    @PostMapping("/permission/role/bind")
    public R bindPermissionToRole(@RequestParam("roleId") Long rid, @RequestParam("pids") String jsonPids) {
        List<Long> pid = JsonUtil.fromJsonArray(jsonPids, Long.class);
        if (!DataUtil.collectionIsUsable(pid)) {
            return R.fail("SYSTEM.0002", "参数不合法");
        }
        return returnPairResult(permissionResourceService.bindPermissionToRole(rid, pid));
    }


    @GetMapping("/permission/role/info/{id}")
    public R getPermissionsByRole(@PathVariable("id") Long rid) {
        if(Objects.isNull(rid)) {
            return R.fail("SYSTEM.0002", "参数不合法");
        }

        return returnPairResult(permissionResourceService.getPermissionsByRole(rid));
    }


}
