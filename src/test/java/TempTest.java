import com.xiliulou.afterserver.entity.ExportMaterialConfig;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
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
        List<Integer> statusList =
                configs.stream()
                        .filter(i -> !CollectionUtils.isEmpty(i.getAssociationStatus())).flatMap(i -> i.getAssociationStatus().stream()).collect(Collectors.toList());
        System.out.println(statusList);
    }
}
