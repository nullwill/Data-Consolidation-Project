package com.yrl;

import java.time.LocalDate;
import java.util.List;

/**
 * This class contains the methods and attributes pertaining to 
 * the "Sale" category.
 */

public class Sale implements Comparable<Sale> {
	private String saleCode;
	private Store store;
	private Person customer;
	private Person salesPerson;
	private List<Item> items;
	private LocalDate date;
	
	public Sale(String saleCode, Store store, Person customer, Person salesPerson, LocalDate date, List<Item> items) {
		this.saleCode = saleCode;
		this.store = store;
		this.customer = customer;
		this.salesPerson = salesPerson;
		this.date = date;
		this.items = items;
	}
	
	public String getSaleCode() {
		return saleCode;
	}
	public Store getStore() {
		return store;
	}
	public Person getCustomer() {
		return customer;
	}
	public Person getSalesPerson() {
		return salesPerson;
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
	
	public void addItem(Item item) {
		items.add(item);
	}

	@Override
	public int compareTo(Sale o) {
		return this.saleCode.compareTo(o.saleCode);
	}

	
}
