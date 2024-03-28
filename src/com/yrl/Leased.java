package com.yrl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * This class contains the methods and attributes pertaining
 * to a "Leased" item, it extends from the "Product" class
 */

public class Leased extends Product {
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	public Leased(String itemCode, String name, Double price, LocalDate startDate, LocalDate endDate) {
		super(itemCode, name, price);
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
	
	public Long getLeaseLength() {
		return this.getStartDate().until(this.endDate, ChronoUnit.MONTHS);
	}
	

	@Override
	public Double getGrossTotal() {
		return this.getPrice() + this.getPrice() * .5;
	}

	@Override
	public Double getNetTotal() {
		return (this.getGrossTotal() + this.getTaxes()) / this.getLeaseLength();
	}
	
	public Double getPreTaxTotal() {
		Long months = this.getStartDate().until(this.endDate, ChronoUnit.MONTHS);
		return (this.getGrossTotal() + this.getTaxes()) / months;
	}
} 
