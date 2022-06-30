package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditEntryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:37
 * @mood
 */
public interface AuditEntryMapper extends BaseMapper<AuditEntry> {

    Long getCountByIdsAndRequired(@Param("entryIds") List<Long> entryIds, @Param("required")Integer required);

    List<KeyProcessAuditEntryVo> queryByEntryIdsAndPid(@Param("entryIds")List<Long> entryIds, @Param("pid")Long pid, @Param("groupId")Long groupId,@Param("isAdmin")Integer isAdmin);

    List<AuditEntry> getByEntryIds(@Param("entryIds")List<Long> entryIds);

    @Update("update t_audit_entry set del_flag = 1 where id = #{id}")
    boolean removeById(@Param("id")Long id);
}
