package com.yrl;

import java.util.Iterator;
import java.util.List;

public class JDBCTester {

	public static void main(String args[]) {
//
//		List<Person> people = DataLoader.getAllPeople();
//		for (Person p : people) {
//			System.out.println(p.getName());
//			System.out.println(p.getUuid());
//		}
//		System.out.println();
//		
//		List<Store> stores = DataLoader.getAllStores();
//
//		for (Store s : stores) {
//			System.out.println(s.getStoreCode());
//			System.out.println(s.getSales());
//		}
//		System.out.println();
//
//		Item item = DataLoader.getItemData(4);
//		System.out.println(item.getName());
//
//		List<Item> items = DataLoader.getAllItems();
//		for (Item m : items) {
//			System.out.println(m.getItemCode());
//			System.out.println(m.getName());
//		}
//		
		Sale sale = DataLoader.getSaleData(2);
		System.out.println(sale.getSaleCode());
	}

}
