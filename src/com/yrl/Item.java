package com.yrl;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This is the super class for the "Item" type, it is abstract
 * and contains the methods and attributes for an item.
 */

public abstract class Item implements Comparable<Item> {
    private String itemCode;
    private String name;

    public Item(String itemCode, String name) {
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
    public abstract Double getTaxes();

    @JsonIgnore
    public abstract Double getGrossTotal();
    
    @JsonIgnore
    public abstract Double getNetTotal();
    
    @JsonIgnore
    public abstract Double getPreTaxTotal();
    
    @Override
    public int compareTo(Item a) {
    	return this.itemCode.compareTo(a.itemCode);
    }
    
    
	

    
}