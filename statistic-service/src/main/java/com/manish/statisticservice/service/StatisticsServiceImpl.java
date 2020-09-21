package com.manish.statisticservice.service;

import com.google.common.collect.ImmutableMap;

import com.manish.statisticservice.domain.DataPoint;
import com.manish.statisticservice.domain.DataPointId;
import com.manish.statisticservice.domain.ItemMetric;
import com.manish.statisticservice.domain.StatisticMetric;
import com.manish.statisticservice.domain.shared.Account;
import com.manish.statisticservice.domain.shared.Item;
import com.manish.statisticservice.domain.shared.Saving;
import com.manish.statisticservice.domain.shared.Currency;
import com.manish.statisticservice.domain.shared.TimePeriod;
import com.manish.statisticservice.repository.DataPointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataPointRepository repository;

    @Autowired
    private ExchangeRatesService ratesService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataPoint> findByAccountName(String accountName) {
        Assert.hasLength(accountName);
        return repository.findByIdAccount(accountName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataPoint save(String accountName, Account account) {

        //time stamp
        Instant instant = LocalDate.now().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant();

        DataPointId pointId = new DataPointId(accountName, Date.from(instant));

        // retrieve list of (incomes) items and convert it into list of ItemMetric.
        Set<ItemMetric> incomes = new HashSet<>();

        for (Item item : account.getIncomes()) {
            //item obj to ItemMetrics obj conversion
            ItemMetric itemMetric = createItemMetric(item);
            incomes.add(itemMetric);
        }

		// retrieve list of (expenses) items and convert it into list of ItemMetric.
        Set<ItemMetric> expenses = new HashSet<>();
        for (Item item : account.getExpenses()) {
            ItemMetric itemMetric = createItemMetric(item);
            expenses.add(itemMetric);
        }

        // respective total of incomes, expenses, saving and listing them as key:value pair.
        Map<StatisticMetric, BigDecimal> statistics = createStatisticMetrics(incomes, expenses, account.getSaving());

        // retrieving currency rates from external api. may be required by frontend to do client side currency conversion when
        // user switches between currency type.
        Map<Currency, BigDecimal> rates = ratesService.getCurrentRates();

        DataPoint dataPoint = new DataPoint();
        dataPoint.setId(pointId);
        dataPoint.setIncomes(incomes);
        dataPoint.setExpenses(expenses);
        dataPoint.setStatistics(statistics);
        dataPoint.setRates(rates);

        log.debug("new datapoint has been created: {}", pointId);

        return repository.save(dataPoint);
    }

    private Map<StatisticMetric, BigDecimal> createStatisticMetrics(Set<ItemMetric> incomes, Set<ItemMetric> expenses, Saving saving) {

        //saving amount in application default currency type
        BigDecimal savingAmount = ratesService.convert(saving.getCurrency(), Currency.getBase(), saving.getAmount());

        BigDecimal expenseTotal = BigDecimal.ZERO;
        for (ItemMetric expense : expenses) {
            BigDecimal amount = expense.getAmount();
            expenseTotal = expenseTotal.add(amount);
        }

        BigDecimal incomeTotal = BigDecimal.ZERO;
        for (ItemMetric income : incomes) {
            BigDecimal amount = income.getAmount();
            incomeTotal = incomeTotal.add(amount);
        }

        return ImmutableMap.of(
                StatisticMetric.EXPENSES_AMOUNT, expenseTotal,
                StatisticMetric.INCOMES_AMOUNT, incomeTotal,
                StatisticMetric.SAVING_AMOUNT, savingAmount
        );
    }

    /**
     * Normalizes given item amount to {@link Currency#getBase()} currency with
     * {@link TimePeriod#getBase()} time period
     */
    private ItemMetric createItemMetric(Item item) {

        // to standardized : all amount will be converted in dollar based on 1 day period.

        BigDecimal amount = ratesService
                .convert(item.getCurrency(), Currency.getBase(), item.getAmount())
                .divide(item.getPeriod().getBaseRatio(), 4, RoundingMode.HALF_UP);

        return new ItemMetric(item.getTitle(), amount);
    }
}
