// package com.xiliulou.afterserver.util;
//
//
// import cn.hutool.core.collection.ListUtil;
// import cn.hutool.core.map.MapUtil;
// import cn.hutool.core.text.StrPool;
// import cn.hutool.core.util.ObjectUtil;
// import cn.hutool.core.util.StrUtil;
// import com.xiliulou.ele.batchdata.bean.operate.UserOperateLogEntity;
// import com.xiliulou.ele.batchdata.bean.operate.UserOperateTypeEntity;
// import com.xiliulou.ele.batchdata.dto.operate.OperateLogDTO;
// import com.xiliulou.ele.batchdata.service.operate.UserOperateLogService;
// import com.xiliulou.ele.batchdata.utils.DateUtils;
// import com.xiliulou.ele.batchdata.utils.OperateI18nUtil;
// import com.xiliulou.ele.batchdata.utils.SpELUtil;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.stereotype.Component;
//
// import java.util.Date;
// import java.util.HashMap;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.Set;
//
// /**
//  * <p>
//  * Description: This class is UserOperateLogHandler!
//  * </p>
//  * <p>Project: xiliulou-ele-data-batch</p>
//  * <p>Copyright: Copyright (c) 2024</p>
//  * <p>Company: xiliulou</p>
//  *
//  * @author <a href="mailto:wxblifeng@163.com">PeakLee</a>
//  * @since V1.0 2024/3/27
//  **/
// @Slf4j
// @Component
// public class UserOperateLogHandler {
//
//     public final static String FIRST_LINE_FORMATTING_CHARACTER = ":,";
//
//     private final static String BUILT_IN_TOOLS = "i18n";
//
//     private final static String NEW_PREFIX = "NEW";
//
//     private final static String OLD_PREFIX = "OLD";
//
//     private final static String VARIABLE_TEMPLATE = "%s_%s";
//
//     private final UserOperateLogService userOperateLogService;
//
//     public UserOperateLogHandler(UserOperateLogService userOperateLogService) {
//         this.userOperateLogService = userOperateLogService;
//     }
//
//     public UserOperateLogEntity handlerDTO(OperateLogDTO dto, UserOperateTypeEntity type) {
//         UserOperateLogEntity result = new UserOperateLogEntity();
//
//         Map<String, Object> newMap = builderContext(NEW_PREFIX, dto.getNewValue());
//         Map<String, Object> oldMap = builderContext(OLD_PREFIX, dto.getOldValue());
//         Map<String, Object> context = new HashMap<>(newMap);
//         context.putAll(oldMap);
//         context.put(BUILT_IN_TOOLS, OperateI18nUtil.getInstance());
//
//         SpELUtil.ExpressParse expressParse = SpELUtil.createContext(context);
//
//         Map<String, Object> diff = diffMap(type.getAllow(), dto.getOldValue(), dto.getNewValue());
//
//         List<String> strings = parseExpress(type, diff);
//
//         StringBuilder prompt = new StringBuilder();
//         boolean entered = false;
//         for (String express : strings) {
//             String s = StrUtil.nullToDefault(expressParse.parseValue(express), "");
//             if (s.startsWith(FIRST_LINE_FORMATTING_CHARACTER) && !entered) {
//                 entered = true;
//                 s = s.replaceFirst(FIRST_LINE_FORMATTING_CHARACTER, StrPool.COLON);
//             }
//             if (s.startsWith(FIRST_LINE_FORMATTING_CHARACTER) && entered) {
//                 s = s.replaceFirst(FIRST_LINE_FORMATTING_CHARACTER, StrPool.COMMA);
//             }
//             prompt.append(s);
//         }
//
//         if (StringUtils.isNotBlank(type.getTitle())) {
//             String title = StrUtil.nullToDefault(expressParse.parseValue(type.getTitle()), "");
//             if (title.trim().equals(prompt.toString().trim())) {
//                 prompt.setLength(0);
//             }
//         }
//
//         result.setUid(dto.getUid());
//         result.setOperateIp(dto.getIp());
//         result.setTenantId(dto.getTenantId());
//         result.setOperateUsername(dto.getUsername());
//         result.setOperateType(StrUtil.nullToDefault(expressParse.parseValue(type.getType()), ""));
//         result.setOperateContent(prompt.toString());
//         result.setOperateTime(DateUtils.toTimeFormatter.format(new Date(ObjectUtil.defaultIfNull(dto.getOperateTime(), System.currentTimeMillis()))));
//         return result;
//     }
//
//     public void batchSave(final List<UserOperateLogEntity> entities) {
//         try {
//             userOperateLogService.batchSave(entities);
//         } catch (Exception e) {
//             log.error("Batch processing of user records fail.", e);
//         }
//     }
//
//     private Map<String, Object> builderContext(String prefix, Map<String, Object> value) {
//         Map<String, Object> result = new HashMap<>();
//         if (value == null || value.isEmpty()) {
//             return result;
//         }
//
//         for (Map.Entry<String, Object> entry : value.entrySet()) {
//             Object entryValue = entry.getValue();
//             String key = String.format(VARIABLE_TEMPLATE, prefix, entry.getKey());
//             result.put(key, entryValue);
//         }
//
//         return result;
//     }
//
//     private List<String> parseExpress(UserOperateTypeEntity type, Map<String, Object> diff) {
//         if (Objects.isNull(type) || MapUtil.isEmpty(type.getTemplates()) || MapUtil.isEmpty(diff)) {
//             return ListUtil.empty();
//         }
//
//         List<String> result = new LinkedList<>();
//         Map<String, String> templates = type.getTemplates();
//         String title = type.getTitle();
//         String index = type.getIndex();
//         if (StrUtil.isNotBlank(title)) {
//             result.add(title);
//         }
//
//         Map<String, String> tempMap = new HashMap<>();
//         Set<Map.Entry<String, Object>> entries = diff.entrySet();
//         for (Map.Entry<String, Object> entry : entries) {
//             String key = entry.getKey();
//             if (templates.containsKey(key.trim())) {
//                 tempMap.put(key, templates.get(key));
//             }
//         }
//
//         if (StrUtil.isBlank(index)) {
//             result.addAll(tempMap.values());
//             return result;
//         }
//
//         String[] split = index.split(",");
//         for (String s : split) {
//             if (tempMap.containsKey(s)) {
//                 result.add(tempMap.remove(s));
//             }
//         }
//
//         if (!tempMap.isEmpty()) {
//             result.addAll(tempMap.values());
//         }
//
//         return result;
//     }
//
//     private Map<String, Object> diffMap(String allow, Map<String, Object> old, Map<String, Object> newVale) {
//         if (MapUtil.isEmpty(old) && !MapUtil.isEmpty(newVale)) {
//             return allowField(newVale, allow);
//         }
//
//         if (!MapUtil.isEmpty(old) && MapUtil.isEmpty(newVale)) {
//             return allowField(old, allow);
//         }
//
//         if (MapUtil.isEmpty(old) && MapUtil.isEmpty(newVale)) {
//             return MapUtil.empty();
//         }
//
//         Map<String, Object> result = new HashMap<>();
//         Set<Map.Entry<String, Object>> entries = newVale.entrySet();
//
//         for (Map.Entry<String, Object> entry : entries) {
//             String key = entry.getKey();
//             Object value = entry.getValue();
//             if (StrUtil.isNotBlank(allow) && allow.contains(key)) {
//                 result.put(key, value);
//                 continue;
//             }
//             if (old.containsKey(key)) {
//                 Object o = old.get(key);
//                 if (Objects.equals(o, value)) {
//                     continue;
//                 }
//                 result.put(key, o);
//             }
//         }
//
//         return result;
//     }
//
//     private Map<String, Object> allowField(Map<String, Object> root, String allow) {
//         Map<String, Object> result = new HashMap<>();
//         for (Map.Entry<String, Object> entry : root.entrySet()) {
//             String key = entry.getKey();
//             Object value = entry.getValue();
//             if (StrUtil.isNotBlank(allow) && allow.contains(key)) {
//                 result.put(key, value);
//             }
//         }
//         return result;
//     }
// }
