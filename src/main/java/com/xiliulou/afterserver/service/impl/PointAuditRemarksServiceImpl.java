package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.PointAuditRemarks;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.PointAuditRemarksMapper;
import com.xiliulou.afterserver.service.PointAuditRemarksService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PointAuditRemarksServiceImpl extends ServiceImpl<PointAuditRemarksMapper, PointAuditRemarks> implements PointAuditRemarksService {

    @Autowired
    UserService userService;

    @Autowired
    PointAuditRemarksMapper pointAuditRemarksMapper;

    @Override
    public R saveOne(String remarks, Integer type) {
        Long uid = SecurityUtils.getUid();
        User user = userService.getUserById(uid);
        if(Objects.isNull(user)){
            return R.fail("查询不到用户");
        }

        List<PointAuditRemarks> list = this.queryByUid(uid, type);
        if(!CollectionUtil.isEmpty(list) && list.size() >= 10){
            return R.fail("最多只编辑10条快捷输入");
        }

        if(StringUtils.isBlank(remarks)){
            return R.fail("请输入有效快捷输入语句");
        }

        if(Objects.isNull(type)){
            return R.fail("参数有误");
        }

        PointAuditRemarks insert = new PointAuditRemarks();
        insert.setRemarks(remarks);
        insert.setType(type);
        insert.setUid(uid);
        insert.setCreateTime(System.currentTimeMillis());
        insert.setUpdateTime(System.currentTimeMillis());
        insert.setDelFlag(PointAuditRemarks.DEL_NORMAL);
        this.save(insert);
        return R.ok();
    }

    @Override
    public R getList(Integer type) {
        return R.ok(queryByUid(SecurityUtils.getUid(), type));
    }

    @Override
    public R deleteOne(Long id) {
        boolean boo = this.removeById(id);
        if(boo){
            return R.ok();
        }
        return R.fail("删除失败");
    }

    @Override
    public R updateOne(String remarks, Long id) {
        PointAuditRemarks pointAuditRemarks = this.getById(id);
        if(Objects.isNull(pointAuditRemarks)){
            return R.fail("没查询到该条快捷输入语句");
        }

        PointAuditRemarks update = new PointAuditRemarks();
        update.setId(id);
        update.setRemarks(remarks);
        update.setUpdateTime(System.currentTimeMillis());
        this.updateById(update);
        return R.ok();
    }

    public List<PointAuditRemarks> queryByUid(Long uid, Integer type){
        QueryWrapper<PointAuditRemarks> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", type);
        queryWrapper.eq("del_flag", PointAuditRemarks.DEL_NORMAL);
        queryWrapper.orderByDesc("create_time");

        return pointAuditRemarksMapper.selectList(queryWrapper);
    }
}
