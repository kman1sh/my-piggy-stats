package com.manish.statisticservice.service;

import com.google.common.collect.ImmutableMap;
import com.manish.statisticservice.client.ExchangeRatesClient;
import com.manish.statisticservice.domain.ExchangeRatesContainer;
import com.manish.statisticservice.domain.shared.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

	private static final Logger log = LoggerFactory.getLogger(ExchangeRatesServiceImpl.class);

	private ExchangeRatesContainer container;

	@Autowired
	private ExchangeRatesClient client;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Currency, BigDecimal> getCurrentRates() {

		// if container doesn't have data or data is not from today's date => fetch the data from ExchangeRatesClient API.
			if (container == null || !container.getDate().equals(LocalDate.now())) {
			container = client.getRates(Currency.getBase());
			log.info("exchange rates has been updated: {}", container);
		}

		// from container taking only 3 value which is required.
		return ImmutableMap.of(
				Currency.EUR, container.getRates().get(Currency.EUR.name()),
				Currency.RUB, container.getRates().get(Currency.RUB.name()),
				Currency.USD, BigDecimal.ONE
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {

		Assert.notNull(amount);

		Map<Currency, BigDecimal> rates = getCurrentRates();
		// dollar to rupees => ratio = rupeesRate(73.32) / dollar(1) : 73.32
		// dollar to euro => ratio = EuroRate(0.84) / dollar(1) : 0.84
		// rupees to euro => ratio = 0.84/73.32
		// NOTE: Since default base currency is Dollar, amount will be converted and stored in dollar by default.
		BigDecimal ratio = rates.get(to).divide(rates.get(from), 4, RoundingMode.HALF_UP);

		return amount.multiply(ratio);
	}
}
