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
public class ProcessSkillGroups {

    private final StepBuilderFactory stepBuilderFactory;
    private final ApplicationConfig config;
    private final J_EntityService service;
    private final EntityMapper mapper;
    private final PlatformTransactionManager tm;


    @Bean("ProcessSkillGroups.step")
    public Step step() {
        return this.stepBuilderFactory.get("Skill Groups")
                .transactionManager(tm)
                .<SkillGroup, SkillGroup>chunk(config.getChunkSize())
                .reader(skillGroupFlatFileItemReader())
                .writer(skillGroupItemWriter())
                .listener(new StepChunkListener())
                .build();
    }

    @Bean
    public FlatFileItemReader<SkillGroup> skillGroupFlatFileItemReader() {
        final String[] fields = new String[]{"conceptType", "conceptUri", "preferredLabel", "altLabels", "hiddenLabels", "status", "modifiedDate", "scopeNote", "inScheme", "description", "code"};

        return new FlatFileItemReaderBuilder<SkillGroup>()
                .name("ProcessSkillGroups Reader")
                .resource(new ClassPathResource("skillGroups_nl.csv"))
                .linesToSkip(1) // skip header
                .recordSeparatorPolicy(new SeparatorPolicy(fields.length))
                .delimited()
                .names(fields)
                .targetType(SkillGroup.class)
                .build();
    }

    @Bean
    public ItemWriter<SkillGroup> skillGroupItemWriter() {
        return items -> service.save(items.stream().map(mapper::from).toList());
    }

    @Data
    public static class SkillGroup {

        private String altLabels;
        private String code;
        private String conceptType;
        private String conceptUri;
        private String description;
        private String hiddenLabels;
        private String inScheme;
        private String modifiedDate;
        private String preferredLabel;
        private String scopeNote;
        private String status;
    }
}
