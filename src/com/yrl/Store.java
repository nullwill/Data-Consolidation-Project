package com.yrl;

import java.util.List;

/**
 * This class contains the methods and attributes pertaining
 * to the "Store"
 */

public class Store implements Comparable<Store>{
	private String storeCode;
	private Person manager;
	private Address address;
	private List<Sale> sales;
	
	public Store(String storeCode, Person manager, Address address, List<Sale> sales) {
		this.storeCode = storeCode;
		this.manager = manager;
		this.address = address;
		this.sales = sales;
	}

	public String getStoreCode() {
		return storeCode;
	}
	
	public Person getManager() {
		return manager;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void addSale(Sale sale) {
		sales.add(sale);
	}
	
	public List<Sale> getSales() {
		return sales;
	}
	
	public Double getNetTotalOfStore() {
		Double netTotal = 0.0;
		for (Sale s : sales) {
			netTotal += s.getSaleGrandTotal();
		}
		
		return netTotal;
	}
	

	@Override
	public int compareTo(Store o) {
		return this.storeCode.compareTo(o.storeCode);
	}
}
