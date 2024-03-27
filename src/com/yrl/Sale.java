package com.yrl;

import java.time.LocalDate;
import java.util.List;

public class Sale implements Comparable<Sale> {
	private String saleCode;
	private String storeCode;
	private String customerUuid;
	private String salesPersonUuid;
	private List<Item> items;
	private LocalDate date;
	
	public Sale(String saleCode, String storeCode, String customerUuid, String salesPersonUuid, LocalDate date, List<Item> items) {
		this.saleCode = saleCode;
		this.storeCode = storeCode;
		this.customerUuid = customerUuid;
		this.salesPersonUuid = salesPersonUuid;
		this.date = date;
		this.items = items;
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
	
	public List<Item> getItems() {
		return this.items;
	}
	
	public Double getSaleGrandTotal() {
		Double total = 0.0;
		if (this.items != null) {
			for (Item i : this.items) {
				total += i.getNetTotal();
			}
		}
		
		return total;
	}
	
	public Double getSaleTaxTotal() {
		Double total = 0.0;
		if (this.items != null) {
			for (Item i : this.items) {
				total += i.getTaxes();
			}
		}
		
		return total;
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
