package com.example.Query_Service.util;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionPoolMonitor {

    private final HikariDataSource hikariDataSource;

    @Scheduled(fixedDelay = 60000)
    public void printPoolStatus() {

        HikariPoolMXBean pool =
                hikariDataSource.getHikariPoolMXBean();

        log.info("");
        log.info("==========================================================");
        log.info("        QUERY SERVICE DATABASE CONNECTION POOL");
        log.info("==========================================================");
        log.info("Pool Name          : {}", hikariDataSource.getPoolName());
        log.info("Total Connections  : {}", pool.getTotalConnections());
        log.info("Active Connections : {}", pool.getActiveConnections());
        log.info("Idle Connections   : {}", pool.getIdleConnections());
        log.info("Waiting Threads    : {}", pool.getThreadsAwaitingConnection());
        log.info("==========================================================");
        log.info("");
    }
}