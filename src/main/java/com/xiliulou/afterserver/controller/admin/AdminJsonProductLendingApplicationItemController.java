package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiliulou.afterserver.service.ProductLendingApplicationItemService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgw
 * @date 2021/9/22 20:02
 * @mood
 */
@RestController
@Slf4j
public class AdminJsonProductLendingApplicationItemController {

    @Autowired
    ProductLendingApplicationItemService productLendingApplicationItemService;

    @GetMapping("admin/productIendingApplicationItem/page")
    public R getPage(Long offset, Long size, Long productAppId){
        return R.ok(productLendingApplicationItemService.getPage(offset, size, productAppId));
    }

    @PutMapping("admin/productIendingApplicationItem/edit")
    public R edit(Long id, Long takeNum, Long returnNum){
        return productLendingApplicationItemService.edit(id, takeNum, returnNum);
    }
}
