package com.btb.sne.batch;

import com.btb.sne.batch.jpa.*;
import com.btb.sne.batch.neo.ProcessEntities;
import com.btb.sne.batch.neo.ProcessRelations;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableBatchProcessing
@Primary
@RequiredArgsConstructor
public class BatchConfiguration extends DefaultBatchConfigurer {

    private final ProcessSkills processSkills;
    private final ProcessSkillGroups processSkillGroups;
    private final ProcessOccupations processOccupations;
    private final ProcessISCOGroups processISCOGroups;
    private final ProcessBroaderRelations processBroaderRelations;
    private final ProcessSkillSkillRelation processSkillSkillRelation;
    private final ProcessOccupationalSkillRelation processOccupationalSkillRelation;
    private final ProcessTransversals processTransversals;
    private final ProcessEntities processEntities;
    private final ProcessRelations processRelations;
    private final JobLoggerListener jobLoggerListener;

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("ESCO job")
                .incrementer(new RunIdIncrementer())
                .start(jpaFlow())
                .next(processEntities.step())
                .next(processRelations.step())
                .build()
                .listener(jobLoggerListener)
                .build();
    }

    public Flow jpaFlow() {
        return new FlowBuilder<Flow>("JPA flow")
                .start(processSkills.step())
                .next(processSkillGroups.step())
                .next(processTransversals.step())
                .next(processSkillSkillRelation.step())
                .next(processOccupations.step())
                .next(processISCOGroups.step())
                .next(processBroaderRelations.step())
                .next(processOccupationalSkillRelation.step())
                .build();
    }
}