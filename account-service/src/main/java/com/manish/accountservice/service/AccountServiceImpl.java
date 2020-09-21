package com.manish.accountservice.service;

import com.manish.accountservice.client.AuthServiceClient;
import com.manish.accountservice.client.StatisticsServiceClient;
import com.manish.accountservice.domain.Account;
import com.manish.accountservice.domain.User;
import com.manish.accountservice.domain.shared.Currency;
import com.manish.accountservice.domain.shared.Saving;
import com.manish.accountservice.repository.AccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatisticsServiceClient statisticsClient;

    @Autowired
    private AuthServiceClient authClient;

    @Autowired
    private AccountRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Account findByName(String accountName) {
        Assert.hasLength(accountName);
        return repository.findByName(accountName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account create(User user) {

        Account existing = repository.findByName(user.getUsername());
        Assert.isNull(existing, "account already exists: " + user.getUsername());

//		Invokes Auth Service user creation: Auth service create user of type "UserDetails" class.
        authClient.createUser(user);

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(0));
        saving.setCurrency(Currency.getDefault());
        saving.setInterest(new BigDecimal(0));
        saving.setDeposit(false);
        saving.setCapitalization(false);

        Account account = new Account();
        account.setName(user.getUsername());
        account.setLastSeen(new Date());
        account.setSaving(saving);

        // finance account details of the user
        repository.save(account);

        log.info("new account has been created: " + account.getName());

        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveChanges(String name, Account update) {

        Account account = repository.findByName(name);
        Assert.notNull(account, "can't find account with name " + name);

        account.setIncomes(update.getIncomes());
        account.setExpenses(update.getExpenses());
        account.setSaving(update.getSaving());
        account.setNote(update.getNote());
        account.setLastSeen(new Date());
        repository.save(account);

        log.debug("account {} changes has been saved", name);
        statisticsClient.updateStatistics(name, account);
    }
}
