package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.service.PointNewService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Hardy
 * @date 2021/8/17 10:33
 * @mood 点位模块新改
 */
@RestController
public class AdminJsonPointNewController {

    @Autowired
    private PointNewService pointNewService;

    @PostMapping("/admin/pointNew")
    public R saveAdminPointNew(@RequestBody PointNew pointNew){
        return pointNewService.saveAdminPointNew(pointNew);
    }

    @PutMapping("/admin/pointNew")
    public R putAdminPointNew(@RequestBody PointNew pointNew){
        return pointNewService.putAdminPointNew(pointNew);
    }

    @DeleteMapping("/admin/pointNew/{id}")
    public R delAdminPointNew(@PathVariable("id") Long id){
        return pointNewService.delAdminPointNew(id);
    }

    @GetMapping("/admin/pointNew/list")
    public R pointList(@RequestParam("offset") Integer offset,
                       @RequestParam("limit") Integer limit,
                       @RequestParam(value = "name",required = false) String name){
        return R.ok(pointNewService.queryAllByLimit(offset,limit,name));
    }

}
