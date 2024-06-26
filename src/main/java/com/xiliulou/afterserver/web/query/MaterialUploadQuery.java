// MaterialUploadQuery.java

package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.Material;
import lombok.Data;

import java.util.List;

/**
 * Request
 */
@Data
public class MaterialUploadQuery {
    private long materialBatchId;
    private List<MaterialQuery>  materials;
    
}
