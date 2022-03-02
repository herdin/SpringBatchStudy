package com.harm.app.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultRunner implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(DefaultRunner.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("hello, runner");
    }
}
