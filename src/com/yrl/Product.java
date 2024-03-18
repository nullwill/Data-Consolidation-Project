package com.yrl;

import java.util.Comparator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "product")
public class Product extends Item {
	private Double price;
	private static final char TYPE = 'P';
	
	public Product(String storeCode, String itemCode, String name, Double price) {
		super(storeCode, itemCode, name);
		this.price = price;
	}

	public Double getPrice() {
		return this.price;
	}

	public static char getType() {
		return TYPE;
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

}
