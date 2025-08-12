package com.hallbooking.config;

import com.hallbooking.bookingService.scheduler.BookingAutoCancelJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail autoCancelJobDetail() {
        return JobBuilder.newJob(BookingAutoCancelJob.class)
                .withIdentity("bookingAutoCancelJob","booking-jobs")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger autoCancelTrigger(JobDetail autoCancelJobDetail) {
        SimpleScheduleBuilder scheduleBuilder=SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(2).repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(autoCancelJobDetail)
                .withIdentity("bookingAutoCancelTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
