package com.harm.app.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;

/**
 * CallableTaskletAdapter
 * MethodInvokingTaskletAdapter
 * SystemCommandTasklet -> dir 이 안먹는다
 * */
@Slf4j
@Configuration
public class SimpleJobConfig001 {
    @Autowired
    BatchBuilderContainer batchBuilderContainer;

//    @Bean
    public Job callableJob() {
        return batchBuilderContainer.getJobBuilderFactory()
                .get("callableJob")
                .start(callableStep())
                .build();
    }
    @Bean
    public Step callableStep() {
        return batchBuilderContainer.getStepBuilderFactory()
                .get("callableStep")
                .tasklet(callableTasklet())
                .build();
    }
    @Bean
    public CallableTaskletAdapter callableTasklet() {
        CallableTaskletAdapter callableTaskletAdapter = new CallableTaskletAdapter();
        callableTaskletAdapter.setCallable(callableObject());
        return callableTaskletAdapter;
    }
    @Bean
    public Callable<RepeatStatus> callableObject() {
        return () -> {
            log.debug("callable!");
            return RepeatStatus.FINISHED;
        };
    }


//    @Bean
    public Job methodInvokeJob() {
        return batchBuilderContainer.getJobBuilderFactory()
                .get("methodInvokeJob")
                .start(methodInvokeStep())
                .build();
    }
    @Bean
    public Step methodInvokeStep() {
        return batchBuilderContainer.getStepBuilderFactory()
                .get("methodInvokeStep")
                .tasklet(methodInvokingTasklet())
                .build();
    }
    @Bean
    public MethodInvokingTaskletAdapter methodInvokingTasklet() {
        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter = new MethodInvokingTaskletAdapter();
        methodInvokingTaskletAdapter.setTargetObject(customService());
        methodInvokingTaskletAdapter.setTargetMethod("service");
        return methodInvokingTaskletAdapter;
    }
    @Bean
    public CustomService customService() {
        return new CustomService();
    }
    @Slf4j
    public static class CustomService {
        public void service() {
            log.debug("service something");
        }
    }


//    @Bean
    public Job systemCommandJob() {
        return batchBuilderContainer.getJobBuilderFactory()
                .get("systemCommandJob")
                .start(systemCommandStep())
                .build();
    }
    @Bean
    public Step systemCommandStep() {
        return batchBuilderContainer.getStepBuilderFactory()
                .get("systemCommandStep")
                .tasklet(systemCommandTasklet())
                .build();
    }
    /**
     * 음.. dir 이라는 파일을 찾을 수 없다고 나온다. 왜이럴까?
     * */
    @Bean
    public SystemCommandTasklet systemCommandTasklet() {
        SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();
//        systemCommandTasklet.setWorkingDirectory("C:\\Users\\lg\\Downloads");
        systemCommandTasklet.setCommand("dir");
        systemCommandTasklet.setTimeout(5000);
        systemCommandTasklet.setInterruptOnCancel(true);
        return systemCommandTasklet;
    }
}
