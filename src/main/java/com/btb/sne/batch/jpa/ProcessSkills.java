package com.btb.sne.batch.jpa;

import com.btb.sne.batch.SeparatorPolicy;
import com.btb.sne.batch.StepChunkListener;
import com.btb.sne.config.ApplicationConfig;
import com.btb.sne.service.J_EntityService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ProcessSkills {

    private final StepBuilderFactory stepBuilderFactory;
    private final J_EntityService service;
    private final EntityMapper mapper;
    private final ApplicationConfig config;
    private final PlatformTransactionManager tm;

    @Bean("ProcessSkills.step")
    public Step step() {
        return this.stepBuilderFactory.get("Skills")
                .transactionManager(tm)
                .<Skill, Skill>chunk(config.getChunkSize())
                .reader(skillFlatFileItemReader())
                .writer(skillItemWriter())
                .listener(new StepChunkListener())
                .build();
    }

    @Bean
    public FlatFileItemReader<Skill> skillFlatFileItemReader() {
        final String[] fields = new String[]{"conceptType", "conceptUri", "skillType", "reuseLevel", "preferredLabel", "altLabels", "hiddenLabels", "status", "modifiedDate", "scopeNote", "definition", "inScheme", "description"};

        return new FlatFileItemReaderBuilder<Skill>()
                .name("ProcessSkills Reader")
                .resource(new ClassPathResource("skills_nl.csv"))
                .linesToSkip(1) // skip header
                .recordSeparatorPolicy(new SeparatorPolicy(fields.length))
                .delimited()
                .names(fields)
                .targetType(Skill.class)
                .build();
    }

    @Bean
    public ItemWriter<Skill> skillItemWriter() {
        return items -> service.save(items.stream().map(mapper::from).toList());
    }

    @Data
    public static class Skill {

        private String altLabels;
        private String conceptType;
        private String conceptUri;
        private String definition;
        private String description;
        private String hiddenLabels;
        private String inScheme;
        private String modifiedDate;
        private String preferredLabel;
        private String reuseLevel;
        private String scopeNote;
        private String skillType;
        private String status;
    }
}
