package com.manish.statisticservice.domain.shared;

public enum Currency {

	USD, EUR, RUB;

	public static Currency getBase() {
		return USD;
	}
}
