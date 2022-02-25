package com.xiliulou.afterserver.controller.outer;

import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import com.xiliulou.afterserver.web.query.CompressionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgw
 * @date 2022/2/25 15:23
 * @mood
 */
@RestController
@RequestMapping("/outer/compression")
public class JsonAdminCompressionController {

    @Autowired
    private ProductNewService productNewService;

    @PostMapping("/check")
    public R checkCompression(@RequestBody ApiRequestQuery apiRequestQuery){
        return productNewService.checkCompression(apiRequestQuery);
    }

    @PostMapping("/success")
    public R successCompression(@RequestBody ApiRequestQuery apiRequestQuery){
        return productNewService.successCompression(apiRequestQuery);
    }

}
