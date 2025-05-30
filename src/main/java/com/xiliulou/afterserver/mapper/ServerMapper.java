package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import com.xiliulou.afterserver.web.vo.ServerPullVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-04 15:55
 **/

public interface ServerMapper extends BaseMapper<Server> {

    @Select("select id, name from server where name like concat('%', #{name}, '%') limit 0, 20")
    List<ServerPullVo> queryServerPull(String name);

    @Select("select id from server where name like concat('%', #{name}, '%')")
    List<Integer> getByIdsByName(String serverName);

    List<PageSearchVo> serverSearch(@Param("offset") Long offset,@Param("size") Long size, @Param("name")String name);
}
