package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.ImportTemplate;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ImportTemplateQuery;

/**
 * @author Hardy
 * @date 2022/2/14 16:45
 * @mood
 */
public interface ImportTemplateService  extends IService<ImportTemplate> {

    R upload(ImportTemplateQuery importTemplateQuery);

    R infoByType(String type);
}
