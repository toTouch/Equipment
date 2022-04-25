package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.SupplierPullVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface SupplierService extends IService<Supplier> {


    IPage getPage(Long offset, Long size, Supplier supplier);

    Supplier querySupplierName(String supplierName);

    R getUserInfo();

    List<SupplierPullVo> querySupplierPull(String name);
}
