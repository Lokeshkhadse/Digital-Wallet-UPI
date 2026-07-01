package com.example.Query_Service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "queryExecutor")
    public Executor queryExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        // Minimum threads always alive
        executor.setCorePoolSize(5);

        // Maximum threads
        executor.setMaxPoolSize(20);   //5 core + 15 load wale idhar ke = 20

        // Waiting queue
        executor.setQueueCapacity(100);

        // Thread name
        executor.setThreadNamePrefix("QUERY-SERVICE-");

        //joh bhi idle baithe hai thread unko destory karo
        executor.setKeepAliveSeconds(60000); //only max pool ke honge idle wale not core wale

        // Graceful shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);  // application band- exisitng task karo - then shutdown

//        executor.setAwaitTerminationSeconds(30);

        // If queue full
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;
    }

}