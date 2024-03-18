package com.yrl;

import java.time.LocalDate;

public class Leased extends Product {
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	public Leased(String storeCode, String itemCode, String name, Double price, LocalDate startDate, LocalDate endDate) {
		super(storeCode, itemCode, name, price);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	@Override
	public Double getTaxes() {
		return 0.0;
	}
	

	@Override
	public Double getGrossTotal() {
		return this.getPrice();
	}

	@Override
	public Double getNetTotal() {
		return this.getGrossTotal() + this.getTaxes();
	}
}
