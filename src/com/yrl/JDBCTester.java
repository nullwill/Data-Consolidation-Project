package com.yrl;

import java.util.HashMap;

public class JDBCTester {
	public static void main(String args[]) {
		HashMap<String, Store> stores = DataLoader.getAllStores();
		for (Store s : stores.values()) {
			for (Sale sale : s.getSales()) {
				System.out.println(sale.getItems());
			}
		}
	}
}
