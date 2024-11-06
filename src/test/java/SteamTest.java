
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
public class SteamTest {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        Collections.addAll(names, "张无忌", "张三丰", "周芷若", "张忠明", "赵敏");
        System.out.println(names);
        List<String> zhangNa = new ArrayList<>();
        for (String string : names) {
            if (string.startsWith("张")) {
                zhangNa.add(string);
            }
        }
        // stream流获取
        System.out.println("steam流截取首字为张,长度为3:");
        names.stream().filter(s -> s.startsWith("张")).filter(s -> s.length() == 3).forEach(s -> System.out.println(s));
        System.out.println("===============");
        // stream流获取
        // Collection
        Collection<String> list = new ArrayList<>();
        Collections.addAll(list, "张无忌", "张三丰", "周芷若", "张忠明", "赵敏");
        Stream<String> s = list.stream();
        // map流获取
        Map<String, Integer> maps = new HashMap<>();
        // 键流
        Stream<String> s1 = maps.keySet().stream();
        // 值流
        Stream<Integer> i1 = maps.values().stream();
        // 数组流获取
        int[] a1 = { 1, 2, 3, 4, 5, 6, 7 };
        IntStream InArrStream = Arrays.stream(a1);
        String[] str = { "张无忌", "张三丰", "周芷若", "张忠", "赵敏" };
        Stream<String> strArrStream = Arrays.stream(str);
        Stream<String> strArrStream1 = Stream.of(str);
        // 常用API
        // 过滤数据, 源码
        list.stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String t) {
                return t.startsWith("张");
            };
        });// 简化为:
        System.out.println("数据过滤:");
        Stream<String> Sh1=list.stream().filter(t -> t.startsWith("张"));// 支持链式编程
//        Sh1.forEach(t -> System.out.println(t));
        System.out.println("============");
        list.stream().filter(t -> t.startsWith("张")).forEach(System.out::println);// 支持链式编程
        System.out.println("============");
        System.out.println("Map加工方法");
        Stream<String> Sh2= list.stream().map(t -> "可爱呢" + t);
//        Sh2.forEach(t -> System.out.println(t));
        System.out.println("============");
        System.out.println("合并流");
        Stream<String> Sh3=Stream.concat(Sh1,Sh2);
        Sh3.forEach(s2 -> System.out.println(s2));
        
        List<String> collected = list.stream().filter(t -> t.startsWith("张")).collect(Collectors.toList());
        System.out.println(collected);
        String 张 = list.stream().filter(t -> t.startsWith("张1")).findFirst().orElse(null);
        System.out.println(张);
        String 张1 = list.stream().filter(t -> t.startsWith("张1")).findFirst().get();
    }
    
}
