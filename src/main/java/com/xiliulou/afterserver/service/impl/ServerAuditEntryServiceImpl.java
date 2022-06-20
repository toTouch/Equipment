package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.CommonConstants;
import com.xiliulou.afterserver.entity.ServerAuditEntry;
import com.xiliulou.afterserver.mapper.ServerAuditEntryMapper;
import com.xiliulou.afterserver.service.ServerAuditEntryService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ServerAuditEntryQuery;
import com.xiliulou.afterserver.web.vo.ServerAuditEntryVo;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zgw
 * @date 2022/6/20 11:21
 * @mood
 */
@Service
@Slf4j
public class ServerAuditEntryServiceImpl extends ServiceImpl<ServerAuditEntryMapper, ServerAuditEntry> implements ServerAuditEntryService {
    @Resource
    ServerAuditEntryMapper serverAuditEntryMapper;
    @Autowired
    StorageConfig storageConfig;
    @Autowired
    AliyunOssService aliyunOssService;

    @Override
    public ServerAuditEntry getByName(String name) {
        return this.baseMapper.selectOne(new QueryWrapper<ServerAuditEntry>().eq("name", name).eq("del_flag", ServerAuditEntry.DEL_NORMAL));
    }

    @Override
    public ServerAuditEntry getBySort(BigDecimal sort) {
        return this.baseMapper.selectOne(new QueryWrapper<ServerAuditEntry>().eq("sort", sort).eq("del_flag", ServerAuditEntry.DEL_NORMAL));
    }

    @Override
    public R saveOne(ServerAuditEntryQuery query) {
        ServerAuditEntry serverAuditEntry = this.getByName(query.getName());
        if(Objects.nonNull(serverAuditEntry)) {
            return R.fail("组件名称已存在");
        }

        serverAuditEntry = this.getBySort(query.getSort());
        if(Objects.nonNull(serverAuditEntry)) {
            return R.fail("排序值重复，请修改");
        }

        String regexp = null;
        if(Objects.equals(query.getType(), ServerAuditEntry.TYPE_RADIO) || Objects.equals(query.getType(), ServerAuditEntry.TYPE_CHECKBOX)) {
            regexp = generateRegular(query.getType(), query.getJsonRoot());
            if(StringUtils.isBlank(regexp)) {
                return R.fail("请填入备选项");
            }
        }

        serverAuditEntry = new ServerAuditEntry();
        serverAuditEntry.setName(query.getName());
        serverAuditEntry.setType(query.getType());
        serverAuditEntry.setJsonRoot(query.getJsonRoot());
        serverAuditEntry.setJsonContent(regexp);
        serverAuditEntry.setPhoto(query.getPhoto());
        serverAuditEntry.setRequired(query.getRequired());
        serverAuditEntry.setSort(query.getSort());
        serverAuditEntry.setCreateTime(System.currentTimeMillis());
        serverAuditEntry.setUpdateTime(System.currentTimeMillis());
        serverAuditEntry.setDelFlag(ServerAuditEntry.DEL_NORMAL);
        this.save(serverAuditEntry);
        return R.ok();
    }

    @Override
    public R putOne(ServerAuditEntryQuery query) {
        ServerAuditEntry serverAuditEntryOld = this.getById(query.getId());
        if(Objects.isNull(serverAuditEntryOld)) {
            return R.fail("未查询到相关组件");
        }

        ServerAuditEntry serverAuditEntry = this.getByName(query.getName());
        if(Objects.nonNull(serverAuditEntry)) {
            return R.fail("组件名称已存在");
        }

        serverAuditEntry = this.getBySort(query.getSort());
        if(Objects.nonNull(serverAuditEntry)) {
            return R.fail("排序值重复，请修改");
        }

        String regexp = null;
        if(Objects.equals(query.getType(), ServerAuditEntry.TYPE_RADIO) || Objects.equals(query.getType(), ServerAuditEntry.TYPE_CHECKBOX)) {
            regexp = generateRegular(query.getType(), query.getJsonRoot());
            if(StringUtils.isBlank(regexp)) {
                return R.fail("请填入备选项");
            }
        }

        serverAuditEntry = new ServerAuditEntry();
        serverAuditEntry.setId(query.getId());
        serverAuditEntry.setName(query.getName());
        serverAuditEntry.setJsonRoot(query.getJsonRoot());
        serverAuditEntry.setJsonContent(regexp);
        serverAuditEntry.setPhoto(query.getPhoto());
        serverAuditEntry.setRequired(query.getRequired());
        serverAuditEntry.setSort(query.getSort());
        serverAuditEntry.setUpdateTime(System.currentTimeMillis());
        this.updateById(serverAuditEntry);
        return R.ok();
    }

    @Override
    public R getList() {
        List<ServerAuditEntry> serverAuditEntryList = this.baseMapper.selectList(new QueryWrapper<ServerAuditEntry>().eq("del_flag", ServerAuditEntry.DEL_NORMAL));
        if(CollectionUtils.isEmpty(serverAuditEntryList)) {
            return R.ok();
        }

        List<ServerAuditEntryVo> data = new ArrayList<>();
        serverAuditEntryList.forEach(item -> {
            Map<String, String> ossUrlMap = getOssUrlMap(JsonUtil.fromJsonArray(item.getPhoto(), String.class));
            ServerAuditEntryVo vo = new ServerAuditEntryVo();
            BeanUtils.copyProperties(item, vo);
            vo.setOssUrlMap(ossUrlMap);
        });

        return R.ok(data);
    }


    private String generateRegular(Integer auditEntryType, String jsonRoot){
        List<String> jsonRoots = JsonUtil.fromJsonArray(jsonRoot, String.class);
        if(CollectionUtils.isEmpty(jsonRoots)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        jsonRoots.forEach(item -> {
            sb.append(item).append(",");
        });
        String alternatives = sb.deleteCharAt(sb.length() - 1).toString();

        String alternativesReg = alternatives.replaceAll(",", "|");
        //1单选 2多选 3文本 4图片
        if(auditEntryType.equals(ServerAuditEntry.TYPE_RADIO)) {
            return alternativesReg;
        }

        String template = "((%s),)*(%s)";
        return String.format(template, alternativesReg, alternativesReg);
    }

    private Map<String, String> getOssUrlMap(List<String> fileNameList){
        Map<String, String> ossUrlMap = new HashMap<>();
        if(CollectionUtils.isEmpty(fileNameList)) {
            return ossUrlMap;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        fileNameList.forEach(item -> {
            String timeStamp = item.substring(0, item.lastIndexOf("."));
            String time = sdf.format(new Date(Long.parseLong(timeStamp)));
            ossUrlMap.put(getOssWatermarkUrl(item, time), item);
        });

        return ossUrlMap;
    }

    private String getOssWatermarkUrl(String fileName, String createTime) {
        if (!Objects.equals(storageConfig.getIsUseOSS(), StorageConfig.IS_USE_OSS)) {
            return "";
        }

        String bucketName = storageConfig.getBucketName();
        String dirName = storageConfig.getDir();
        String url = "";


        try {
            Long expiration = Optional.ofNullable(storageConfig.getExpiration())
                    .orElse(1000L * 60L * 3L) + System.currentTimeMillis();

            String style = String.format(CommonConstants.OSS_IMG_WATERMARK_STYLE,
                    base64Encode(createTime),
                    CommonConstants.OSS_IMG_WATERMARK_TYPE,
                    CommonConstants.OSS_IMG_WATERMARK_COLOR,
                    CommonConstants.OSS_IMG_WATERMARK_SIZE,
                    CommonConstants.OSS_IMG_WATERMARK_OFFSET);

            url = aliyunOssService.getOssFileUrl(bucketName, dirName + fileName, expiration, style);
        } catch (Exception e) {
            log.error("aliyunOss down watermark file Error!", e);
        }

        return url;
    }


    private String base64Encode(String content) {
        Base64.Encoder encoder = Base64.getUrlEncoder();
        byte[] base64Result = encoder.encode(content.getBytes());
        return new String(base64Result, StandardCharsets.UTF_8);
    }
}
