package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.MaterialTraceability;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.MaterialTraceabilityQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 物料追溯表(MaterialTraceability)表服务接口
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */
public interface MaterialTraceabilityService {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialTraceability queryById(Long id);
    
    /**
     * 分页查询
     *
     * @param materialTraceability 筛选条件
     * @param offset               当前页
     * @param size                 条数
     * @return 查询结果
     */
    List<MaterialTraceability> queryByPage(MaterialTraceabilityQuery materialTraceability, Long offset, Long size);
    
    /**
     * 新增数据
     *
     * @param materialTraceability 实例对象
     * @return 实例对象
     */
    R insert(MaterialTraceabilityQuery materialTraceability);
    
    /**
     * 修改数据
     *
     * @param materialTraceability 实例对象
     * @return 实例对象
     */
    R update(MaterialTraceabilityQuery materialTraceability);
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);
    
    R checkSn(String sn);
    
    R materialUnbundling(MaterialTraceabilityQuery materialTraceability);
    
    R exportExcel(MaterialTraceabilityQuery materialTraceability, HttpServletResponse response);
    
    long queryByPageCount(MaterialTraceabilityQuery materialTraceability);
}
