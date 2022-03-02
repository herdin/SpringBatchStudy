package com.harm.app.job;

import com.harm.app.util.LogDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;

/**
 * https://jojoldu.tistory.com/325
 * incrementer
 * validator
 * */
@Configuration
@Slf4j
public class SimpleJobConfig002 {

    @Autowired
    BatchBuilderContainer batchBuilderContainer;

    /**
     * @Value 를 이용하여 lazy binding 으로 받아오려면 @JobScope/@StepScope 가 필요하다.
     * @JobScope 와 @StepScope 의 차이는 뭘까?
     * https://stackoverflow.com/questions/51549891/spring-batch-late-binding-step-scope-or-job-scope
     * 말그대로 scope 의 생명주기와 관련이 있는듯하다.
     * So a step scope bean is the same instance for each read/process/write phase and listeners of a given step.
     * Job scope is the same instance for all steps in a job.
     * */
    @Bean
    @JobScope
//    @StepScope
    public Step simpleStep(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return batchBuilderContainer.getStepBuilderFactory().get("simple-step")
                .tasklet((contribution, chunkContext) -> {
                    LogDecorator.decorateLine(log, "simple step, job param request date (lazy bind)-> [{}]", requestDate);
                    String name = (String)chunkContext.getStepContext().getJobParameters().get("name");
                    LogDecorator.decorateLine(log, "simple step, job param name is [{}]", name);
                    return RepeatStatus.FINISHED;
                }).build();
    }


//    @Bean
    public Job job() {
        return batchBuilderContainer.getJobBuilderFactory().get("simple-job")
                .start(simpleStep(null))
                .incrementer(new RunIdIncrementer())
                .build();
    }

//    @Bean
    public Job jobWithRunIdIncrementer() {
        return batchBuilderContainer.getJobBuilderFactory().get("simple-job-with-incrementer")
                .start(simpleStep(null))
                .incrementer(new RunIdIncrementer())
                .build();
    }

//    @Bean
    public Job jobWithCustomIncrementer() {
        return batchBuilderContainer.getJobBuilderFactory().get("simple-job-with-custom-incrementer")
                .start(simpleStep(null))
                .incrementer(new CustomIncrementer())
                .build();
    }

    public static class CustomIncrementer implements JobParametersIncrementer {

        @Override
        public JobParameters getNext(JobParameters parameters) {
            return new JobParametersBuilder(parameters)
                    .addDate("currentDate", Date.from(Instant.now()))
                    .toJobParameters();
        }
    }

//    @Bean
    public Job job2WithJobListener() {
        return batchBuilderContainer.getJobBuilderFactory().get("simple-job-with-job-execution-listener")
                .start(simpleStep(null))
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.debug("before");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.debug("after {}", jobExecution.getStatus());
                    }
                })
                .build();
    }

//    @Bean
    public Job jobWithCustomAnnotatedListener() {
        return batchBuilderContainer.getJobBuilderFactory()
                .get("simple-job-with-custom-annotated-listener")
                .start(simpleStep(null))
                .incrementer(new RunIdIncrementer())
                .listener(JobListenerFactoryBean.getListener(new CustomAnnotatedListener()))
                .build();
    }

    @Slf4j
    public static class CustomAnnotatedListener {
        @BeforeJob
        public void hello() {
            log.debug("hello");
        }
        @AfterJob
        public void goodBye() {
            log.debug("good bye");
        }
    }

    //    @Bean
    public Job jobWithValidator() {
        class ParameterValidator implements JobParametersValidator {
            @Override
            public void validate(JobParameters parameters) throws JobParametersInvalidException {
                String param1 = parameters.getString("param1");
                if(StringUtils.isEmpty(param1)) {
                    throw new RuntimeException("param1 empty.");
                }
            }
        }
        //기본으로 제공되는 밸리데이터
//        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
//        defaultJobParametersValidator.setRequiredKeys(new String[]{"requireKey1"});
//        defaultJobParametersValidator.setOptionalKeys(new String[]{"optionalKey1"});

        //이렇게 섞을수도있다
//        CompositeJobParametersValidator compositeJobParametersValidator = new CompositeJobParametersValidator();
//        compositeJobParametersValidator.setValidators(Arrays.asList(
//                new ParameterValidator(),
//                new DefaultJobParametersValidator(new String[]{}, new String[]{} )
//        ));

        return batchBuilderContainer.getJobBuilderFactory().get("simple-job-with-validator")
                .start(simpleStep(null))
                .incrementer(new RunIdIncrementer())
                .validator(new ParameterValidator())
                .build();
    }
}
