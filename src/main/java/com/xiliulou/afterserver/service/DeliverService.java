package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Deliver;

import javax.servlet.http.HttpServletResponse;

public interface DeliverService extends IService<Deliver> {

    IPage getPage(Long offset, Long size, Deliver deliver);

    void exportExcel(Deliver deliver, HttpServletResponse response);
}
