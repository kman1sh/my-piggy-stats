package com.manish.accountservice.client;


import com.manish.accountservice.domain.Account;
import org.springframework.stereotype.Component;

/**
 * @author cdov
 */
@Component
public class StatisticsServiceClientFallback implements StatisticsServiceClient {

    @Override
    public Account updateStatistics(String accountName, Account account) {
        System.out.println("Error during update statistics for account: {}" + accountName);
        return null;
    }
}
