package com.btb.sne.batch.neo;

import com.btb.sne.batch.EntityData;
import com.btb.sne.model.neo.N_ISCOGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ISCOGroupMapper {

    @Mapping(target = "broaderNodes", ignore = true)
    @Mapping(target = "essentials", ignore = true)
    @Mapping(target = "optionals", ignore = true)
    N_ISCOGroup from(EntityData entity);
}