package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import com.xiliulou.afterserver.web.vo.DeliverVo;
import org.apache.ibatis.annotations.Param;

public interface DeliverMapper extends BaseMapper<Deliver> {

    IPage<DeliverVo> getDeliverPage(Page page, @Param("query") DeliverQuery deliver);

}
