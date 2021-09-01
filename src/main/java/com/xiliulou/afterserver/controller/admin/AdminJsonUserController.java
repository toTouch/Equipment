package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.config.MinioConfig;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.MinioUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.password.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("admin/register")
    public R register(@RequestBody User user) {
        return super.returnPairResult(userService.register(user));
    }

    @PostMapping("auth/login")
    public R login(@RequestBody User user) {
        return super.returnPairResult(userService.login(user));
    }

    @GetMapping("admin/user/page")
    public R page(@RequestParam("offset") Long offset, @RequestParam("size") Long size, @RequestParam(value = "username",required = false) String username, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        if (uid > 3){
            return R.fail("没有权限");
        }
        return userService.list(offset,size,username);

    }

    @GetMapping("admin/user/page/pull")
    public R pagePull(@RequestParam("offset") Long offset, @RequestParam("size") Long size, @RequestParam(value = "username",required = false) String username, HttpServletRequest request) {
        return userService.list(offset,size,username);

    }
   @PutMapping("/admin/user")
    public R updateUser(@RequestBody User user,HttpServletRequest request){
       Long uid = (Long) request.getAttribute("uid");
       if (uid > 3){
           return R.fail("没有权限");
       }

//       User userDb = this.userService.getBaseMapper().selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName()));
//       if (Objects.nonNull(userDb)) {
//           return R.fail("用户名已存在");
//       }

      return R.ok(userService.updateById(user));
   }

   @DeleteMapping("/admin/user/{uid}")
    public R delUser(@PathVariable("uid") Long uid){
        return R.ok(userService.removeById(uid));
   }
}
