package com.yrl;

/**
 * This class contains the attributes and methods pertaining
 * to a "Purchased" item, this class extends from the "Product"
 * class. 
 */

public class Purchased extends Product{

	public Purchased(String itemCode, String name, Double price) {
		super(itemCode, name, price);
	}

	@Override
	public Double getTaxes() {
		return (double) Math.round(this.getGrossTotal() * 6.5) / 100;
	}
	
	@Override
	public Double getGrossTotal() {
		return this.getPrice();
	}

	@Override
	public Double getNetTotal() {
		return this.getGrossTotal() + this.getTaxes();
	}
	
	public Double getPreTaxTotal() {
		return this.getGrossTotal();
	}

}
