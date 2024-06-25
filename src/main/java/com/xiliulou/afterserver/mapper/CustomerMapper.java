package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.CustomerPullVo;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CustomerMapper extends BaseMapper<Customer> {
    @Select("select id, name from customer where name like concat('%', #{name}, '%') limit 0, 20")
    List<CustomerPullVo> queryCustomerPull(String name);

    List<PageSearchVo> customerSearch(@Param("offset") Long offset, @Param("size")Long size, @Param("name")String name);
    
    List<Customer> selectByParamer(@Param("customer") Customer customer);
}
