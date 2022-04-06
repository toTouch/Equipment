package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.ColorCard;
import com.xiliulou.afterserver.util.R;

/**
 * @author zgw
 * @date 2022/3/30 19:20
 * @mood
 */
public interface ColorCardService extends IService<ColorCard> {
     R queryPull(String name);
}
