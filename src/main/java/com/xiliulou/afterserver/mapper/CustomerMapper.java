package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.web.vo.CustomerPullVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CustomerMapper extends BaseMapper<Customer> {
    @Select("select id, name from customer where name like concat('%', #{name}, '%') limit 0, 20")
    List<CustomerPullVo> queryCustomerPull(String name);
}
