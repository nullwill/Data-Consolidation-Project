package com.yrl;

import java.util.List;

public class JDBCTester {

	public static void main(String args[]) {

		
		
		List<Store> stores = DataLoader.getAllStores();
		for (Store s: stores) {
			System.out.println(s.getStoreCode());
			System.out.println(s.getSales());
		}
	}
}
