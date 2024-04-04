package com.yrl;


import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * This class contains the attributes and methods pertaining to
 * the Service item, which extends from the abstract "Item" class.
 */

@JacksonXmlRootElement(localName = "service")
public class Service extends Item {
	private Double costPerHour;
	private Double hoursBilled;
	private Person employee;

	public Service(String itemCode, String name, Double costPerHour, Double hoursBilled, Person employee) {
		super(itemCode, name);
		this.costPerHour = costPerHour;
		this.hoursBilled = hoursBilled;
		this.employee = employee;
	}
	
	@JsonIgnore
	public Double getHoursBilled() {
		return hoursBilled;
	}
	
	public Double getCostPerHour() {
		return costPerHour;
	}

	
	@JsonIgnore
	public Person getEmployee() {
		return employee;
	}
	
	@Override
	public Double getTaxes() {
		return (double) Math.round(this.getGrossTotal() * 3.5) / 100;
	}
	
	@Override
	public Double getGrossTotal() {
		return hoursBilled * costPerHour;
	}

	@Override
	public Double getNetTotal() {
		return this.getGrossTotal() + this.getTaxes();
	}
	
	static Comparator<Service> cmpByItemCode = new Comparator<Service>() {
		public int compare(Service a, Service b) {
			return a.getItemCode().compareTo(b.getItemCode());
		}
	};

	@Override
	public Double getPreTaxTotal() {
		return this.getGrossTotal();
	}
}
