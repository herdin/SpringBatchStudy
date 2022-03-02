package com.harm.app.job;

import com.harm.app.util.LogDecorator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;

/**
 * step/job execution context
 * execution context promotion listener
 * */
@Configuration
@Slf4j
public class SimpleJobConfig003 {

    @Autowired
    BatchBuilderContainer batchBuilderContainer;

//    @Bean
    public Job jobWithAddUserNameToExecutionContext() {
        return batchBuilderContainer.getJobBuilderFactory().get("simple-job-with-add-execution-context-inc")
                .start(stepWithAddUserNameToExecutionContext())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    @Bean
    public Step stepWithAddUserNameToExecutionContext() {
        return batchBuilderContainer.getStepBuilderFactory().get("simple-step-with-add-execution-context")
                .tasklet(new AddUserNameToExecutionContextTasklet(AddUserNameToExecutionContextTasklet.ADD_LOCATION.STEP))
                .build();
    }
    @Slf4j
    @AllArgsConstructor
    public static class AddUserNameToExecutionContextTasklet implements Tasklet {
        public enum ADD_LOCATION {
            JOB, STEP, NONE
        }
        final ADD_LOCATION addLocation;
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            String name = (String)chunkContext.getStepContext().getJobParameters().get("name");
            String nameFromStepEC = (String)chunkContext.getStepContext().getStepExecutionContext().get("name");
            LogDecorator.decorateLine(log, "job param name [{}], step EC name", name, nameFromStepEC);
            ExecutionContext executionContext = null;
            if(addLocation == ADD_LOCATION.JOB) {
                executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            } else if(addLocation == ADD_LOCATION.STEP) {
                executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
            }

            if(executionContext != null) {
                executionContext.put("user.name", name);
            }
            return RepeatStatus.FINISHED;
        }
    }

//    @Bean
    public Job jobWithExecutionContextPromotionListener() {
        return batchBuilderContainer.getJobBuilderFactory().get("simple-job-with-execution-context-promotion-listener")
                .start(step1())
                .next(step2())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step1() {
        return batchBuilderContainer.getStepBuilderFactory().get("simple-step-with-execution-context-promotion-listener-1")
                .tasklet((contribution, chunkContext) -> {
                    final String jobParamKey_name = "name";
                    final String contextKeyFromStep = "name-step";
                    String name = (String)chunkContext.getStepContext().getJobParameters().get(jobParamKey_name);
                    String nameFromStep = (String)chunkContext.getStepContext().getJobExecutionContext().get(contextKeyFromStep);
                    LogDecorator.decorateLine(log, "job param name/context value from step [{}]/[{}]", name, nameFromStep);
                    if(nameFromStep == null) {
                        chunkContext.getStepContext().getStepExecution().getExecutionContext().put(contextKeyFromStep, name);
                    }

                    return RepeatStatus.FINISHED;
                })
                .listener(promotionListener())
                .build();
    }
    @Bean
    public Step step2() {
        return batchBuilderContainer.getStepBuilderFactory().get("simple-step-with-execution-context-promotion-listener-2")
                .tasklet((contribution, chunkContext) -> {
                    final String jobParamKey_name = "name";
                    final String contextKeyFromStep = "name-step";
                    String name = (String)chunkContext.getStepContext().getJobParameters().get(jobParamKey_name);
                    String nameFromStep = (String)chunkContext.getStepContext().getJobExecutionContext().get(contextKeyFromStep);
                    LogDecorator.decorateLine(log, "job param name/context value from step [{}]/[{}]", name, nameFromStep);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"name-step"});
        return listener;
    }





}
