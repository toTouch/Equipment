package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.File;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 11:23
 **/
public interface FileMapper extends BaseMapper<File> {

    void saveBatchFile(@Param("files") List<File> files);
}
