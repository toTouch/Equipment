package com.xiliulou.afterserver.service;




import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.SysAreaCodeEntity;
import com.xiliulou.afterserver.util.R;

/**
 * (City)表服务接口
 *
 * @author makejava
 * @since 2021-01-21 18:05:41
 */
public interface CityService extends IService<SysAreaCodeEntity> {


    R cityTree();
}