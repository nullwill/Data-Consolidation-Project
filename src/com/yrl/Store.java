package com.yrl;

import java.util.List;

public class Store {
	private String storeCode;
	private Manager manager;
	private Address address;
	private List<Sale> sales;
	
	public Store(String storeCode, Manager manager, Address address, List<Sale> sales) {
		this.storeCode = storeCode;
		this.manager = manager;
		this.address = address;
		this.sales = sales;
	}

	public String getStoreCode() {
		return storeCode;
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public List<Sale> getSales() {
		return sales;
	}
}
