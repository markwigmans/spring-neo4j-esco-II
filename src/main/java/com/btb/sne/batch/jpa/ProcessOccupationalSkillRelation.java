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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProcessOccupationalSkillRelation {

    private final StepBuilderFactory stepBuilderFactory;
    private final J_EntityService service;
    private final EntityMapper mapper;
    private final ApplicationConfig config;
    private final PlatformTransactionManager tm;

    @Bean("ProcessOccupationalSkillRelation.step")
    public Step step() {
        return this.stepBuilderFactory.get("Occupational Skill relations")
                .transactionManager(tm)
                .<OccupationalSkillRelation, OccupationalSkillRelation>chunk(config.getChunkSize())
                .reader(occupationalSkillRelationFlatFileItemReader())
                .writer(occupationalSkillRelationItemWriter())
                .listener(new StepChunkListener())
                .build();
    }

    @Bean
    public FlatFileItemReader<OccupationalSkillRelation> occupationalSkillRelationFlatFileItemReader() {
        final String[] fields = new String[]{"occupationUri", "relationType", "skillType", "skillUri"};

        return new FlatFileItemReaderBuilder<OccupationalSkillRelation>()
                .name("ProcessOccupationalSkillRelation Reader")
                .resource(new ClassPathResource("occupationSkillRelations.csv"))
                .linesToSkip(1) // skip header
                .recordSeparatorPolicy(new SeparatorPolicy(fields.length))
                .delimited()
                .names(fields)
                .targetType(OccupationalSkillRelation.class)
                .maxItemCount(config.getMaxCount())
                .build();
    }

    @Data
    public static class OccupationalSkillRelation {
        private String occupationUri;
        private String relationType;
        private String skillType;
        private String skillUri;
    }

    @Bean
    public ItemWriter<OccupationalSkillRelation> occupationalSkillRelationItemWriter() {
        return items -> {
            // group By occupation
            var collect = items.stream().collect(groupingBy(OccupationalSkillRelation::getOccupationUri));

            collect.forEach((key, value) -> {
                final Set<J_Entity> optionals = new HashSet<>();
                final Set<J_Entity> essentials = new HashSet<>();
                Optional<J_Entity> occupation = service.get(key);
                if (occupation.isPresent()) {
                    value.forEach(s -> {
                        Optional<J_Entity> skill = service.get(s.getSkillUri());
                        if (skill.isPresent()) {
                            switch (s.getRelationType()) {
                                case "optional" -> optionals.add(skill.get());
                                case "essential" -> essentials.add(skill.get());
                                default -> log.warn("unknown type: {}", s.getRelationType());
                            }
                        } else {
                            log.warn("Skill {} does not exist ", s.getSkillUri());
                        }
                    });
                    // if value creates a change
                    if (occupation.get().getOptionals().addAll(optionals) && occupation.get().getEssentials().addAll(essentials)) {
                        service.save(occupation.get());
                    }
                } else {
                    log.warn("Occupation {} does not exist ", key);
                }
            });
        };
    }
}

