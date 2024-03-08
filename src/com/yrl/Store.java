package com.yrl;

public class Store extends Address {
	private String storeCode;
	private Person manager;
	private Address address;
	private Sales sales;
	
	public Store(String storeCode, Person manager, Address address, Sales sales) {
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
	
	public Sales getSales() {
		return sales;
	}
}
