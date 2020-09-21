package com.manish.accountservice.client;

import com.manish.accountservice.domain.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "statistics-service",  fallback = StatisticsServiceClientFallback.class)
public interface StatisticsServiceClient {

    @PutMapping(path = "/statistics/{accountName}")
    Account updateStatistics(@PathVariable("accountName") String accountName, Account account);
}
