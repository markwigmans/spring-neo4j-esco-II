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
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProcessBroaderRelations {

    private final StepBuilderFactory stepBuilderFactory;
    private final J_EntityService service;
    private final ApplicationConfig config;
    private final PlatformTransactionManager tm;

    @Bean("ProcessBroaderRelations.step")
    public Step step() {
        return this.stepBuilderFactory.get("Broader relations")
                .transactionManager(tm)
                .<BroaderRelation, BroaderRelation>chunk(config.getChunkSize())
                .reader(broaderRelationMultiResourceItemReader())
                .writer(broaderRelationItemWriter())
                .listener(new StepChunkListener())
                .build();
    }

    @Bean
    public MultiResourceItemReader<BroaderRelation> broaderRelationMultiResourceItemReader() {
        Resource[] inputFiles = {
                new ClassPathResource("broaderRelationsOccPillar.csv"),
                new ClassPathResource("broaderRelationsSkillPillar.csv"),
        };

        return new MultiResourceItemReaderBuilder<BroaderRelation>()
                .name("BroaderRelation Multireader")
                .resources(inputFiles)
                .delegate(broaderRelationFlatFileItemReader())
                .build();
    }

    FlatFileItemReader<BroaderRelation> broaderRelationFlatFileItemReader() {
        final String[] fields = new String[]{"conceptType", "conceptUri", "broaderType", "broaderUri"};

        return new FlatFileItemReaderBuilder<BroaderRelation>()
                .name("BroaderRelation Reader")
                .linesToSkip(1) // skip header
                .recordSeparatorPolicy(new SeparatorPolicy(fields.length))
                .delimited()
                .names(fields)
                .targetType(BroaderRelation.class)
                .maxItemCount(config.getMaxCount())
                .build();
    }

    @Bean
    public ItemWriter<BroaderRelation> broaderRelationItemWriter() {
        return items -> {
            for (BroaderRelation item : items) {
                Optional<J_Entity> concept = service.get(item.getConceptUri());
                Optional<J_Entity> broader = service.get(item.getBroaderUri());
                if (concept.isPresent() && broader.isPresent()) {
                    if (concept.get().getBroaderNodes().add(broader.get())) {
                        service.save(concept.get());
                    }
                } else {
                    log.warn("{} : {} : {} : {}", item.getConceptUri(), concept.isPresent(), item.getBroaderUri(), broader.isPresent());
                }
            }
        };
    }

    @Data
    public static class BroaderRelation {
        private String conceptType;
        private String conceptUri;
        private String broaderType;
        private String broaderUri;
    }
}
