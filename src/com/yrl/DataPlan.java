package com.yrl;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "dataPlan")
public class DataPlan extends Item {
	private Double costPerGB;
	private Double GbPurchased;
	private static final char TYPE = 'D';

	public DataPlan(String storeCode, String itemCode, String name, Double costPerGB, Double GbPurchased) {
		super(storeCode, itemCode, name);
		this.costPerGB = costPerGB;
		this.GbPurchased = GbPurchased;
	}
	
	@JsonIgnore
	public Double getGbPurchased() {
		return this.GbPurchased;
	}

	public Double getCostPerGB() {
		return costPerGB;
	}
	
	@JsonIgnore
	public static char getType() {
		return TYPE;
	}

	@JsonIgnore
	@Override
	public Double getTaxes() {
		return this.getGrossTotal() * 0.055;
	}
	
	@JsonIgnore
	@Override
	public Double getGrossTotal() {
		return GbPurchased * costPerGB;
	}
	
	@JsonIgnore
	@Override
	public Double getNetTotal() {
		return this.getGrossTotal() + this.getTaxes();
	}
	
	static Comparator<DataPlan> cmpByItemCode = new Comparator<DataPlan>() {
		public int compare(DataPlan a, DataPlan b) {
			return a.getItemCode().compareTo(b.getItemCode());
		}
	};

}