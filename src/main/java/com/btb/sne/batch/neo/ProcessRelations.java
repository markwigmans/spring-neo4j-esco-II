package com.btb.sne.batch.neo;

import com.btb.sne.batch.EntityData;
import com.btb.sne.batch.StepChunkListener;
import com.btb.sne.batch.jpa.EntityMapper;
import com.btb.sne.config.ApplicationConfig;
import com.btb.sne.model.jpa.J_Entity;
import com.btb.sne.model.neo.N_BaseEntity;
import com.btb.sne.service.N_EntityService;
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
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProcessRelations {

    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationConfig config;

    final private N_EntityService service;
    private final EntityMapper entityMapper;

    @Bean("ProcessRelations.step")
    public Step step() {
        return this.stepBuilderFactory.get("Relations")
                .<J_Entity, EntityData>chunk(config.getChunkSize())
                .reader(entityJpaPagingItemReader(null))
                .processor(transform())
                .writer(entityItemWriter())
                .listener(new StepChunkListener())
                .build();
    }

    @Bean("ProcessRelations.reader")
    public JpaPagingItemReader<J_Entity> entityJpaPagingItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<J_Entity>()
                .name("Relations Item Reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("from Entity")
                .pageSize(config.getChunkSize())
                .build();
    }

    @Bean("ProcessRelations.transform")
    public TransformEntity transform() {
        return new TransformEntity();
    }

    public class TransformEntity implements ItemProcessor<J_Entity, EntityData> {

        @Override
        public EntityData process(J_Entity item) throws Exception {
            return entityMapper.from(item);
        }
    }

    @Bean("ProcessRelations.writer")
    public ItemWriter<EntityData> entityItemWriter() {
        return items -> items.forEach(item -> {
            N_BaseEntity entity = service.get(item.getConceptUri()).orElseThrow();
            entity.getBroaderNodes().addAll(retrieveEntities(item.getBroaderNodes()));
            entity.getEssentials().addAll(retrieveEntities(item.getEssentials()));
            entity.getOptionals().addAll(retrieveEntities(item.getOptionals()));
            service.save(entity);
        });
    }

    List<N_BaseEntity> retrieveEntities(List<String> entities) {
        return entities.stream().map(e -> service.get(e).orElseThrow()).toList();
    }
}
