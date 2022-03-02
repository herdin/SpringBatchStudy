package com.harm.app.runner;

import com.harm.app.util.LogDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
//@Component
public class JobRegistryCheckRunner implements ApplicationRunner {
    @Autowired
    JobExplorer jobExplorer;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        jobExplorer.getJobNames().stream()
                .forEach(jobName -> {
                    try {
                        LogDecorator.decorateLine(log, "[{}] job is [{}]", jobName, jobExplorer.getJobInstanceCount(jobName));
                    } catch (NoSuchJobException e) {
                        e.printStackTrace();
                    }
                });
//        LogDecorator.decorateLine(log, "all jobs [{}]", jobExplorer.getJobNames().stream().collect(Collectors.joining(",")));

    }
}
