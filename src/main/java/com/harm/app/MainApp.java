package com.harm.app;

import com.harm.app.util.LogDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@EnableBatchProcessing
@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
//        ZonedDateTime zdtSeoul = Year.of(2002).atMonth(6).atDay(18).atTime(20, 30, 00).atZone(ZoneId.of("Asia/Seoul"));
//        LogDecorator.decorateLine(log, "Time in Seoul : [{}]", zdtSeoul);
//
//        Instant instant = zdtSeoul.toInstant();
//        LogDecorator.decorateLine(log, "Instant/Timestamp in Seoul : [{}]/[{}]", instant, instant.getEpochSecond());
//
//        ZonedDateTime zdtVancouver = instant.atZone(ZoneId.of("America/Vancouver"));
//        LogDecorator.decorateLine(log, "Time in Vancouver : [{}]", zdtVancouver);
//        instant = zdtVancouver.toInstant();
//        LogDecorator.decorateLine(log, "Instant/Timestamp in Vancouver : [{}]/[{}]", instant, instant.getEpochSecond());

        SpringApplication.run(MainApp.class, args);
    }
}
