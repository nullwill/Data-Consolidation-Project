package com.yrl;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * This class contains the methods and attributes pertaining to a
 * "VoicePlan" it extends from the abstract "Item" class
 */

@JacksonXmlRootElement(localName = "voicePlan")
public class VoicePlan extends Item {
	private Double periodCost;
	@JsonIgnore
	private String phoneNumber;
	@JsonIgnore
	private Double days;
	
	public VoicePlan(String itemCode, String name, Double periodCost, String phoneNumber, Double days) {
		super(itemCode, name);
		this.periodCost = periodCost;
		this.days = days;
		this.phoneNumber = phoneNumber;
	}
	
	public Double getPeriodCost() {
		return this.periodCost;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Double getDays() {
		return days;
	}
	

	@Override
	public Double getGrossTotal() {
		return (this.getDays() / 30.0) * periodCost;
	}

	@Override
	public Double getNetTotal() {
		return this.getGrossTotal() + this.getTaxes();
	}
	
	@Override
	public Double getTaxes() {
		return (double) Math.round(this.getGrossTotal() * 6.5) / 100;
	}

	static Comparator<VoicePlan> cmpByItemCode = new Comparator<VoicePlan>() {
		public int compare(VoicePlan a, VoicePlan b) {
			return a.getItemCode().compareTo(b.getItemCode());
		}
	};

	@Override
	public Double getPreTaxTotal() {
		return this.getGrossTotal();
	}
}
