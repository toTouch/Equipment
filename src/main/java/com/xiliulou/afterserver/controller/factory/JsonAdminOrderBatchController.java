package com.xiliulou.afterserver.controller.factory;

import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/2/25 11:39
 * @mood
 */
@RestController
@RequestMapping("/admin/factory")
public class JsonAdminOrderBatchController {
    /**
     * 服务对象
     */
    @Autowired
    private BatchService batchService;
    @Autowired
    private ProductNewService productNewService;


    @GetMapping("/batch/list")
    public R queryByfactory(@RequestParam(value = "offset", required = false) Long offset,
                            @RequestParam(value = "limit", required = false) Long size){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }

        if(Objects.isNull(offset)){
            offset = 0L;
        }

        if(Objects.isNull(size)){
            size = 50L;
        }

        return batchService.queryByfactory(offset, size);
    }

    /**
     *手持终端 获取柜机列表
     * @param batchId
     * @return
     */
    @GetMapping("/productNew")
    public R queryByBatchAndSupplier(@RequestParam("batchId") Long batchId,
                                     @RequestParam(value = "offset", required = false) Long offset,
                                     @RequestParam(value = "size", required = false) Long size){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }
        if(Objects.isNull(offset)){
            offset = 0L;
        }
        if(Objects.isNull(size)){
            size = 50L;
        }
        return productNewService.queryByBatchAndSupplier(batchId, offset, size);
    }

    /**
     * 手持终端获取 柜机详情
     * @param no
     * @return
     */
    @GetMapping("/productNew/info")
    public R queryProductNewInfoById(@RequestParam("no")String no){
        return productNewService.queryProductNewInfoById(no);
    }

    /**
     * 手持终端 更新产品信息
     * @param productNewDetailsQuery
     * @return
     */
    @PutMapping("/productNew")
    public R updateProductNew(@RequestBody ProductNewDetailsQuery productNewDetailsQuery){
        return productNewService.updateProductNew(productNewDetailsQuery);
    }
}
