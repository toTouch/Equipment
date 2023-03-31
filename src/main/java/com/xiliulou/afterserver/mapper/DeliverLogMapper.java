package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.DeliverLog;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author Hardy
 * @date 2022/2/21 9:06
 * @mood
 */
public interface DeliverLogMapper extends BaseMapper<DeliverLog> {

    Integer queryMaxCountBydeliverIds(@Param("ids") List<Long> ids);
}
