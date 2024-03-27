package com.yrl;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
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
	public static List<Person> loadPersonData(String filename) {
		List<Person> people = new ArrayList<>();

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
					people.add(p);
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
	public static List<Store> loadStoreData(String filename, List<Person> people) {
		List<Store> stores = new ArrayList<>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length == 6) {
					Collections.sort(people, Person.cmpByUuid);
					Person key = new Person(tokens[1], "", "", null, null);
					Address a = new Address(tokens[2], tokens[3], tokens[4], Integer.parseInt(tokens[5]));
					int index = Collections.binarySearch(people, key, Person.cmpByUuid);
					Person p = people.get(index);
					Person m = new Person(p.getUuid(), p.getFirstName(), p.getLastName(), p.getAddress(),
							p.getEmails());

					List<Sale> sales = new ArrayList<>();
					Store st = new Store(tokens[0], m, a, sales);
					stores.add(st);
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
//		List<Item> items = new ArrayList<>();
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
						Service a = new Service(tokens[0], tokens[2], Double.parseDouble(tokens[3]), 0.0, "");
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
	public static List<Sale> loadSalesData(String filename) {
		List<Sale> sales = new ArrayList<>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length == 5) {
					Sale sa = new Sale(tokens[0], tokens[1], tokens[2], tokens[3], LocalDate.parse(tokens[4]), null);
					sales.add(sa);
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return sales;
	}

	public static HashMap<String, List<Item>> loadSaleItemsData(String filename, HashMap<String, Item> items,
			List<Sale> sales) {
		HashMap<String, List<Item>> saleItems = new HashMap<String, List<Item>>();

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
							addToMap(saleItems, tokens[0], l);
						} else {
							Purchased pu = new Purchased(p.getItemCode(), p.getName(), p.getPrice());
							addToMap(saleItems, tokens[0], pu);
						}
					} else if (i instanceof Service) {
						Service se = (Service) i;
						Service service = new Service(se.getItemCode(), se.getName(), se.getCostPerHour(),
								Double.parseDouble(tokens[2]), tokens[3]);
						addToMap(saleItems, tokens[0], service);
					} else if (i instanceof DataPlan) {
						DataPlan d = (DataPlan) i;
						DataPlan dp = new DataPlan(d.getItemCode(), d.getName(), d.getCostPerGB(),
								Double.parseDouble(tokens[2]));
						addToMap(saleItems, tokens[0], dp);
					} else if (i instanceof VoicePlan) {
						VoicePlan v = (VoicePlan) i;
						VoicePlan vp = new VoicePlan(v.getItemCode(), v.getName(), v.getPeriodCost(), tokens[2],
								Double.parseDouble(tokens[3]));
						addToMap(saleItems, tokens[0], vp);
					}
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return saleItems;
	}

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
