package com.yrl;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
					Person key = new Manager(tokens[1], "", "", null, null);
					Address a = new Address(tokens[2], tokens[3], tokens[4], Integer.parseInt(tokens[5]));
					int index = Collections.binarySearch(people, key, Person.cmpByUuid);
					Person p = people.get(index);
					Manager m = new Manager(p.getUuid(), p.getFirstName(), p.getLastName(), p.getAddress(),
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

	public static List<List<?>> loadItemData(String filename) {
		List<List<?>> items = new ArrayList<>();

		List<Product> products = new ArrayList<>();
		List<Service> services = new ArrayList<>();
		List<VoicePlan> voicePlans = new ArrayList<>();
		List<DataPlan> dataPlans = new ArrayList<>();

		items.add(products);
		items.add(services);
		items.add(voicePlans);
		items.add(dataPlans);

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
						Product p = new Product("", tokens[0], tokens[2], Double.parseDouble(tokens[3]));
						products.add(p);
						break;
					case 'S':
						Service a = new Service("", tokens[0], tokens[2], Double.parseDouble(tokens[3]), 0.0, "");
						services.add(a);
						break;
					case 'V':
						VoicePlan v = new VoicePlan("", tokens[0], tokens[2], Double.parseDouble(tokens[3]), "", 0.0);
						voicePlans.add(v);
						break;
					case 'D':
						DataPlan d = new DataPlan("", tokens[0], tokens[2], Double.parseDouble(tokens[3]), 0.0);
						dataPlans.add(d);
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
					Sale sa = new Sale(tokens[0], tokens[1], tokens[2], tokens[3], LocalDate.parse(tokens[4]));
					sales.add(sa);
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return sales;
	}

	public static List<List<?>> loadSaleItemsData(String filename, List<List<?>> items, List<Sale> sales) {
		List<List<?>> soldItems = new ArrayList<>();

		List<Leased> leasedProducts = new ArrayList<>();
		List<Purchased> purchasedProducts = new ArrayList<>();
		List<Service> services = new ArrayList<>();
		List<VoicePlan> voicePlans = new ArrayList<>();
		List<DataPlan> dataPlans = new ArrayList<>();

		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				switch (tokens[1].charAt(0)) {
				case 'e':
					Product key = new Product("", tokens[1], "", 0.0);
					List<?> list = items.get(0);

					if (list instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<Product> products = (List<Product>) list;
						Collections.sort(products, Product.cmpByItemCode);
						int index = Collections.binarySearch(products, key, Product.cmpByItemCode);
						Product p = products.get(index);
						if (tokens.length == 4) {
							Leased l = new Leased(tokens[0], p.getItemCode(), p.getName(), p.getPrice(),
									LocalDate.parse(tokens[2]), LocalDate.parse(tokens[3]));
							leasedProducts.add(l);
						} else {
							Purchased pu = new Purchased(tokens[0], p.getItemCode(), p.getName(), p.getPrice());
							purchasedProducts.add(pu);
						}
					}
					break;
				case 's':
					Service key1 = new Service("", tokens[1], "", 0.0, 0.0, "");
					list = items.get(1);

					if (list instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<Service> serv = (List<Service>) list;
						Collections.sort(serv, Service.cmpByItemCode);
						int index = Collections.binarySearch(serv, key1, Service.cmpByItemCode);
						Service se = serv.get(index);
						Service newService = new Service(tokens[0], se.getItemCode(), se.getName(),
								se.getCostPerHour(), Double.parseDouble(tokens[2]), tokens[3]);
						services.add(newService);
					}
					break;
				case 'p':
					if (tokens.length == 2) {
						VoicePlan key2 = new VoicePlan("", tokens[1], "", 0.0, "", 0.0);
						list = items.get(2);
						if (list instanceof List<?>) {
							@SuppressWarnings("unchecked")
							List<VoicePlan> vp = (List<VoicePlan>) list;
							Collections.sort(vp, VoicePlan.cmpByItemCode);
							int index = Collections.binarySearch(vp, key2, VoicePlan.cmpByItemCode);
							VoicePlan v = vp.get(index);
							VoicePlan newVp = new VoicePlan(tokens[0], v.getItemCode(), v.getName(), v.getPeriodCost(), tokens[2], Double.parseDouble(tokens[3]));
							voicePlans.add(newVp);
						}
					} else {
						DataPlan key3 = new DataPlan("", tokens[1], "", 0.0, 0.0);
						list = items.get(3);
						if (list instanceof List<?>) {
							@SuppressWarnings("unchecked")
							List<DataPlan> dp = (List<DataPlan>) list;
							Collections.sort(dp, DataPlan.cmpByItemCode);
							int index = Collections.binarySearch(dp, key3, DataPlan.cmpByItemCode);
							DataPlan d = dp.get(index);
							DataPlan newDp = new DataPlan(tokens[0], d.getItemCode(), d.getName(), d.getCostPerGB(), Double.parseDouble(tokens[2]));
							dataPlans.add(newDp);
						}
					}

				}
					
				s.close();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return soldItems;
	}
}
