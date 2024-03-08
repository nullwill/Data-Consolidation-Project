package com.yrl;

public class Address {
	private String street;
	private String city;
	private String state;
	private Integer zip;
	
	public Address() {
	}
	
	public Address(String street, String city, String state, Integer zip) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}
	
	public String getStreet() {
		return street;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getState() {
		return state;
	}
	
	public Integer getZip() {
		return zip;
	}
}
