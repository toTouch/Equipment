package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.DeliverFactoryQuery;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DeliverService extends IService<Deliver> {

    IPage getPage(Long offset, Long size, DeliverQuery deliver);

    void exportExcel(DeliverQuery deliver, HttpServletResponse response);

    R updateStatusFromBatch(List<Long> ids, Integer status);

    R insert(Deliver deliver,  Long wareHouseIdStart, Long wareHouseIdEnd);

    R updateDeliver(Deliver deliver,  Long wareHouseIdStart, Long wareHouseIdEnd);

    R queryListByFactory(Long offset, Long size);

    R factoryDeliver(DeliverFactoryQuery deliverFactoryQuery);

    R queryIssueListByFactory();

    R queryIssueInfo(String sn);

    R queryContentByFactory(String no);

    Deliver queryByNo(String deliverNo);
    
    Deliver getDeliverInfo(String sn);
}
