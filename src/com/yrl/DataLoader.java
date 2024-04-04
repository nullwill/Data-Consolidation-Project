package com.yrl;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;

/**
 * Author(s): Will Aldag & Oliver Triana
 * 
 * This is the main driver that is used to parse the CSV files and maps them
 * into their corresponding objects. The file contains methods that are used to
 * convert the objects into a formatted XML and JSON output.
 */

public class DataLoader {

	/**
	 * This method loads specifically the Person CSV formatted data and returns a
	 * list of "Person"s.
	 * 
	 * @param filename
	 * @return
	 */
	public static HashMap<String, Person> loadPersonData(String filename) {
		HashMap<String, Person> people = new HashMap<String, Person>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				List<String> email = new ArrayList<>();
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length >= 7) {
					for (int i = 7; i < tokens.length; i++) {
						email.add(tokens[i]);
					}
					Address a = new Address(tokens[3], tokens[4], tokens[5], Integer.parseInt(tokens[6]));
					Person p = new Person(tokens[0], tokens[1], tokens[2], a, email);
					people.put(tokens[0], p);
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return people;
	}

	/**
	 * This method loads specifically the Store CSV formatted data and returns a
	 * list of "Store"s.
	 * 
	 * @param filename
	 * @return
	 */
	public static HashMap<String, Store> loadStoreData(String filename, HashMap<String, Person> people) {
		HashMap<String, Store> stores = new HashMap<String, Store>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length == 6) {
					Address a = new Address(tokens[2], tokens[3], tokens[4], Integer.parseInt(tokens[5]));
					Person manager = people.get(tokens[1]);
					List<Sale> sales = new ArrayList<>();
					Store st = new Store(tokens[0], manager, a, sales);
					stores.put(tokens[0], st);
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return stores;
	}

	/**
	 * This method loads specifically the Item CSV formatted data and returns a list
	 * of "Item"s.
	 * 
	 * @param filename
	 * @return
	 */

	public static HashMap<String, Item> loadItemData(String filename) {
		HashMap<String, Item> items = new HashMap<String, Item>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length == 4) {
					char c = tokens[1].charAt(0);
					switch (c) {
					case 'P':
						Product p = new Product(tokens[0], tokens[2], Double.parseDouble(tokens[3]));
						items.put(tokens[0], p);
						break;
					case 'S':
						Service a = new Service(tokens[0], tokens[2], Double.parseDouble(tokens[3]), 0.0, null);
						items.put(tokens[0], a);
						break;
					case 'V':
						VoicePlan v = new VoicePlan(tokens[0], tokens[2], Double.parseDouble(tokens[3]), "", 0.0);
						items.put(tokens[0], v);
						break;
					case 'D':
						DataPlan d = new DataPlan(tokens[0], tokens[2], Double.parseDouble(tokens[3]), 0.0);
						items.put(tokens[0], d);
						break;
					}
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return items;
	}

	/**
	 * This method loads specifically the Sales CSV formatted data and returns a
	 * list of "Sale"s.
	 * 
	 * @param filename
	 * @return
	 */
	public static HashMap<String, Sale> loadSalesData(String filename, HashMap<String, Person> people, HashMap<String, Store> stores) {
		HashMap<String, Sale> sales = new HashMap<String, Sale>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length == 5) {
					Store store = stores.get(tokens[1]);
					Sale sa = new Sale(tokens[0], store, people.get(tokens[2]), people.get(tokens[3]), LocalDate.parse(tokens[4]), new ArrayList<>());
					store.addSale(sa);
					sales.put(tokens[0], sa);
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return sales;
	}
	
	/**
	 * This method loads specifically the SaleItems CSV formatted data
	 * and returns a list of "sold items".
	 * @param filename
	 * @param items
	 * @return
	 */
	public static void loadSaleItemsData(String filename, HashMap<String, Item> items, HashMap<String, Sale> sales, HashMap<String, Person> people) {
//		HashMap<String, List<Item>> saleItems = new HashMap<String, List<Item>>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");

				if (tokens.length >= 2) {
					Item i = items.get(tokens[1]);
					if (i instanceof Product) {
						Product p = (Product) i;
						if (tokens.length == 4) {
							Leased l = new Leased(p.getItemCode(), p.getName(), p.getPrice(),
									LocalDate.parse(tokens[2]), LocalDate.parse(tokens[3]));
//							addToMap(saleItems, tokens[0], l);
							sales.get(tokens[0]).addItem(l);
						} else {
							Purchased pu = new Purchased(p.getItemCode(), p.getName(), p.getPrice());
							sales.get(tokens[0]).addItem(pu);
						}
					} else if (i instanceof Service) {
						Service se = (Service) i;
						Service service = new Service(se.getItemCode(), se.getName(), se.getCostPerHour(),
								Double.parseDouble(tokens[2]), people.get(tokens[3]));
						sales.get(tokens[0]).addItem(service);
					} else if (i instanceof DataPlan) {
						DataPlan d = (DataPlan) i;
						DataPlan dp = new DataPlan(d.getItemCode(), d.getName(), d.getCostPerGB(),
								Double.parseDouble(tokens[2]));
						sales.get(tokens[0]).addItem(dp);
					} else if (i instanceof VoicePlan) {
						VoicePlan v = (VoicePlan) i;
						VoicePlan vp = new VoicePlan(v.getItemCode(), v.getName(), v.getPeriodCost(), tokens[2],
								Double.parseDouble(tokens[3]));
						sales.get(tokens[0]).addItem(vp);
					}
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

//		return saleItems;
	}
	
	/**
	 * This is the helper method used to add a list to a map, it creates the list if
	 * it does not yet exist, otherwise adds the given item to the list if it does exist.
	 * 
	 * @param map
	 * @param key
	 * @param i
	 */
	public static void addToMap(HashMap<String, List<Item>> map, String key, Item i) {
		if (map.containsKey(key)) {
			List<Item> itemList = map.get(key);
			itemList.add(i);
		} else {
			List<Item> newList = new ArrayList<>();
			newList.add(i);
			map.put(key, newList);
		}
	}
}
