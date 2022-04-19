package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.WorkAuditNotify;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author zgw
 * @date 2022/4/2 17:08
 * @mood
 */
public interface WorkAuditNotifyMapper extends BaseMapper<WorkAuditNotify> {

    List<WorkAuditNotify> queryList(@Param("size") Integer size, @Param("offset")Integer offset, @Param("type")Integer type);

    @Select("select count(*) from t_work_audit_notify where type = #{type}")
    Long queryCount(Integer type);

    @Update("update t_work_audit_notify set type = 1, update_time = #{curTime} where type = 0")
    void readNotifyAll(Long curTime);
}
