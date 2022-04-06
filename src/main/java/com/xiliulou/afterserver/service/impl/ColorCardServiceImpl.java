package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.ColorCard;
import com.xiliulou.afterserver.mapper.ColorCardMapper;
import com.xiliulou.afterserver.service.ColorCardService;
import com.xiliulou.afterserver.util.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author zgw
 * @date 2022/3/30 19:21
 * @mood
 */
@Service("colorCardService")
public class ColorCardServiceImpl extends ServiceImpl<ColorCardMapper, ColorCard> implements ColorCardService {

    @Override
    public R queryPull(String name){
        return R.ok(this.baseMapper.selectList(new LambdaQueryWrapper<ColorCard>().like(StringUtils.isNotBlank(name), ColorCard::getName, name).orderByDesc(ColorCard::getName)));
    }
}
