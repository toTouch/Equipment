package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.util.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DeliverService extends IService<Deliver> {

    IPage getPage(Long offset, Long size, Deliver deliver);

    void exportExcel(Deliver deliver, HttpServletResponse response);

    R updateStatusFromBatch(List<Long> ids, Integer status);
}
