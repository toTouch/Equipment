package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.AuditProcessConstans;
import com.xiliulou.afterserver.constant.CommonConstants;
import com.xiliulou.afterserver.entity.AuditEntry;
import com.xiliulou.afterserver.entity.AuditGroup;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.mapper.AuditEntryMapper;
import com.xiliulou.afterserver.service.AuditEntryService;
import com.xiliulou.afterserver.service.AuditGroupService;
import com.xiliulou.afterserver.service.AuditValueService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.AuditEntryStrawberryQuery;
import com.xiliulou.afterserver.web.vo.AuditEntryStrawberryVo;
import com.xiliulou.afterserver.web.vo.KeyProcessAuditEntryVo;
import com.xiliulou.afterserver.web.vo.OssUrlVo;
import com.xiliulou.core.exception.CustomBusinessException;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zgw
 * @date 2022/6/6 17:42
 * @mood
 */
@Slf4j
@Service
public class AuditEntryServiceImpl extends ServiceImpl<AuditEntryMapper, AuditEntry> implements AuditEntryService {

    @Resource
    AuditEntryMapper auditEntryMapper;
    @Autowired
    AuditValueService auditValueService;
    @Autowired
    AuditGroupService auditGroupService;
    @Autowired
    AliyunOssService aliyunOssService;
    @Autowired
    StorageConfig storageConfig;

    @Override
    public Long getCountByIdsAndRequired(List<Long> entryIds, Integer required) {
        return auditEntryMapper.getCountByIdsAndRequired(entryIds, required);
    }

    @Override
    public List<KeyProcessAuditEntryVo> getVoByEntryIds(List<Long> entryIds, Long pid) {
        if(CollectionUtils.isEmpty(entryIds)) {
            return null;
        }

        List<KeyProcessAuditEntryVo> voList = auditEntryMapper.queryByEntryIdsAndPid(entryIds, pid);
        if(CollectionUtils.isEmpty(voList)) {
            return null;
        }

        voList.forEach(item -> {
            item.setJsonRootList(JsonUtil.fromJsonArray(item.getJsonRoot(), String.class));
            if(Objects.equals(item.getType(), AuditEntry.TYPE_PHOTO)) {
                Map<String, String> ossUrlMap = this.getOssUrlMap(JsonUtil.fromJsonArray(item.getValue(), String.class));
                item.setOssUrlMap(ossUrlMap);
            }
        });

        return voList;
    }

    @Override
    public List<AuditEntry> getByEntryIds(List<Long> entryIds) {
        if(CollectionUtils.isEmpty(entryIds)) {
            return null;
        }

        return  auditEntryMapper.getByEntryIds(entryIds);
    }

    @Override
    public AuditEntry getByName(String name) {
        return this.baseMapper.selectOne(new QueryWrapper<AuditEntry>().eq("name", name).eq("del_flag", AuditEntry.DEL_NORMAL));
    }

    @Override
    public AuditEntry getBySort(BigDecimal sort, List<Long> entryIds) {
        if(CollectionUtils.isEmpty(entryIds)){
            return null;
        }
        return this.baseMapper.selectOne(new QueryWrapper<AuditEntry>().eq("sort", sort).in("entry_ids", entryIds).eq("del_flag", AuditEntry.DEL_NORMAL));
    }

    @Override
    public boolean removeById(Long id) {
        return this.baseMapper.removeById(id);
    }


    @Override
    public R queryList(String groupId) {
        AuditGroup auditGroup = auditGroupService.getById(groupId);
        if(Objects.isNull(auditGroup)) {
            return R.fail(null,"未查询到相关模块");
        }

        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isEmpty(entryIds)) {
            return R.ok();
        }

        List<AuditEntry> entrys = this.getByEntryIds(entryIds);
        if(CollectionUtils.isEmpty(entrys)) {
            return R.ok();
        }

        List<AuditEntryStrawberryVo> data = new ArrayList<>();
        entrys.forEach(item -> {
            AuditEntryStrawberryVo vo = new AuditEntryStrawberryVo();
            BeanUtils.copyProperties(item, vo);
            vo.setJsonRoot(JsonUtil.fromJsonArray(item.getJsonRoot(), String.class));
            data.add(vo);
        });
        return R.ok(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveOne(AuditEntryStrawberryQuery query) {
        AuditGroup auditGroup = auditGroupService.getById(query.getGroupId());
        if(Objects.isNull(auditGroup)) {
            return R.fail(null,"未查询到相关模块");
        }

        AuditEntry auditEntry = this.getByName(query.getName());
        if(Objects.nonNull(auditEntry)) {
            return R.fail(null,"组件名称已存在");
        }

        auditEntry = this.getBySort(query.getSort(), JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class));
        if(Objects.nonNull(auditEntry)) {
            return R.fail(null,"排序值重复，请修改");
        }

        String regexp = null;
        if(Objects.equals(query.getType(), AuditEntry.TYPE_RADIO) || Objects.equals(query.getType(), AuditEntry.TYPE_CHECKBOX)) {
            regexp = generateRegular(query.getType(), query.getJsonRoot());
            if(StringUtils.isBlank(regexp)) {
                return R.fail(null,"请填入备选项");
            }
        }

        AuditEntry insertEntry = new AuditEntry();
        insertEntry.setName(query.getName());
        insertEntry.setType(query.getType());
        insertEntry.setSort(query.getSort());
        insertEntry.setJsonContent(regexp);
        insertEntry.setJsonRoot(query.getJsonRoot());
        insertEntry.setRequired(query.getRequired());
        insertEntry.setDelFlag(AuditEntry.DEL_NORMAL);
        insertEntry.setCreateTime(System.currentTimeMillis());
        insertEntry.setUpdateTime(System.currentTimeMillis());
        if((this.baseMapper.insert(insertEntry) == 0)) {
            log.error("DB ERROR! save auditEntry sql error data={}", insertEntry.toString());
            throw new CustomBusinessException("数据库错误");
        }


        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isEmpty(entryIds)) {
            entryIds = new ArrayList<>();
        }
        entryIds.add(insertEntry.getId());
        auditGroup.setEntryIds(JsonUtil.toJson(entryIds));
        if(!auditGroupService.updateById(auditGroup)) {
            log.error("DB ERROR! update auditEntry sql error data={}", insertEntry.toString());
            throw new CustomBusinessException("数据库错误");
        }

        return R.ok();
    }

    @Override
    public R putOne(AuditEntryStrawberryQuery query) {
        AuditGroup auditGroup = auditGroupService.getById(query.getGroupId());
        if(Objects.isNull(auditGroup)) {
            return R.fail(null, "未查询到相关模块");
        }

        AuditEntry auditEntry = this.getById(query.getId());
        if(Objects.isNull(auditEntry)) {
            return R.fail(null, "未查询到相关组件");
        }

        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isEmpty(entryIds) || !entryIds.contains(auditEntry.getId())) {
            return R.fail(null, "模块内没有该组件信息");
        }

        AuditEntry auditEntryOld = this.getByName(query.getName());
        if(Objects.nonNull(auditEntryOld) && !Objects.equals(auditEntryOld.getId(), auditEntry.getId())) {
            return R.fail(null, "组件名称已存在");
        }

        auditEntryOld = this.getBySort(query.getSort(), entryIds);
        if(Objects.nonNull(auditEntryOld) && !Objects.equals(auditEntryOld.getId(), auditEntry.getId())) {
            return R.fail(null, "排序值重复，请修改");
        }

        String regexp = null;
        if(Objects.equals(auditEntry.getType(), AuditEntry.TYPE_RADIO) || Objects.equals(auditEntry.getType(), AuditEntry.TYPE_CHECKBOX)) {
            regexp = generateRegular(auditEntry.getType(), query.getJsonRoot());
            if(StringUtils.isBlank(regexp)) {
                return R.fail("请填入备选项");
            }
        }

        AuditEntry updateEntry = new AuditEntry();
        updateEntry.setId(query.getId());
        updateEntry.setName(query.getName());
        updateEntry.setSort(query.getSort());
        updateEntry.setJsonContent(regexp);
        updateEntry.setJsonRoot(query.getJsonRoot());
        updateEntry.setRequired(query.getRequired());
        updateEntry.setUpdateTime(System.currentTimeMillis());
        if((this.baseMapper.updateById(updateEntry) == 0)) {
            log.error("DB ERROR! save auditEntry sql error data={}", updateEntry.toString());
            throw new CustomBusinessException("数据库错误");
        }
        return R.ok();
    }

    @Override
    public R removeOne(Long groupId, Long id) {
        AuditGroup auditGroup = auditGroupService.getById(groupId);
        if(Objects.isNull(auditGroup)) {
            return R.fail(null, "未查询到相关模块");
        }

        AuditEntry auditEntry = this.getById(id);
        if(Objects.isNull(auditEntry)) {
            return R.fail(null, "未查询到相关组件");
        }

        List<Long> entryIds = JsonUtil.fromJsonArray(auditGroup.getEntryIds(), Long.class);
        if(CollectionUtils.isEmpty(entryIds) || !entryIds.contains(auditEntry.getId())) {
            return R.fail(null, "模块内没有该组件信息");
        }

        String fixedgGroup = AuditProcessConstans.getFixedgEntry(id);
        if(!org.springframework.util.StringUtils.isEmpty(fixedgGroup)) {
            return R.fail("组件不可删除");
        }

        entryIds.remove(id);
        auditGroup.setEntryIds(JsonUtil.toJson(entryIds));
        if(!auditGroupService.updateById(auditGroup)) {
            log.error("DB ERROR! update auditGroup sql error data={}", auditGroup.toString());
            throw new CustomBusinessException("数据库错误");
        }

        if(!removeById(id)) {
            log.error("DB ERROR! delete auditEntry sql error data={}", auditGroup.toString());
            throw new CustomBusinessException("数据库错误");
        }
        return R.ok();
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
        if(auditEntryType.equals(AuditEntry.TYPE_RADIO)) {
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
