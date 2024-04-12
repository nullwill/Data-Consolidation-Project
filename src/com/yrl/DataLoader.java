package com.yrl;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	public static HashMap<String, Sale> loadSalesData(String filename, HashMap<String, Person> people,
			HashMap<String, Store> stores) {
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
					Sale sa = new Sale(tokens[0], store, people.get(tokens[2]), people.get(tokens[3]),
							LocalDate.parse(tokens[4]), new ArrayList<>());
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
	 * This method loads specifically the SaleItems CSV formatted data and returns a
	 * list of "sold items".
	 * 
	 * @param filename
	 * @param items
	 * @return
	 */
	public static void loadSaleItemsData(String filename, HashMap<String, Item> items, HashMap<String, Sale> sales,
			HashMap<String, Person> people) {
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
	 * it does not yet exist, otherwise adds the given item to the list if it does
	 * exist.
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

	public static Person getPersonData(int personId) {
		Person p = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String query = "SELECT Person.personId AS personId, Person.lastName AS lastName, "
				+ "Person.firstName AS firstName, Person.personUuid AS uuid, "
				+ "Address.street AS street, Address.city AS city, Address.state AS state, "
				+ "Address.zipCode AS zipCode, " + "Email.email AS email " + "FROM Person "
				+ "JOIN Address ON Person.addressId = Address.addressId "
				+ "LEFT JOIN Email ON Person.personId = Email.personId " + "WHERE Person.personId = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, personId);
			rs = ps.executeQuery();

			List<String> emails = new ArrayList<>(); // List to store emails

			while (rs.next()) {
				String email = rs.getString("email");
				if (email != null) { // Check if email is not null
					emails.add(email);
				}

				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				String uuid = rs.getString("uuid");
				String street = rs.getString("street");
				String city = rs.getString("city");
				String state = rs.getString("state");
				Integer zipCode = rs.getInt("zipCode");

				Address a = new Address(street, city, state, zipCode);
				p = new Person(uuid, firstName, lastName, a, emails);
			}

			if (p == null) {
				throw new IllegalStateException("No such person in database with id = " + personId);
			}

		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("SQLException: ");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return p;
	}

	public static Store getStoreData(int storeId) {

		Store s = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String query = "SELECT Store.storeCode AS storeCode, " + "Store.managerId AS managerId, "
				+ "Address.street AS street, Address.city AS city, Address.state AS state, "
				+ "Address.zipCode AS zipCode, "
				+ "Sale.saleId AS saleId, Sale.saleCode AS saleCode, Sale.saleDate AS date, Sale.customerId AS customerId, "
				+ "Sale.salesPersonId AS salesPersonId " + "FROM Store "
				+ "JOIN Address ON Store.addressId = Address.addressId "
				+ "LEFT JOIN Sale ON Store.storeId = Sale.storeId " + "WHERE Store.storeId = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, storeId);
			rs = ps.executeQuery();

			List<Sale> sales = new ArrayList<>();

			while (rs.next()) {
				if (rs.getString("saleId") != null) {
					String saleCode = rs.getString("saleCode");
					Person customer = getPersonData(rs.getInt("customerId"));
					Person salesPerson = getPersonData(rs.getInt("salesPersonId"));
					LocalDate date = rs.getDate("date").toLocalDate();

					Sale sale = new Sale(saleCode, null, customer, salesPerson, date, null);
					sales.add(sale);
				}
				String storeCode = rs.getString("storeCode");
				Person manager = getPersonData(rs.getInt("managerId"));
				String street = rs.getString("street");
				String city = rs.getString("city");
				String state = rs.getString("state");
				Integer zipCode = rs.getInt("zipCode");

				Address a = new Address(street, city, state, zipCode);
				s = new Store(storeCode, manager, a, sales);
			}

			if (s == null) {
				throw new IllegalStateException("No such store in database with id = " + storeId);
			}

		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("SQLException: ");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return s;
	}

	public static List<Person> getAllPeople() {
		List<Person> people = new ArrayList<>();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String personQuery = "SELECT * FROM Person";
		String emailQuery = "SELECT * FROM Email WHERE personId = ?";
		String addressQuery = "SELECT * FROM Address WHERE addressId = ?";

		PreparedStatement personPs = null;
		PreparedStatement emailPs = null;
		PreparedStatement addressPs = null;
		ResultSet personRs = null;
		ResultSet emailRs = null;
		ResultSet addressRs = null;

		try {
			// Retrieve all people
			personPs = conn.prepareStatement(personQuery);
			personRs = personPs.executeQuery();

			while (personRs.next()) {
				int personId = personRs.getInt("personId");
				String uuid = personRs.getString("personUuid");
				String firstName = personRs.getString("firstName");
				String lastName = personRs.getString("lastName");
				Address a = null;
				// Create Person object

				// Retrieve emails for the current person
				addressPs = conn.prepareStatement(addressQuery);
				addressPs.setInt(1, personId);
				addressRs = addressPs.executeQuery();
				if (addressRs.next()) {
					String street = addressRs.getString("street");
					String city = addressRs.getString("city");
					String state = addressRs.getString("state");
					Integer zipCode = addressRs.getInt("zipCode");
					a = new Address(street, city, state, zipCode);
				}
				List<String> emails = new ArrayList<>();
				emailPs = conn.prepareStatement(emailQuery);
				emailPs.setInt(1, personId);
				emailRs = emailPs.executeQuery();
				while (emailRs.next()) {
					String email = emailRs.getString("email");
					emails.add(email);
				}
				// Associate emails with the Person object
				Person person = new Person(uuid, firstName, lastName, a, emails);

				// Add Person object to the list
				people.add(person);
			}

		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (personRs != null) {
					personRs.close();
				}
				if (personPs != null) {
					personPs.close();
				}
				if (addressPs != null) {
					addressPs.close();
				}
				if (addressRs != null) {
					addressRs.close();
				}
				if (emailRs != null) {
					emailRs.close();
				}
				if (emailPs != null) {
					emailPs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("SQLException: ");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return people;
	}

	public static List<Store> getAllStores() {
		List<Store> stores = new ArrayList<>();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String storeQuery = "SELECT storeId, storeCode, managerId, addressId FROM Store;";
		String addressQuery = "SELECT * FROM Address WHERE addressId = ?";
		String saleQuery = "SELECT * FROM Sale WHERE storeId = ?";

		PreparedStatement storePs = null;
		PreparedStatement addressPs = null;
		PreparedStatement salePs = null;
		ResultSet storeRs = null;
		ResultSet addressRs = null;
		ResultSet saleRs = null;

		try {
			// Retrieve all people
			storePs = conn.prepareStatement(storeQuery);
			storeRs = storePs.executeQuery();

			while (storeRs.next()) {
				int storeId = storeRs.getInt("storeId");
				Address a = null;
				// Create Person object

				// Retrieve emails for the current person
				addressPs = conn.prepareStatement(addressQuery);
				addressPs.setInt(1, storeId);
				addressRs = addressPs.executeQuery();
				if (addressRs.next()) {
					String street = addressRs.getString("street");
					String city = addressRs.getString("city");
					String state = addressRs.getString("state");
					Integer zipCode = addressRs.getInt("zipCode");
					a = new Address(street, city, state, zipCode);
				}

				List<Sale> sales = new ArrayList<>();
				salePs = conn.prepareStatement(saleQuery);
				salePs.setInt(1, storeId);
				saleRs = salePs.executeQuery();
				while (saleRs.next()) {
					String saleCode = saleRs.getString("saleCode");
					LocalDate saleDate = saleRs.getDate("saleDate").toLocalDate();
					Person customer = getPersonData(saleRs.getInt("customerId"));
					Person salesPerson = getPersonData(saleRs.getInt("salesPersonId"));
					Sale sale = new Sale(saleCode, null, customer, salesPerson, saleDate, null);
					sales.add(sale);
				}
				// Associate emails with the Person object
				String storeCode = storeRs.getString("storeCode");
				Integer managerId = storeRs.getInt("managerId");

				Person manager = getPersonData(managerId);

				// Add Person object to the list
				Store store = new Store(storeCode, manager, a, sales);

				for (Sale s : sales) {
					s.updateStore(store);
				}

				stores.add(store);
			}

		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (storeRs != null) {
					storeRs.close();
				}
				if (storePs != null) {
					storePs.close();
				}
				if (addressPs != null) {
					addressPs.close();
				}
				if (addressRs != null) {
					addressRs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("SQLException: ");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		return stores;
	}

	public static Item getItemData(int itemId) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String query = "SELECT Item.itemCode, Item.itemName, Item.itemType, "
				+ "		SaleItem.saleItemId, SaleItem.saleId, SaleItem.itemId, SaleItem.startDate,"
				+ "     SaleItem.endDate, SaleItem.employeeId, SaleItem.baseCost, SaleItem.numGB,"
				+ "     SaleItem.numHours, SaleItem.numDays, SaleItem.phoneNumber " + "FROM Item "
				+ "		LEFT JOIN SaleItem ON Item.itemId = SaleItem.itemId " + "WHERE Item.itemId = ?;";

		PreparedStatement itemsPs = null;
		ResultSet itemsRs = null;





		try {
			itemsPs = conn.prepareStatement(query);
			itemsPs.setInt(1, itemId);
			itemsRs = itemsPs.executeQuery();

			while (itemsRs.next()) {

				String itemCode = itemsRs.getString("itemCode");
				String itemName = itemsRs.getString("itemName");
				String itemType = itemsRs.getString("itemType");

				String startDate = itemsRs.getString("startDate");
				String endDate = itemsRs.getString("endDate");
				String employeeId = itemsRs.getString("employeeId");
				Double baseCost = itemsRs.getDouble("baseCost");
				Double gb = itemsRs.getDouble("numGB");
				Double hours = itemsRs.getDouble("numHours");
				Double days = itemsRs.getDouble("numDays");
				String phoneNumber = itemsRs.getString("phoneNumber");

				if (itemType.equals("D")) {
					DataPlan d = new DataPlan(itemCode, itemName, baseCost, gb);
					return d;
				}
				if (itemType.equals("P")) {
					Product p = new Product(itemCode, itemName, baseCost);
					return p;
				}
				if (itemType.equals("S")) {
					Service e = new Service(itemCode, itemName, baseCost, hours, null);
					return e;
				}
				if (itemType.equals("V")) {
					VoicePlan v = new VoicePlan(itemCode, itemName, baseCost, phoneNumber, days);
					return v;
				}

			}

			throw new IllegalStateException("No such item in database with id = " + itemId);

		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {

				if (itemsRs != null) {
					itemsRs.close();
				}
				if (itemsPs != null) {
					itemsPs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("SQLException: ");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	}

}
