package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.ColorCard;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zgw
 * @date 2022/3/30 19:17
 * @mood
 */
public interface ColorCardMapper extends BaseMapper<ColorCard> {

    @Select("select name from t_color_card")
    List<String> getNameAll();
}
