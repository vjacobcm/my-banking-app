package com.demo.mybankingapp.manager;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@NoArgsConstructor
public class ScheduledJobManager {
    @Scheduled(cron = "0 */1 * ? * *")
    @SchedulerLock(name="taskLock",lockAtMostFor = "PT30S",lockAtLeastFor = "PT10S")
    public void printMethod(){
        System.out.println("hello world!!! " + LocalDateTime.now());
    }
}
