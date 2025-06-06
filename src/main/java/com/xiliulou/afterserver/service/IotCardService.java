package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.util.R;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface IotCardService extends IService<IotCard> {

    R saveOne(IotCard iotCard);

    R updateOne(IotCard iotCard);

    R deleteOne(Long id);

    R getPage(Long offset, Long size, IotCard iotCard);

    void exportExcel(IotCard iotCard, HttpServletResponse response);

    IotCard queryBySn(String iotCard);

    R snLike(String sn);

    boolean checkBind(Long id);

    R pageIotcardLikeSn(Long offset, Long size, String sn);
}
