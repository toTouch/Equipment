package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service

public interface SupplierService extends IService<Supplier> {


    IPage getPage(Long offset, Long size, Supplier supplier);

    Supplier querySupplierName(String supplierName);

    R getUserInfo();
}
