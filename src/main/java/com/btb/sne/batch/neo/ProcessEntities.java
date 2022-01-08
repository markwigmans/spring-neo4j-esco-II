package com.btb.sne.batch.neo;

import com.btb.sne.batch.EntityData;
import com.btb.sne.batch.RecordTypes;
import com.btb.sne.batch.StepChunkListener;
import com.btb.sne.batch.jpa.EntityMapper;
import com.btb.sne.config.ApplicationConfig;
import com.btb.sne.model.jpa.J_Entity;
import com.btb.sne.service.N_ISCOGroupService;
import com.btb.sne.service.N_OccupationService;
import com.btb.sne.service.N_SkillGroupService;
import com.btb.sne.service.N_SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static java.util.stream.Collectors.groupingBy;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProcessEntities {

    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationConfig config;

    final private N_SkillService skillService;
    final private N_SkillGroupService skillGroupService;
    final private N_OccupationService occupationService;
    final private N_ISCOGroupService iscoGroupService;

    private final SkillMapper skillMapper;
    private final SkillGroupMapper skillGroupMapper;
    private final OccupationMapper occupationMapper;
    private final ISCOGroupMapper iscoGroupMapper;

    private final EntityMapper entityMapper;

    @Bean("ProcessEntities.step")
    public Step step() {
        return this.stepBuilderFactory.get("Entities")
                .<J_Entity, EntityData>chunk(config.getChunkSize())
                .reader(entityJpaPagingItemReader(null))
                .processor(transform())
                .writer(entityItemWriter())
                .listener(new StepChunkListener())
                .build();
    }

    @Bean("ProcessEntities.reader")
    public JpaPagingItemReader<J_Entity> entityJpaPagingItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<J_Entity>()
                .name("Entities Item Reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("from Entity")
                .pageSize(config.getChunkSize())
                .build();
    }

    @Bean("ProcessEntities.transform")
    public TransformEntity transform() {
        return new TransformEntity();
    }

    public class TransformEntity implements ItemProcessor<J_Entity, EntityData> {

        @Override
        public EntityData process(J_Entity item) throws Exception {
            return entityMapper.from(item);
        }
    }

    @Bean("ProcessEntities.writer")
    public ItemWriter<EntityData> entityItemWriter() {
        return items -> {
            // group By concept
            var types = items.stream().collect(groupingBy(EntityData::getConceptType));
            types.forEach((key, value) -> {
                switch (RecordTypes.valueOf(key)) {
                    case KnowledgeSkillCompetence -> skillService.save(value.stream().map(skillMapper::from).toList());
                    case SkillGroup -> skillGroupService.save(value.stream().map(skillGroupMapper::from).toList());
                    case Occupation -> occupationService.save(value.stream().map(occupationMapper::from).toList());
                    case ISCOGroup -> iscoGroupService.save(value.stream().map(iscoGroupMapper::from).toList());
                    default -> log.warn("Key: {} : not supported", key);
                }
            });
        };
    }
}
