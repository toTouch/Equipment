package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiliulou.afterserver.entity.FactoryTestConf;
import com.xiliulou.afterserver.service.AuditGroupService;
import com.xiliulou.afterserver.service.FactoryTestConfService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditGroupStrawberryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zgw
 * @date 2022/6/10 11:06
 * @mood
 */
@RestController
@Slf4j
@RequestMapping("/admin/factoryTestConf")
public class JsonAdminFactoryTestConfController {
    @Autowired
    FactoryTestConfService factoryTestConfService;

    @GetMapping("/list")
    public R queryList(@RequestParam("offset")Long offset, @RequestParam("size")Long size,@RequestParam(required = false,value = "confName") String confName){
        return factoryTestConfService.getPage(offset,size,confName);
    }

    @PostMapping("/save")
    public R saveOne(@RequestBody @Validated FactoryTestConf con){
        return factoryTestConfService.saveOne(con);
    }


    @DeleteMapping("/del/{id}")
    public R reomveOne(@PathVariable("id")Long id){
        return factoryTestConfService.reomveOne(id);
    }
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id")Long id){
        return factoryTestConfService.detailById(id);
    }

    @PutMapping("/update")
    public R updateOne(@RequestBody @Validated FactoryTestConf con){
        return factoryTestConfService.updateOne(con);
    }


}
