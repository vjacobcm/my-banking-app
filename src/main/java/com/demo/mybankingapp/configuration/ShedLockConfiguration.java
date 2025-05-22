package com.demo.mybankingapp.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
@Configuration
@Slf4j
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor="PT30S")
public class ShedLockConfiguration {
    
    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        log.info("Initializing ShedLock Configuration");
    }

    @Bean
    public LockProvider lockProvider(DataSource datasource){
        String port = environment.getProperty("server.port");
        String customLockedByValue = port+"-"+Utils.getHostname();
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(datasource))
                        .withTableName("shedlock")
                        .withLockedByValue(customLockedByValue)
                        .usingDbTime()
                        .build()
        );
    }
}
