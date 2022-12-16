package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.service.PartsService;
import com.xiliulou.afterserver.web.query.PartsQuery;
import com.xiliulou.core.web.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Parts)表控制层
 *
 * @author Hardy
 * @since 2022-12-15 15:02:06
 */
@RestController
public class PartsController {
    /**
     * 服务对象
     */
    @Resource
    private PartsService partsService;

    @GetMapping("admin/parts")
    public R queryList(@RequestParam("size") Integer size,
                       @RequestParam("offset") Integer offset,
                       @RequestParam(value = "name", required = false) String name){
        return  partsService.queryList(size, offset, name);
    }

    @PostMapping("admin/parts")
    public R saveOne(@RequestBody @Validated PartsQuery partsQuery){
        return  partsService.saveOne(partsQuery);
    }

    @PutMapping("admin/parts")
    public R updateOne(@RequestBody @Validated PartsQuery partsQuery){
        return  partsService.updateOne(partsQuery);
    }

    @DeleteMapping("admin/parts/{id}")
    public R deleteOne(@PathVariable("id")Long id){
        return  partsService.deleteOne(id);
    }

    @GetMapping("admin/parts/pull")
    public R queryPull(@RequestParam("size") Integer size,
        @RequestParam("offset") Integer offset,
        @RequestParam(value = "name", required = false) String name){
        return  partsService.queryPull(size, offset, name);
    }
}
