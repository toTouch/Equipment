package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import com.xiliulou.afterserver.web.query.CabinetCompressionQuery;
import com.xiliulou.afterserver.web.query.CompressionQuery;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import com.xiliulou.afterserver.web.query.ProductNewQuery;

import com.xiliulou.afterserver.web.vo.CabinetCompressionContentVo;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * (ProductNew)表服务接口
 *
 * @author Hardy
 * @since 2021-08-17 10:29:14
 */
public interface ProductNewService extends IService<ProductNew> {

    /**
     * 通过ID查询单条数据从数据库
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNew queryByIdFromDB(Long id);

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    ProductNew queryByIdFromCache(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ProductNew> queryAllByLimit(int offset, int limit, String no, Long modelId, Long startTime, Long endTime, List<Long> list, String testType);

    /**
     * 新增数据
     *
     * @param productNew 实例对象
     * @return 实例对象
     */
    ProductNew insert(ProductNew productNew);

    /**
     * 修改数据
     *
     * @param productNew 实例对象
     * @return 实例对象
     */
    Integer update(ProductNew productNew);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    R saveAdminProductNew(ProductNew productNew);

    R putAdminProductNew(ProductNewQuery query);

    R delAdminProductNew(Long id);

    R updateList(List<ProductNew> productNewList);

    ProductNew prdouctInfoByNo(String no);

    Integer count(String no,Long modelId,Long startTime,Long endTime, List<Long> list);

    R getProductFile(Long id, Integer fileType);

    R updateStatusFromBatch(List<Long> ids, Integer status);

    R queryProductInfo(String no);

    R queryLikeProductByNo(String no);

    R bindPoint(Long productId, Long pointId, Integer pointType);

    List<ProductNew> queryByBatch(Long id);

    R checkCompression(ApiRequestQuery apiRequestQuery);

    R successCompression(ApiRequestQuery apiRequestQuery);

    R findIotCard(String no);

    R queryByBatchAndSupplier(Long batchId,Long offset, Long size);

    R queryProductNewProcessInfo(String no, HttpServletResponse response);

    R updateProductNew(ProductNewDetailsQuery productNewDetailsQuery);

    R checkProperty(String no, String deliverNo);

    R factorySaveFile(File file);

    R pointList(Integer offset, Integer limit, String no, Long modelId, Long pointId, Integer pointType, Long startTime, Long endTime, String testType);

    public ProductNew queryByNo(String no);

    String getStatusName(Integer status);

    R delOssFileByName(String name);

    R cabinetCompressionStatus(CabinetCompressionQuery cabinetCompressionQuery);

    R cabinetCompressionCheck(String no);

    R cabinetCompressionList(String sn, Long startTime, Long endTime);

    CabinetCompressionContentVo queryProductTestInfo(Long pid);

    R runFullLoadTest(ApiRequestQuery apiRequestQuery);
}
