package com.harm.app.job;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * tasklet based job
 * chunked based job
 */
@Configuration
@Slf4j
public class SimpleJobConfig004 {
    @Autowired
    BatchBuilderContainer batchBuilderContainer;

    /**
     * Job
     */
//    @Bean
    public Job taskletBasedJob() {
        return batchBuilderContainer.getJobBuilderFactory().get("taskletBasedJob")
                .start(taskletBasedStep(null))
                .incrementer(new RunIdIncrementer())
                .build();
    }
    public static boolean flag = false;
    @Bean
    @JobScope
    public Step taskletBasedStep(@Value("#{jobParameters[limit]}") Integer limit) {
        limit = Optional.ofNullable(limit).orElse(10);
        return batchBuilderContainer.getStepBuilderFactory().get("taskletBasedStep")
                .tasklet((contribution, chunkContext) -> {
                    Integer current = (Integer)chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("limit");
                    log.debug("current : {}", current);
                    if(current == null) {
                        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("limit", 0);
                        return  RepeatStatus.CONTINUABLE;
                    }

                    if(current < 10) {
                        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("limit", current+1);
                        return RepeatStatus.CONTINUABLE;
                    } else {
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
    /**
     * Job
     */
    @Bean
    public Job chunkBasedJob() {
        return batchBuilderContainer.getJobBuilderFactory().get("chunkBasedJob")
            .start(chunkStep())
                .incrementer(new RunIdIncrementer())
            .build();
    }
    @Bean
    public Step chunkStep() {
        return batchBuilderContainer.getStepBuilderFactory().get("chunkStep")
                //You must specify either a chunkCompletionPolicy or a commitInterval but not both.
                .<String, String>chunk(1000)
//                .<String, String>chunk(completionPolicy())
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ListItemReader<String> itemReader() {
        final int itemSize = 100_000;
        List<String> items = new ArrayList<>(itemSize);
        for(int i=0; i<itemSize; i++) {
            items.add(UUID.randomUUID().toString());
        }
        return new ListItemReader<>(items);
    }
    @Bean
    public ItemWriter<String> itemWriter() {
        Logger logger = LoggerFactory.getLogger(this.getClass().getName() + "_itemWriter");
        return items -> {
            log.info("item size [{}] write ok.", items.size());
        };
    }

    @Bean
    public CompletionPolicy completionPolicy() {
        CompositeCompletionPolicy policy = new CompositeCompletionPolicy();
        policy.setPolicies(new CompletionPolicy[]{
                new TimeoutTerminationPolicy(3),
                new SimpleCompletionPolicy(1000),
        });
        return policy;
    }
}
