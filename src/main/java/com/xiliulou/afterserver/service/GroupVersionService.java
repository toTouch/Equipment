package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.GroupVersion;
import com.xiliulou.afterserver.util.R;

import java.util.List;

/**
 * @author zgw
 * @date 2022/6/23 15:56
 * @mood
 */
public interface GroupVersionService extends IService<GroupVersion> {
    GroupVersion queryByGroupId(Long groupId);

    void createOrUpdate(Long groupId, String groupName);

    boolean removeByGroupId(Long id);

    R getList(String type);

    R checkGroupVersion(List<GroupVersion> groupVersionList, String type);
}
