package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.WareHouseProductDetails;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.WareHouseProductDetailsQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgw
 * @date 2021/9/17 18:19
 * @mood
 */
public interface WareHouseProductDetailsService extends IService<WareHouseProductDetails> {

    IPage getPage(Long offset, Long size, WareHouseProductDetailsQuery wareHouseProductDetailsQuery);

    R insert(Long wareHouseId, List<WareHouseProductDetailsQuery> list);
}
