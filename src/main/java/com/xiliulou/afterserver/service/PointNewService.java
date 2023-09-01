package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.PointAuditStatusQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.vo.PointNewPullVo;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * (PointNew)表服务接口
 *
 * @author Hardy
 * @since 2021-08-17 10:28:43
 */
public interface PointNewService extends IService<PointNew> {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNew queryByIdFromDB(Long id);

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNew queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<PointNew> queryAllByLimit(int offset, int limit, String name, Integer cid, Integer status,
                                   Long customerId, Long startTime, Long endTime, Long createUid, String snNo, Integer productSeries, Integer auditStatus);

    /**
     * 新增数据
     *
     * @param pointNew 实例对象
     * @return 实例对象
     */
    PointNew insert(PointNew pointNew);

    /**
     * 修改数据
     *
     * @param pointNew 实例对象
     * @return 实例对象
     */
    Integer update(PointNew pointNew);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    R saveAdminPointNew(PointNew pointNew);

    R putAdminPointNew(PointNew pointNew);

    R delAdminPointNew(Long id);

    R putAdminPoint(PointNew pointNew);

    R pointInfo(Long pid);

    Integer countPoint(String name, Integer cid, Integer status,
                       Long customerId, Long startTime, Long endTime, Long createUid, String snNo, Integer productSeries, Integer auditStatus);

    List<PointNew> queryAllByLimitExcel(String name, Integer cid, Integer status, Long customerId, Long startTime, Long endTime, Long createUid, String snNo,Integer productSeries, Integer auditStatus);

    R putAdminPointNewCreateUser(Long id, Long createUid);

    void updateMany(List<PointNew> pointNew);

    R pointBindSerialNumber(PointQuery pointQuery);

    R updateAuditStatus(PointAuditStatusQuery pointAuditStatusQuery);

    //R saveCache(Long pointId, Long modelId, String no, Long batch);

    //R deleteProduct(Long pointId, Long producutId);

    void updatePastWarrantyStatus();

    List<PointNewPullVo> queryPointNewPull(String name);

    R pointNewMapStatistics(List<BigDecimal> coordXList, List<BigDecimal> coordYList, Long cityId, Long provinceId, Integer productSeries);

    R pointNewMapProvinceCount();

    R pointNewMapCityCount(Long pid);


    R queryFiles(Long pid);

    R batchUpdateAuditStatus(PointAuditStatusQuery pointAuditStatusQuery);

    R productNewDeliverList(Long offset, Long size, String batchNo, String sn, String deviceName, String cabinetSn, String tenantName,
        Long startTime, Long endTime);

    R productNewDeliverCount(String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime);

    R productNewDeliverExportExcel(String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime, HttpServletResponse response);


}
