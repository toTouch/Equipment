package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Deliver;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeliverMapper extends BaseMapper<Deliver> {
    int updateStatusFromBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    List<Deliver> orderList(Deliver deliver);

//    IPage<DeliverVo> getDeliverPage(Page page, @Param("query") DeliverQuery deliver);

}
