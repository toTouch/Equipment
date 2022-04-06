package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.InventoryFlowBill;
import com.xiliulou.afterserver.entity.IotCard;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IotCardMapper extends BaseMapper<IotCard> {

    Integer saveOne(IotCard iotCard);

    Integer updateOne(IotCard iotCard);

    IPage<IotCard> getPage(Page page, @Param("iotCard")IotCard iotCard);

    @Update("update t_iot_card set expiration_flag = 1 where expiration_time < #{curTime}")
    void expirationHandle(@Param("curTime") Long curTime);

    @Update("update t_iot_card set expiration_flag = 0 where expiration_time > #{curTime}")
    void notExpirationHandle(@Param("curTime") Long curTime);

    List<IotCard> iotCardList(@Param("iotCard")IotCard iotCard);
}
