package com.btb.sne.batch.neo;

import com.btb.sne.batch.EntityData;
import com.btb.sne.model.neo.N_SkillGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SkillGroupMapper {

    @Mapping(target = "broaderNodes", ignore = true)
    @Mapping(target = "essentials", ignore = true)
    @Mapping(target = "optionals", ignore = true)
    N_SkillGroup from(EntityData entity);
}