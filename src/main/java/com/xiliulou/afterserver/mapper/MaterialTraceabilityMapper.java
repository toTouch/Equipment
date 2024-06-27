package com.xiliulou.afterserver.mapper;

import com.xiliulou.afterserver.entity.Material;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialTraceabilityMapper {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Material selectById(@Param("id") Long id);
    
    /**
     * 查询指定行数据
     *
     * @param material 查询条件
     * @return 对象列表
     */
    List<Material> selectListByLimit(@Param("query") Material material, @Param("offset") Long offset, @Param("size") Long size);
    
    /**
     * 统计总行数
     *
     * @param material 查询条件
     * @return 总行数
     */
    long count(@Param("query") Material material);
    
    /**
     * 新增数据
     *
     * @param material 实例对象
     * @return 影响行数
     */
    int insert(Material material);
    
    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Material> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Material> entities);
    
    /**
     * 修改数据
     *
     * @param material 实例对象
     * @param thirdId
     * @return 影响行数
     */
    int update(@Param("material") Material material, @Param("thirdId") Long thirdId);
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteByIds(@Param("id") List<Long> id);
    
    Material selectByParameter(Material material);
    
    int materialUnbundling(Material material);
    
    List<String> selectMaterialSnListBySN(@Param("productNo") String productNo, @Param("offset") Long offset, @Param("size") Long size);
    
    List<Material> listAllByImeis(@Param("imeis") List<String> imeis);
    
    List<Material> ListAllByAtemlIDs(@Param("atemlIDs") List<String> atemlIDs);
    
    List<Material> ListAllBySns(@Param("sns") List<String> sns);
    
    Material exitsByBindingStatus(Material material);
    
    Material exitsByBindingStatusList(@Param("ids") List<Long> ids);
    
    Integer updateMaterialStateByIds(@Param("ids") List<Long> ids, @Param("updateTime") Long updateTime, @Param("remark") String remark, @Param("materialState") Integer materialState);
    
    List<Material> selectListByIds(@Param("ids") List<Long> ids);
    
    List<Material> selectListByNos(@Param("nos") List<String> collect);
    
    List<Material> selectCountListByNos(@Param("nos") List<String> nos, @Param("unPassing") Integer unPassing);
    
    Integer deleteByMaterialBatchNo(Material material);
}


