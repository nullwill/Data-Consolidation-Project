package com.yrl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Poop")
public abstract class Item {
    private String itemCode;
    private String storeCode;
    private String name;

    public Item(String storeCode, String itemCode, String name) {
        this.storeCode = storeCode;
    	this.itemCode = itemCode;
        this.name = name;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getName() {
        return name;
    }
    
    @JsonIgnore
	public String getStoreCode() {
		return storeCode;
	}
	
    @JsonIgnore
    public abstract Double getTaxes();

    @JsonIgnore
    public abstract Double getGrossTotal();
    
    @JsonIgnore
    public abstract Double getNetTotal();

	
    
}