package com.yrl;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "voicePlan")
public class VoicePlan extends Item {
	private Double periodCost;
	@JsonIgnore
	private String phoneNumber;
	@JsonIgnore
	private Double days;
	@JsonIgnore
	private static final char TYPE = 'V';

	public VoicePlan(String storeCode, String itemCode, String name, Double periodCost, String phoneNumber, Double days) {
		super(storeCode, itemCode, name);
		this.periodCost = periodCost;
		this.days = days;
		this.phoneNumber = phoneNumber;
	}
	
	public Double getPeriodCost() {
		return this.periodCost;
	}

	public static char getType() {
		return TYPE;
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
		return this.getGrossTotal() * periodCost * 0.065;
	}

	static Comparator<VoicePlan> cmpByItemCode = new Comparator<VoicePlan>() {
		public int compare(VoicePlan a, VoicePlan b) {
			return a.getItemCode().compareTo(b.getItemCode());
		}
	};
}
