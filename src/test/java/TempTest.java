import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.entity.ExportMaterialConfig;
import com.xiliulou.afterserver.entity.ProductNew;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TempTest {
    
    @Test
    public void dataCheck_DuplicatePn_ReturnsFail() {
        List<ExportMaterialConfig> configs = Arrays.asList(
                //                new ExportMaterialConfig(null, "pn1", null,null, Arrays.asList(1,2,3),  null, null),
                //                new ExportMaterialConfig(null, "pn1", null, null, List.of(), null, null),
                //                new ExportMaterialConfig(null, "pn1", null, null, null, null, null),
                //                new ExportMaterialConfig(null, "pn1", null, null, Arrays.asList(1,6), null, null)
        );
        
        System.out.println(configs);
        List<Integer> statusList = configs.stream().filter(i -> !CollectionUtils.isEmpty(i.getAssociationStatus())).flatMap(i -> i.getAssociationStatus().stream())
                .collect(Collectors.toList());
        System.out.println(statusList);
    }
    
    @Test
    public void test2() {
        Map<String, List<Long>> lists = new HashMap<>();
        
        lists.put("stream", new ArrayList<>());
        lists.put("forEach", new ArrayList<>());
        lists.put("for", new ArrayList<>());
        lists.put("fori", new ArrayList<>());
        lists.put("parallelStream", new ArrayList<>());
        
        for (int i = 0; i < 10; i++) {
            test(lists);
        }
        System.out.println();
        System.out.println();
        //输出均值
        lists.forEach((k, v) -> {
            Long sum = v.stream().reduce(0L, Long::sum);
            System.out.println(k + ":" + sum / v.size());
        });
        
        
    }
    
    public void test(Map<String, List<Long>> lists) {
        // 创建一个包含重复元素的列表 列表大小为100W
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < 1000_0000; i++) {
            list.add(new Item(i, i));
        }
        // 耗时统计
        long startTime = System.currentTimeMillis();
        Map<Integer, Integer> collect = new HashMap<>();
        collect = list.stream().collect(Collectors.toMap(Item::getName, Item::getValue, (newValue, oldValue) -> newValue));
        long endTime = System.currentTimeMillis();
        lists.get("stream").add(endTime - startTime);
        System.out.println("stream 耗时：" + (endTime - startTime) + "ms");
        collect.clear();
        
        startTime = System.currentTimeMillis();
        Map<Integer, Integer> finalCollect = collect;
        list.forEach(item -> {
            finalCollect.put(item.getName(), item.getValue());
        });
        endTime = System.currentTimeMillis();
        lists.get("forEach").add(endTime - startTime);
        System.out.println("forEach 耗时：" + (endTime - startTime) + "ms");
        collect.clear();
        
        startTime = System.currentTimeMillis();
        for (Item item : list) {
            collect.put(item.getName(), item.getValue());
        }
        endTime = System.currentTimeMillis();
        lists.get("for").add(endTime - startTime);
        System.out.println("for 耗时：" + (endTime - startTime) + "ms");
        
        collect.clear();
        
        // fori
        startTime = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            Item item = list.get(i);
            collect.put(item.getName(), item.getValue());
        }
        endTime = System.currentTimeMillis();
        lists.get("fori").add(endTime - startTime);
        System.out.println("fori 耗时：" + (endTime - startTime) + "ms");
        collect.clear();
        
        //并行流
        startTime = System.currentTimeMillis();
        Map<Integer, Integer> collect1 = list.parallelStream().collect(Collectors.toMap(Item::getName, Item::getValue, (newValue, oldValue) -> newValue));
        endTime = System.currentTimeMillis();
        lists.get("parallelStream").add(endTime - startTime);
        System.out.println("parallelStream 耗时：" + (endTime - startTime) + "ms");
        
        
    }
    
    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    class Item {
        
        private Integer name;
        
        private Integer value;
    }
    
    @Test
    public void test3() {
        List<String> list = Arrays.asList("2.0.1", "2.0.2","2.0.3");
        
        String json = JSON.toJSONString(list);
        System.out.println(json);
        List<String> list1 = JSON.parseArray("[]", String.class);
        System.out.println(list1);
        
    }
    
    
    @Test
    public void test4() {
        Map<Integer, List<String>> statusMap = new HashMap<>();
        ArrayList<ProductNew> beforeModification = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            ProductNew productNew = new ProductNew();
            productNew.setStatus(RandomUtils.nextInt(1, 4));
            productNew.setNo("no" + i);
            beforeModification.add(productNew);
        }

        //分组 状态: no    {1=[no0, no2, no5], 2=[no1, no4, no7], 3=[no3, no6, no8, no9]}
        System.out.println(beforeModification.stream().collect(Collectors.groupingBy(ProductNew::getStatus, Collectors.mapping(ProductNew::getNo, Collectors.toList()))));
        // 分组 状态:对象  太长不粘
        System.out.println(beforeModification.stream().collect(Collectors.groupingBy(ProductNew::getStatus)));
        
        for (ProductNew productNew : beforeModification) {
            
            statusMap.computeIfAbsent(productNew.getStatus(), k -> new ArrayList<>()).add(productNew.getNo());
        }
        System.out.println(statusMap); //{1=[no0, no2, no5], 2=[no1, no4, no7], 3=[no3, no6, no8, no9]}
    }
}
