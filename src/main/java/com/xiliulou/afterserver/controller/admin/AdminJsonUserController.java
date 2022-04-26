package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.config.MinioConfig;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.MinioUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.util.password.PasswordUtils;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.core.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:11
 **/
@RestController
@Slf4j
public class AdminJsonUserController extends BaseController {
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @PostMapping("admin/register")
    public R register(@RequestBody User user) {
        return super.returnPairResult(userService.register(user));
    }

    //@PostMapping("auth/login")
    //public R login(@RequestBody User user) {
    //    return super.returnPairResult(userService.login(user));
    //}

    @GetMapping("admin/user/page")
    public R page(@RequestParam("offset") Long offset, @RequestParam("size") Long size, @RequestParam(value = "username",required = false) String username, HttpServletRequest request) {
        return userService.list(offset,size,username);

    }

    @GetMapping("admin/user/page/pull")
    public R pagePull(@RequestParam("offset") Long offset, @RequestParam("size") Long size, @RequestParam(value = "username",required = false) String username, HttpServletRequest request) {
        return userService.list(offset,size,username);

    }
   @PutMapping("/admin/user")
    public R updateUser(@RequestBody User user,HttpServletRequest request){
//       User userDb = this.userService.getBaseMapper().selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName()));
//       if (Objects.nonNull(userDb)) {
//           return R.fail("用户名已存在");
//       }
        if(Objects.isNull(user)){
            return R.fail("请传入用户信息");
        }
       if(Objects.isNull(user.getPassWord())){
           return R.fail("请传入用户密码");
       }
       user.setPassWord(PasswordUtils.encode(user.getPassWord()));
      return R.ok(userService.updateById(user));
   }

   @DeleteMapping("/admin/user/{uid}")
    public R delUser(@PathVariable("uid") Long uid){
        return R.ok(userService.removeById(uid));
   }

    @GetMapping("admin/user/menu")
    public R getUserMenu() {
        return returnPairResult(roleService.getMenuByUid());
    }

    @PostMapping("admin/user/role/bind")
    public R bindUserRole(@RequestParam("uid") Long uid, @RequestParam("roleIds") String jsonRoleIds) {
        List<Long> roleIds = JsonUtil.fromJsonArray(jsonRoleIds, Long.class);
        if (!DataUtil.collectionIsUsable(roleIds)) {
            return R.fail("SYSTEM.0002", "参数不合法");
        }

        return returnPairResult(roleService.bindUserRole(uid, roleIds));
    }

}
