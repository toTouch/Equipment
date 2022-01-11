package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.service.IotCardService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Slf4j
public class AdminJsonIotCardController extends BaseController {

    @Autowired
    IotCardService iotCardService;

    //@GetMapping("admin/iotCard/list")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, IotCard iotCard) {
        return iotCardService.getPage(offset, size, iotCard);
    }

    //@PostMapping("admin/iotCard")
    public R saveOne(@RequestBody IotCard iotCard){
        if(Objects.isNull(iotCard)){
            return R.fail("参数错误");
        }
        return iotCardService.saveOne(iotCard);
    }

    //@PutMapping("admin/iotCard")
    public R updateOne(@RequestBody IotCard iotCard){
        if(Objects.isNull(iotCard)){
            return R.fail("参数错误");
        }
        return iotCardService.updateOne(iotCard);
    }

    //@DeleteMapping("admin/iotCard/{id}")
    public R deleteOne(@PathVariable("id") Long id){
        return iotCardService.deleteOne(id);
    }
}
