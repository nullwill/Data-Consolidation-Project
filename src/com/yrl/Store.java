package com.yrl;

import java.util.List;

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
	
	public List<Sale> getSales() {
		return sales;
	}
	

	@Override
	public int compareTo(Store o) {
		return this.manager.getLastName().compareTo(o.manager.getLastName());
	}
}
