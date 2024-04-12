package com.yrl;

import java.util.List;

public class JDBCTester {

	public static void main(String args[]) {

//		List<Person> people = DataLoader.getAllPeople();
//		for (Person p : people) {
//			System.out.println(p.getName());
//			System.out.println(p.getUuid());
//		}
//		
//		List<Store> stores = DataLoader.getAllStores();
//
//		for (Store s : stores) {
//			System.out.println(s.getStoreCode());
//			System.out.println(s.getSales());
//		}
		
		for (int i = 1; i<=10; i++) {
		Item item = DataLoader.getItemData(i);
		
		System.out.println(item.getItemCode());
		
		}

	}
}
