package com.btb.sne.model.neo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Node("Skill")
public class N_Skill extends N_BaseEntity {

    private String skillType;
    private String reuseLevel;
    private String hiddenLabels;
    private String status;
    private String modifiedDate;
    private String scopeNote;
    private String definition;
}

