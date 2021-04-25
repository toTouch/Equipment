package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.ServiceInfo;
import com.xiliulou.afterserver.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/4/25 0025 15:29
 * @Description: 服务商
 */
@Slf4j
public class ServiceListener extends AnalysisEventListener<ServiceInfo> {
    private static final int BATCH_COUNT = 100;
    List<ServiceInfo> list = new ArrayList<>();

    public ServerService serverService;
    public ServiceListener(ServerService service) {
        this.serverService = service;
    }

    @Override
    public void invoke(ServiceInfo serviceInfo, AnalysisContext analysisContext) {
        log.info("服务商表导入=====解析到一条数据:{}", JSON.toJSONString(serviceInfo));
        list.add(serviceInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();

        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("服务商表导入=====所有数据解析完成！");
    }



    /**
     * 入库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());
        ArrayList<Server> arrayList = new ArrayList<>();
        list.forEach(item -> {
            Server server = new Server();
            BeanUtils.copyProperties(item, server);
            server.setCreateTime(System.currentTimeMillis());

            arrayList.add(server);
        });
        serverService.saveBatch(arrayList);
        log.info("存储数据库成功！");
    }
}
