package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.PointNew;

import com.xiliulou.afterserver.web.query.PointAuditStatusQuery;
import java.math.BigDecimal;
import java.util.List;

import com.xiliulou.afterserver.web.vo.PointNewMapCountVo;
import com.xiliulou.afterserver.web.vo.PointNewMapStatisticsVo;
import com.xiliulou.afterserver.web.vo.PointNewPullVo;
import com.xiliulou.afterserver.web.vo.ProductNewDeliverVo;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * (PointNew)表数据库访问层
 *
 * @author Hardy
 * @since 2021-08-17 10:28:43
 */
public interface PointNewMapper extends BaseMapper<PointNew> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    PointNew queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<PointNew> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit, @Param("name") String name,
                                   @Param("cid") Integer cid,
                                   @Param("status") Integer status,
                                   @Param("customerId") Long customerId,
                                   @Param("startTime") Long startTime,
                                   @Param("endTime") Long endTime,
                                   @Param("createUid") Long createUid,
                                   @Param("snNo") String snNo,
                                   @Param("productSeries") Integer productSeries,
                                   @Param("auditStatus") Integer auditStatus);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param pointNew 实例对象
     * @return 对象列表
     */
    List<PointNew> queryAll(PointNew pointNew);

    /**
     * 新增数据
     *
     * @param pointNew 实例对象
     * @return 影响行数
     */
    int insertOne(PointNew pointNew);

    /**
     * 修改数据
     *
     * @param pointNew 实例对象
     * @return 影响行数
     */
    int update(PointNew pointNew);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    Integer countPoint(@Param("name") String name,
                       @Param("cid") Integer cid,
                       @Param("status") Integer status,
                       @Param("customerId") Long customerId,
                       @Param("startTime") Long startTime,
                       @Param("endTime") Long endTime,
                       @Param("createUid") Long createUid,
                       @Param("snNo") String snNo,
                       @Param("productSeries") Integer productSeries,
                       @Param("auditStatus") Integer auditStatus);

    List<PointNew> queryAllByLimitExcel(@Param("name") String name,
                                        @Param("cid") Integer cid,
                                        @Param("status") Integer status,
                                        @Param("customerId") Long customerId,
                                        @Param("startTime") Long startTime,
                                        @Param("endTime") Long endTime,
                                        @Param("createUid") Long createUid,
                                        @Param("snNo") String snNo,
                                        @Param("productSeries") Integer productSeries,
                                        @Param("auditStatus") Integer auditStatus);

    @Update("update t_point_new set create_uid = #{createUid} where id = #{id}")
    Integer putAdminPointNewCreateUser(@Param("id")Long id, @Param("createUid")Long createUid);

    void updateMany(@Param("list")List<PointNew> list);

    @Update("update t_point_new set status = 11 where warranty_time < #{curTime} and status != 3")
    void updatePastWarrantyStatus(@Param("curTime")Long curTime);

    @Select("select id, name from t_point_new where name like concat('%', #{name}, '%') limit 0, 20")
    List<PointNewPullVo> queryPointNewPull(String name);

    List<PointNewMapStatisticsVo> mapStatistics(@Param("coordXStart") BigDecimal coordXStart,
                                                @Param("coordXEnd")BigDecimal coordXEnd,
                                                @Param("coordYStart")BigDecimal coordYStart,
                                                @Param("coordYEnd")BigDecimal coordYEnd,
                                                @Param("cityId")Long cityId,
                                                @Param("provinceId")Long provinceId,
                                                @Param("productSeries")Integer productSeries);

    List<PointNewMapCountVo> pointNewMapProvinceCount();

    List<PointNewMapCountVo> pointNewMapCityCount(@Param("pid") Long pid);

    void batchUpdateAuditStatus(PointAuditStatusQuery pointAuditStatusQuery);

    Integer updateAuditStatus(PointNew pointNewUpdate);

    List<ProductNewDeliverVo> productNewDeliverList(@Param("offset") Long offset, @Param("size")  Long size,  @Param("batchNo")String batchNo, @Param("sn")String sn, @Param("deviceName")String deviceName,@Param("cabinetSn")String cabinetSn, @Param("tenantName")String tenantName, @Param("startTime")Long startTime, @Param("endTime")Long endTime);
    Integer productNewDeliverCount( @Param("batchNo")String batchNo, @Param("sn")String sn, @Param("tenantName")String tenantName, @Param("startTime")Long startTime, @Param("endTime")Long endTime);
    List<ProductNewDeliverVo> productNewDeliverExport( @Param("batchNo")String batchNo, @Param("sn")String sn, @Param("deviceName")String deviceName,@Param("cabinetSn")String cabinetSn,@Param("tenantName")String tenantName, @Param("startTime")Long startTime, @Param("endTime")Long endTime);
}
