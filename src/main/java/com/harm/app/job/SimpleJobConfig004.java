package com.harm.app.job;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
public class SimpleJobConfig004 {
    @Autowired
    BatchBuilderContainer batchBuilderContainer;

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
//                .<String, String>chunk(1000)
                .<String, String>chunk(completionPolicy())
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
            log.info("item size [{}]", items.size());
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
