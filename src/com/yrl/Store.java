package com.yrl;

public class Store extends Address {
	private String storeCode;
	private String managerUuid;
	
	public Store(String storeCode, String managerUuid, String street, String state, String city, Integer zip) {
		super(street, state, city, zip);
		this.storeCode = storeCode;
		this.managerUuid = managerUuid;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getManagerUuid() {
		return managerUuid;
	}

	public void setManagerUuid(String managerUuid) {
		this.managerUuid = managerUuid;
	}
}
