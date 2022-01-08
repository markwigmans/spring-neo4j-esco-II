package com.btb.sne.batch.jpa;

import com.btb.sne.batch.EntityData;
import com.btb.sne.model.jpa.J_Entity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface EntityMapper {

    J_Entity from(ProcessSkills.Skill record);

    J_Entity from(ProcessSkillGroups.SkillGroup record);

    J_Entity from(ProcessOccupations.Occupation record);

    J_Entity from(ProcessISCOGroups.ISCOGroup record);

    J_Entity from(ProcessTransversals.TransversalInput record);

    EntityData from(J_Entity record);

    default List<String> transform(Set<J_Entity> entities) {
        return entities.stream().map(J_Entity::getConceptUri).toList();
    }
}
