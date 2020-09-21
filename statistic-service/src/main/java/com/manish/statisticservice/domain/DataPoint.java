package com.manish.statisticservice.domain;

import com.manish.statisticservice.domain.shared.Currency;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * Represents daily time series data point containing
 * current account state. All info of statistics
 */
@Document(collection = "datapoints")
public class DataPoint {

	@Id
	private DataPointId id; // accountName+datestamp obj

	//reduced from list of Item obj to  list of ItemMetrics
	//Item: title, amount,currency,TimePeriod.
	// ItemMetrics: only title and amount.
	private Set<ItemMetric> incomes;

	//reduced from list of Item obj to  list of ItemMetrics
	private Set<ItemMetric> expenses;

	// respective category total:
	// key: (income, expense, saving), value: respective total amount
	private Map<StatisticMetric, BigDecimal> statistics;

	// retrieving currency rates from external api. may be required by frontend to do client side currency conversion when
	// user switches between currency type.
	private Map<Currency, BigDecimal> rates;

	public DataPointId getId() {
		return id;
	}

	public void setId(DataPointId id) {
		this.id = id;
	}

	public Set<ItemMetric> getIncomes() {
		return incomes;
	}

	public void setIncomes(Set<ItemMetric> incomes) {
		this.incomes = incomes;
	}

	public Set<ItemMetric> getExpenses() {
		return expenses;
	}

	public void setExpenses(Set<ItemMetric> expenses) {
		this.expenses = expenses;
	}

	public Map<StatisticMetric, BigDecimal> getStatistics() {
		return statistics;
	}

	public void setStatistics(Map<StatisticMetric, BigDecimal> statistics) {
		this.statistics = statistics;
	}

	public Map<Currency, BigDecimal> getRates() {
		return rates;
	}

	public void setRates(Map<Currency, BigDecimal> rates) {
		this.rates = rates;
	}
}
