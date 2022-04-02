package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.WorkOrderServer;
import com.xiliulou.afterserver.mapper.WorkOrderServerMapper;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.WorkOrderServerService;
import com.xiliulou.afterserver.web.query.WorkOrderServerQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zgw
 * @date 2022/3/29 18:13
 * @mood
 */
@Service
@Slf4j
public class WorkOrderServerServiceImpl extends ServiceImpl<WorkOrderServerMapper, WorkOrderServer> implements WorkOrderServerService {
    @Autowired
    WorkOrderServerMapper workOrderServerMapper;
    @Autowired
    FileService fileService;

    @Override
    public List<WorkOrderServerQuery> queryByWorkOrderIdAndServerId(Long workOrderId, Long serverId) {
        List<WorkOrderServerQuery> WorkOrderServerList =  this.baseMapper.queryByWorkOrderId(workOrderId, serverId);
        WorkOrderServerList.stream().forEach(item -> {
            BaseMapper<File> fileMapper = fileService.getBaseMapper();
            LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            fileLambdaQueryWrapper.eq(File::getBindId, item.getWorkOrderId());
            fileLambdaQueryWrapper.eq(File::getServerId, item.getServerId());
            List<File> files = fileMapper.selectList(fileLambdaQueryWrapper);

            item.setFileList(files);
            item.setFileCount(CollectionUtils.isEmpty(files)?0:files.size());
        });
        return WorkOrderServerList;
    }

    @Override
    public Boolean removeByWorkOrderId(Long id) {
        return this.baseMapper.delete(new UpdateWrapper<WorkOrderServer>().eq("work_order_id", id)) > 0 ;
    }

    @Override
    public List<Long> queryWorkOrderIds(Long serverId) {
        return this.baseMapper.queryWorkOrderIds(serverId);
    }

    @Override
    public Boolean updateSolutionByWorkOrderAndServerId(Long workOrderId, Long thirdId, String solution) {
        return baseMapper.updateSolutionByWorkOrderAndServerId(workOrderId, thirdId ,solution);
    }


}
