package com.btb.sne.model.neo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@ToString
@Node("ISCOGroup")
public class N_ISCOGroup extends N_BaseEntity {

    private String code;
}
