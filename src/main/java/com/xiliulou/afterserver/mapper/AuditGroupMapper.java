package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.AuditGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/6 17:40
 * @mood
 */
public interface AuditGroupMapper extends BaseMapper<AuditGroup> {
    @Select({"<script>",
                "select " +
                        "id, name, entry_ids, sort, process_id from t_audit_group " +
                "where process_id = #{id} ",
                "<if test='isAdmin == null'>",
                    "and del_flag = 0 ",
                "</if>",
                "order by sort",
            "</script>"
    })
    List<AuditGroup> getByProcessId(@Param("id") Long id, @Param("isAdmin")Integer isAdmin);
}
