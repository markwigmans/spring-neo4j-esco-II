package com.btb.sne.batch.jpa;

import com.btb.sne.batch.SeparatorPolicy;
import com.btb.sne.batch.StepChunkListener;
import com.btb.sne.config.ApplicationConfig;
import com.btb.sne.model.jpa.J_Entity;
import com.btb.sne.service.J_EntityService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProcessSkillSkillRelation {

    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationConfig config;
    private final J_EntityService service;
    private final PlatformTransactionManager tm;

    @Bean("ProcessSkillSkillRelation.step")
    public Step step() {
        return this.stepBuilderFactory.get("Skill Skill relations")
                .transactionManager(tm)
                .<SkillSkillRelation, SkillSkillRelation>chunk(config.getChunkSize())
                .reader(skillSkillRelationFlatFileItemReader())
                .writer(skillSkillRelationItemWriter())
                .listener(new StepChunkListener())
                .build();
    }

    @Bean
    public FlatFileItemReader<SkillSkillRelation> skillSkillRelationFlatFileItemReader() {
        final String[] fields = new String[]{"originalSkillUri", "originalSkillType", "relationType", "relatedSkillType", "relatedSkillUri"};

        return new FlatFileItemReaderBuilder<SkillSkillRelation>()
                .name("ProcessSkillSkillRelation Reader")
                .resource(new ClassPathResource("skillSkillRelations.csv"))
                .linesToSkip(1) // skip header
                .recordSeparatorPolicy(new SeparatorPolicy(fields.length))
                .delimited()
                .names(fields)
                .targetType(SkillSkillRelation.class)
                .maxItemCount(config.getMaxCount())
                .build();
    }

    @Bean
    public ItemWriter<SkillSkillRelation> skillSkillRelationItemWriter() {
        return items -> {
            for (SkillSkillRelation item : items) {
                Optional<J_Entity> entity1 = service.get(item.getOriginalSkillUri());
                Optional<J_Entity> entity2 = service.get(item.getRelatedSkillUri());
                if (entity1.isPresent() && entity2.isPresent()) {
                    switch (item.getRelationType()) {
                        case "optional" -> entity1.get().getOptionals().add(entity2.get());
                        case "essential" -> entity1.get().getEssentials().add(entity2.get());
                        default -> log.warn("unknown type: {}", item.getRelationType());
                    }
                    service.save(entity1.get());
                } else {
                    log.warn("{} : {} : {} : {}", item.getOriginalSkillUri(), entity1.isPresent(), item.getRelatedSkillUri(), entity1.isPresent());
                }
            }
        };
    }

    @Data
    public static class SkillSkillRelation {
        private String originalSkillUri;
        private String originalSkillType;
        private String relationType;
        private String relatedSkillType;
        private String relatedSkillUri;
    }
}
