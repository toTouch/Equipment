package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.ProductNew;

import com.xiliulou.afterserver.web.vo.CabinetCompressionContentVo;
import com.xiliulou.afterserver.web.vo.CabinetCompressionVo;
import java.util.List;

import com.xiliulou.afterserver.web.vo.DeviceMessageVo;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * (ProductNew)表数据库访问层
 *
 * @author Hardy
 * @since 2021-08-17 10:29:13
 */
public interface ProductNewMapper extends BaseMapper<ProductNew> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNew queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ProductNew> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit,
                                     @Param("no") String no,
                                     @Param("modelId") Long modelId,
                                     @Param("startTime") Long startTime,
                                     @Param("endTime") Long endTime,
                                     @Param("list")List<Long> list,
                                     @Param("testType")String testType,
                                     @Param("cabinetSn")String cabinetSn);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param productNew 实例对象
     * @return 对象列表
     */
    List<ProductNew> queryAll(ProductNew productNew);

    /**
     * 新增数据
     *
     * @param productNew 实例对象
     * @return 影响行数
     */
    int insertOne(ProductNew productNew);

    /**
     * 修改数据
     *
     * @param productNew 实例对象
     * @return 影响行数
     */
    int update(ProductNew productNew);

    /**
     * 修改数据
     *
     * @param productNew 实例对象
     * @return 影响行数
     */
    int updateByConditions(ProductNew productNew);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    Integer countProduct(@Param("no") String no,
                         @Param("modelId") Long modelId,
                         @Param("startTime") Long startTime,
                         @Param("endTime") Long endTime,
                         @Param("list")List<Long> list											,
                         @Param("testType")String testType,
                         @Param("cabinetSn")String cabinetSn);

    int updateStatusFromBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    @Select("select max(serial_num) from t_product_new where code = #{code} and del_flag = 0")
    Integer queryMaxSerialNum(String code);

    @Select("select id, no from t_product_new where del_flag = 0")
    List<ProductNew> selectNoPull();

    @Update("update t_product_new set test_file = #{testFile}, test_result = #{testResult}, status = #{status}, test_type = #{testType} ,error_message = #{errorMessage},test_end_time = #{testEndTime} where no = #{no}")
    Integer updateByNoNew(ProductNew productNew);

    @Update("update t_product_new set test_file = #{testFile}, test_result = #{testResult}, status = #{status}, test_type = #{testType} where no = #{no}")
    Integer updateByNo(ProductNew productNew);

    @Select("select * from t_product_new where no = #{no} and del_flag = 0")
    ProductNew queryByNo(@Param("no")String no);

    List<CabinetCompressionVo> cabinetCompressionList(@Param("sn")String sn,
        @Param("testStartTimeBeginTime")Long testStartTimeBeginTime,
        @Param("testStartTimeEndTime")Long testStartTimeEndTime,
        @Param("testEndTimeBeginTime")Long testEndTimeBeginTime,
        @Param("testEndTimeEndTime")Long testEndTimeEndTime,
        @Param("sortType")Integer sortType);

    CabinetCompressionContentVo queryProductTestInfo(@Param("pid")Long pid);

    DeviceMessageVo queryDeviceMessage(@Param("no")String no);

    Integer updateUsedStatusByNo(@Param("no")String no,@Param("updateTime")Long  updateTime);
}
