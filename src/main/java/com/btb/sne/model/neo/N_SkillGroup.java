package com.btb.sne.model.neo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@ToString
@Node("SkillGroup")
public class N_SkillGroup extends N_BaseEntity {

    private String hiddenLabels;
    private String status;
    private String modifiedDate;
    private String scopeNote;
    private String code;
}
