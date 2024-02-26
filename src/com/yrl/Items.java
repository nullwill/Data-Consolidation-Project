package com.yrl;

public class Items {
	private String code;
	private char type;
	private String name;
	private Double basePrice;
	
	public Items(String code, char type, String name, Double basePrice) {
		this.code = code;
		this.type = type;
		this.name = name;
		this.basePrice = basePrice;
	}
	
	public String getCode() {
		return code;
	}
	
	public char getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public Double getBasePrice() {
		return basePrice;
	}

}
