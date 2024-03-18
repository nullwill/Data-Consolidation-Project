package com.yrl;

import java.time.LocalDate;

public class Sale implements Comparable<Sale> {
	private String saleCode;
	private String storeCode;
	private String customerUuid;
	private String salesPersonUuid;
	private LocalDate date;
	
	public Sale(String saleCode, String storeCode, String customerUuid, String salesPersonUuid, LocalDate date) {
		this.saleCode = saleCode;
		this.storeCode = storeCode;
		this.customerUuid = customerUuid;
		this.salesPersonUuid = salesPersonUuid;
		this.date = date;
	}
	
	public String getSaleCode() {
		return saleCode;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public String getCustomerUuid() {
		return customerUuid;
	}
	public String getSalesPersonUuid() {
		return salesPersonUuid;
	}
	public LocalDate getDate() {
		return date;
	}
	
	public int cmpBySaleCode(Sale a, Sale b) {
		return a.getSaleCode().compareTo(b.getSaleCode());
	}

	@Override
	public int compareTo(Sale o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
