package com.yrl;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Author(s): Will Aldag & Oliver Triana Date: 2024-04-26
 * 
 * This is the main driver that is used to parse SQL Database Data
 * and maps them into their corresponding objects.
 */

public class DataLoader {

	/**
	 * This is a helper method that loads a single "Person" given that person's SQL
	 * id.
	 * 
	 * @param personId
	 * @return
	 */
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

			List<String> emails = new ArrayList<>();

			while (rs.next()) {
				String email = rs.getString("email");
				if (email != null) {
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

	/**
	 * This is a helper method that loads a single "Store" given an SQL storeId.
	 * 
	 * @param storeId
	 * @return
	 */
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

	/**
	 * This method loads in all of the people in an SQL database into a HashMap with
	 * the Person's "uuid" as the key, and the Person as the value.
	 * 
	 * @return
	 */
	public static HashMap<String, Person> getAllPeople() {
		HashMap<String, Person> people = new HashMap<String, Person>();
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

		PreparedStatement personPs = null;
		PreparedStatement emailPs = null;

		ResultSet personRs = null;
		ResultSet emailRs = null;

		try {
			personPs = conn.prepareStatement(personQuery);
			personRs = personPs.executeQuery();

			while (personRs.next()) {
				int personId = personRs.getInt("personId");
				String uuid = personRs.getString("personUuid");
				String firstName = personRs.getString("firstName");
				String lastName = personRs.getString("lastName");
				Address a = getAddressFromId(personRs.getInt("addressId"));

				List<String> emails = new ArrayList<>();
				emailPs = conn.prepareStatement(emailQuery);
				emailPs.setInt(1, personId);
				emailRs = emailPs.executeQuery();
				while (emailRs.next()) {
					String email = emailRs.getString("email");
					emails.add(email);
				}

				Person person = new Person(uuid, firstName, lastName, a, emails);

				people.put(uuid, person);
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

	/**
	 * This is the helper method used to retrieve an address from any given ID.
	 * 
	 * @param id
	 * @return
	 */
	public static Address getAddressFromId(int id) {
		Address a = null;
		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String query = "SELECT * FROM Address WHERE addressId = ?";
			PreparedStatement psQuery = conn.prepareStatement(query);
			psQuery.setInt(1, id);
			ResultSet rsQuery = psQuery.executeQuery();

			if (rsQuery.next()) {
				String street = rsQuery.getString("street");
				String city = rsQuery.getString("city");
				String state = rsQuery.getString("state");
				Integer zip = rsQuery.getInt("zipCode");

				a = new Address(street, city, state, zip);
			}

			rsQuery.close();
			psQuery.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return a;
	}

	/**
	 * This is a helper method used to retrieve the Sales from a store given the
	 * storeId (identified by <code>storeId</code>).
	 * 
	 * @param storeId
	 * @return
	 */
	public static List<Sale> getSalesFromStoreId(int storeId) {
		List<Sale> sales = new ArrayList<>();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String query = "SELECT * FROM Sale WHERE storeId = ?";
			PreparedStatement psQuery = conn.prepareStatement(query);
			psQuery.setInt(1, storeId);
			ResultSet rsQuery = psQuery.executeQuery();

			while (rsQuery.next()) {
				String saleCode = rsQuery.getString("saleCode");
				Person customer = getPersonData(rsQuery.getInt("customerId"));
				Person salesPerson = getPersonData(rsQuery.getInt("salesPersonId"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate date = LocalDate.parse(rsQuery.getString("saleDate"), formatter);
				List<Item> items = getItemsFromSaleId(rsQuery.getInt("saleId"));
				Sale s = new Sale(saleCode, null, customer, salesPerson, date, items);

				sales.add(s);
			}

			rsQuery.close();
			psQuery.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return sales;
	}

	/**
	 * This is a helper method used to retrieve the items on a particular sale given
	 * the saleId (identified by <code>saleId</code>).
	 * 
	 * @param saleId
	 * @return
	 */
	public static List<Item> getItemsFromSaleId(int saleId) {
		List<Item> items = new ArrayList<>();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String query = "SELECT * FROM SaleItem WHERE saleId = ?";
			PreparedStatement psQuery = conn.prepareStatement(query);
			psQuery.setInt(1, saleId);
			ResultSet rsQuery = psQuery.executeQuery();

			while (rsQuery.next()) {
				String itemQuery = "SELECT * FROM Item where itemId = ?";
				PreparedStatement itemPs = conn.prepareStatement(itemQuery);
				itemPs.setInt(1, rsQuery.getInt("itemId"));
				ResultSet itemRs = itemPs.executeQuery();

				while (itemRs.next()) {
					String itemType = itemRs.getString("itemType");

					switch (itemType) {
					case "D":
						DataPlan d = new DataPlan(itemRs.getString("itemCode"), itemRs.getString("itemName"),
								itemRs.getDouble("baseCost"), rsQuery.getDouble("numGB"));
						items.add(d);
						break;
					case "P":
						if (rsQuery.getString("startDate") != null) {
							Leased l = new Leased(itemRs.getString("itemCode"), itemRs.getString("itemName"),
									itemRs.getDouble("baseCost"), rsQuery.getDate("startDate").toLocalDate(),
									rsQuery.getDate("endDate").toLocalDate());
							items.add(l);
						} else {
							Purchased p = new Purchased(itemRs.getString("itemCode"), itemRs.getString("itemName"),
									itemRs.getDouble("baseCost"));
							items.add(p);
						}
						break;
					case "S":
						Service s = new Service(itemRs.getString("itemCode"), itemRs.getString("itemName"),
								itemRs.getDouble("baseCost"), rsQuery.getDouble("numHours"),

								getPersonData(rsQuery.getInt("employeeId")));
						items.add(s);
						break;
					case "V":
						VoicePlan v = new VoicePlan(itemRs.getString("itemCode"), itemRs.getString("itemName"),
								itemRs.getDouble("baseCost"), rsQuery.getString("phoneNumber"),
								rsQuery.getDouble("numDays"));
						items.add(v);
						break;

					}
				}

				itemPs.close();
				itemRs.close();

			}
			rsQuery.close();
			psQuery.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return items;
	}

	/**
	 * This method loads all of the stores in an SQL database into a HashMap using
	 * the store code (identified by <code>storeCode</code>) as a key and the Store
	 * as a value.
	 * 
	 * @return
	 */
	public static HashMap<String, Store> getAllStores() {
		HashMap<String, Store> stores = new HashMap<>();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String storeQuery = "SELECT * from Store";

		PreparedStatement storePs = null;
		PreparedStatement salePs = null;
		PreparedStatement saleItemPs = null;
		ResultSet storeRs = null;
		ResultSet saleRs = null;
		ResultSet saleItemRs = null;

		try {
			storePs = conn.prepareStatement(storeQuery);
			storeRs = storePs.executeQuery();

			while (storeRs.next()) {
				int storeId = storeRs.getInt("storeId");
				Address a = getAddressFromId(storeRs.getInt("addressId"));

				List<Sale> sales = new ArrayList<>();

				sales = getSalesFromStoreId(storeId);

				String storeCode = storeRs.getString("storeCode");
				Integer managerId = storeRs.getInt("managerId");

				Person manager = getPersonData(managerId);

				Store store = new Store(storeCode, manager, a, sales);

				stores.put(storeCode, store);
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
				if (salePs != null) {
					salePs.close();
				}
				if (saleItemPs != null) {
					saleItemPs.close();
				}
				if (saleRs != null) {
					saleRs.close();
				}
				if (saleItemRs != null) {
					saleItemRs.close();
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

	/**
	 * This method loads all of the items from an SQL database into a HashMap using
	 * the itemCode (identified by <code>itemCode</code>) as the key and the Item
	 * being the value.
	 * 
	 * @return
	 */
	public static HashMap<String, Item> getAllItems() {
		HashMap<String, Item> items = new HashMap<String, Item>();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String query = "SELECT Item.itemCode, Item.itemName, Item.itemType, Item.baseCost, "
				+ "		SaleItem.saleItemId, SaleItem.saleId, SaleItem.itemId, SaleItem.startDate,"
				+ "     SaleItem.endDate, SaleItem.employeeId, SaleItem.numGB,"
				+ "     SaleItem.numHours, SaleItem.numDays, SaleItem.phoneNumber " + "FROM Item "
				+ "		LEFT JOIN SaleItem ON Item.itemId = SaleItem.itemId;";

		PreparedStatement itemsPs = null;
		ResultSet itemsRs = null;

		try {
			itemsPs = conn.prepareStatement(query);
			itemsRs = itemsPs.executeQuery();

			while (itemsRs.next()) {

				String itemCode = itemsRs.getString("itemCode");
				String itemName = itemsRs.getString("itemName");
				String itemType = itemsRs.getString("itemType");
				Double baseCost = itemsRs.getDouble("baseCost");
				Double gb = itemsRs.getDouble("numGB");
				Double hours = itemsRs.getDouble("numHours");
				Double days = itemsRs.getDouble("numDays");
				String phoneNumber = itemsRs.getString("phoneNumber");

				if (itemType.equals("D")) {
					DataPlan d = new DataPlan(itemCode, itemName, baseCost, gb);
					items.put(itemCode, d);
				}
				if (itemType.equals("P")) {
					Product p = new Product(itemCode, itemName, baseCost);
					items.put(itemCode, p);
				}
				if (itemType.equals("S")) {
					Service e = new Service(itemCode, itemName, baseCost, hours, null);
					items.put(itemCode, e);
				}
				if (itemType.equals("V")) {
					VoicePlan v = new VoicePlan(itemCode, itemName, baseCost, phoneNumber, days);
					items.put(itemCode, v);
				}

			}

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

		return items;
	}

	/**
	 * This method returns a Sale from a database given an SQL saleId (identified by
	 * <code>saleId</code>).
	 * 
	 * @param saleId
	 * @return
	 */
	public static Sale getSaleData(int saleId) {
		Sale s = null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String query = "SELECT " + "    Sale.saleId as saleId, " + "    Sale.saleCode as saleCode, "
				+ "    Sale.storeId as storeId, " + "    Sale.customerId as customerId, "
				+ "    Sale.salesPersonId as salesPersonId, " + "    Sale.saleDate as saleDate " + "FROM Sale;";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {

				String saleCode = rs.getString("saleCode");
				Integer storeId = rs.getInt("storeId");
				Integer customerId = rs.getInt("customerId");
				Integer salesPersonId = rs.getInt("salesPersonId");
				LocalDate saleDate = LocalDate.parse(rs.getString("saleDate"));

				Store store = DataLoader.getStoreData(storeId);
				Person customer = DataLoader.getPersonData(customerId);
				Person salesPerson = DataLoader.getPersonData(salesPersonId);

				s = new Sale(saleCode, store, customer, salesPerson, saleDate, null);

			}

			if (s == null) {
				throw new IllegalStateException("No such sale in database with id = " + saleId);
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

	/**
	 * This method is used to load all of the sales in a database into a HashMap
	 * with the sale code (identified by <code>saleCode</code>) as a key and the
	 * Sale itself as the value.
	 * 
	 * @return
	 */
	public static HashMap<String, Sale> getAllSales() {
		HashMap<String, Sale> sales = new HashMap<String, Sale>();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String saleQuery = "SELECT * FROM Sale";
		String saleItemQuery = "SELECT si.*, i.* FROM SaleItem si JOIN Item i ON si.itemId = i.itemId WHERE si.saleId = ?";

		PreparedStatement salePs = null;
		PreparedStatement saleItemPs = null;
		ResultSet saleRs = null;
		ResultSet saleItemRs = null;

		try {
			salePs = conn.prepareStatement(saleQuery);
			saleRs = salePs.executeQuery();

			while (saleRs.next()) {
				int saleId = saleRs.getInt("saleId");
				String saleCode = saleRs.getString("saleCode");
				LocalDate saleDate = LocalDate.parse(saleRs.getString("saleDate"));
				Person customer = DataLoader.getPersonData(saleRs.getInt("customerId"));
				Person salesPerson = DataLoader.getPersonData(saleRs.getInt("salesPersonId"));
				int storeId = saleRs.getInt("storeId");
				Store store = getStoreData(storeId);

				saleItemPs = conn.prepareStatement(saleItemQuery);
				saleItemPs.setInt(1, saleId);
				saleItemRs = saleItemPs.executeQuery();
				List<Item> items = new ArrayList<>();
				while (saleItemRs.next()) {
					String itemType = saleItemRs.getString("itemType");
					switch (itemType) {
					case "D":
						DataPlan d = new DataPlan(saleItemRs.getString("itemCode"), saleItemRs.getString("itemName"),
								saleItemRs.getDouble("baseCost"), saleItemRs.getDouble("numGB"));
						items.add(d);
						break;
					case "P":
						if (saleItemRs.getString("startDate") != null) {
							Leased l = new Leased(saleItemRs.getString("itemCode"), saleItemRs.getString("itemName"),
									saleItemRs.getDouble("baseCost"), saleItemRs.getDate("startDate").toLocalDate(),
									saleItemRs.getDate("endDate").toLocalDate());
							items.add(l);
						} else {
							Purchased p = new Purchased(saleItemRs.getString("itemCode"),
									saleItemRs.getString("itemName"), saleItemRs.getDouble("baseCost"));
							items.add(p);
						}
						break;
					case "S":
						Service s = new Service(saleItemRs.getString("itemCode"), saleItemRs.getString("itemName"),
								saleItemRs.getDouble("baseCost"), saleItemRs.getDouble("numHours"),
								DataLoader.getPersonData(saleItemRs.getInt("employeeId")));
						items.add(s);
						break;
					case "V":
						VoicePlan v = new VoicePlan(saleItemRs.getString("itemCode"), saleItemRs.getString("itemName"),
								saleItemRs.getDouble("baseCost"), saleItemRs.getString("phoneNumber"),
								saleItemRs.getDouble("numDays"));
						items.add(v);
						break;
					}
				}

				Sale sale = new Sale(saleCode, store, customer, salesPerson, saleDate, items);
				sales.put(saleCode, sale);
			}

			if (sales.isEmpty()) {
				throw new IllegalStateException("No sales found in the database.");
			}
		} catch (SQLException e) {
			System.out.println("SQLException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (saleRs != null) {
					saleRs.close();
				}
				if (salePs != null) {
					salePs.close();
				}
				if (saleItemPs != null) {
					saleItemPs.close();
				}
				if (saleItemRs != null) {
					saleItemRs.close();
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
		return sales;
	}

}
