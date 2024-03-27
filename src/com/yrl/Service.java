package com.yrl;


import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "service")
public class Service extends Item {
	private Double costPerHour;
	private Double hoursBilled;
	private String employeeUuid;

	public Service(String itemCode, String name, Double costPerHour, Double hoursBilled, String employeeUuid) {
		super(itemCode, name);
		this.costPerHour = costPerHour;
		this.hoursBilled = hoursBilled;
		this.employeeUuid = employeeUuid;
	}
	
	@JsonIgnore
	public Double getHoursBilled() {
		return hoursBilled;
	}
	
	public Double getCostPerHour() {
		return costPerHour;
	}

	
	@JsonIgnore
	public String getEmployeeUuid() {
		return employeeUuid;
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
