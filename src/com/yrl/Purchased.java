package com.yrl;

public class Purchased extends Product{

	public Purchased(String storeCode, String itemCode, String name, Double price) {
		super(storeCode, itemCode, name, price);
	}

	@Override
	public Double getTaxes() {
		return this.getPrice() * .065;
	}
	
	@Override
	public Double getGrossTotal() {
		return this.getPrice();
	}

	@Override
	public Double getNetTotal() {
		return this.getGrossTotal() + this.getTaxes();
	}

}
