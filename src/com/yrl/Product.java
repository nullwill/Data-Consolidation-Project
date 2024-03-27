package com.yrl;

import java.util.Comparator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "product")
public class Product extends Item {
	private Double price;
	
	public Product(String itemCode, String name, Double price) {
		super(itemCode, name);
		this.price = price;
	}

	public Double getPrice() {
		return this.price;
	}

	@Override
	public Double getTaxes() {
		return null;
	}

	@Override
	public Double getGrossTotal() {
		return null;
	}

	@Override
	public Double getNetTotal() {
		return null;
	}
	
	static Comparator<Product> cmpByItemCode = new Comparator<Product>() {
		public int compare(Product a, Product b) {
			return a.getItemCode().compareTo(b.getItemCode());
		}
	};

	@Override
	public Double getPreTaxTotal() {
		return null;
	}

}
