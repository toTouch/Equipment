package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.FactoryTestConf;
import com.xiliulou.afterserver.service.FactoryTestConfService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public R queryList(@RequestParam("offset") Long offset, @RequestParam("size") Long size, @RequestParam(required = false, value = "confName") String confName) {
        return factoryTestConfService.getPage(offset, size, confName);
    }
    
    @PostMapping("/save")
    public R saveOne(@RequestBody @Validated FactoryTestConf con) {
        return factoryTestConfService.saveOne(con);
    }
    
    
    @DeleteMapping("/del/{id}")
    public R reomveOne(@PathVariable("id") Long id) {
        return factoryTestConfService.reomveOne(id);
    }
    
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        return factoryTestConfService.detailById(id);
    }
    
    /**
     * 上下架工厂测试流程配置
     */
    @PutMapping("/shelf/{id}")
    public R shelfOne(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        return factoryTestConfService.updateOneShelf(id, status);
    }
    
    @PutMapping("/update")
    public R updateOne(@RequestBody @Validated FactoryTestConf con) {
        return factoryTestConfService.updateOne(con);
    }
    
    
}
