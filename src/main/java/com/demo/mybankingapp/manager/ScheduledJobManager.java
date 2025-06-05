package com.demo.mybankingapp.manager;

import com.demo.mybankingapp.service.TransactionProcessingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class ScheduledJobManager {
    
    private TransactionProcessingService transactionProcessingService;
    
    @Scheduled(cron = "0 */1 * ? * *")
//    @SchedulerLock(name="taskLock",lockAtMostFor = "PT30S",lockAtLeastFor = "PT10S")
    public void printMethod(){
        log.info("Executing transaction processing now (" + LocalDateTime.now() + ")");
    }
    
    @Scheduled(cron = "0 */2 * ? * *")
//    @SchedulerLock(name="transactionProcessingLock",lockAtMostFor = "PT30S",lockAtLeastFor = "PT10S")
    public void processTransactionsScheduledJob(){
        transactionProcessingService.processTransactions();
    }
}
