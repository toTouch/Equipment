package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.ServerAuditEntry;
import com.xiliulou.afterserver.mapper.ServerAuditEntryMapper;
import com.xiliulou.afterserver.service.ServerAuditEntryService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import com.xiliulou.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/6/20 11:21
 * @mood
 */
@Service
@Slf4j
public class ServerAuditEntryServiceImpl extends ServiceImpl<ServerAuditEntryMapper, ServerAuditEntry> implements ServerAuditEntryService {
    @Resource
    ServerAuditEntryMapper serverAuditEntryMapper;

    @Override
    public ServerAuditEntry getByName(String name) {
        return this.baseMapper.selectOne(new QueryWrapper<ServerAuditEntry>().eq("name", name).eq("del_flag", ServerAuditEntry.DEL_NORMAL));
    }

    @Override
    public ServerAuditEntry getBySort(BigDecimal sort) {
        return this.baseMapper.selectOne(new QueryWrapper<ServerAuditEntry>().eq("sort", sort).eq("del_flag", ServerAuditEntry.DEL_NORMAL));
    }

    @Override
    public R saveOne(ServerAuditEntryQuery query) {
        ServerAuditEntry serverAuditEntry = this.getByName(query.getName());
        if(Objects.nonNull(serverAuditEntry)) {
            return R.fail(null,"组件名称已存在");
        }

        serverAuditEntry = this.getBySort(query.getSort());
        if(Objects.nonNull(serverAuditEntry)) {
            return R.fail(null,"排序值重复，请修改");
        }

        String regexp = null;
        if(Objects.equals(query.getType(), ServerAuditEntry.TYPE_RADIO) || Objects.equals(query.getType(), ServerAuditEntry.TYPE_CHECKBOX)) {
            regexp = generateRegular(query.getType(), query.getJsonRoot());
            if(StringUtils.isBlank(regexp)) {
                return R.fail(null,"请填入备选项");
            }
        }
        return null;
    }
}
