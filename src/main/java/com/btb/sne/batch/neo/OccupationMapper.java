package com.btb.sne.batch.neo;

import com.btb.sne.batch.EntityData;
import com.btb.sne.model.neo.N_Occupation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OccupationMapper {

    @Mapping(target = "broaderNodes", ignore = true)
    @Mapping(target = "essentials", ignore = true)
    @Mapping(target = "optionals", ignore = true)
    N_Occupation from(EntityData entity);
}