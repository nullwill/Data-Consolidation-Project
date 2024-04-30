package com.yrl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author(s): Will Aldag & Oliver Triana Date: 2024-04-26
 * 
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 *
 */
public class SalesData {

	/**
	 * Helper method to retrieve a personId from a given uuid (identified by
	 * <code>personUuid</code>).
	 * 
	 * @param personUuid
	 * @return
	 */
	public static int getPersonIdFromUuid(String personUuid) {
		Connection conn;
		int personId = -1;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String query = "SELECT Person.personId as personId from Person where personUuid = ?";
			PreparedStatement psQuery = conn.prepareStatement(query);
			psQuery.setString(1, personUuid);
			ResultSet rsQuery = psQuery.executeQuery();

			if (rsQuery.next()) {
				personId = rsQuery.getInt("personId");
			}

			rsQuery.close();
			psQuery.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return personId;
	}

	/**
	 * Helper method to retrieve a storeId from a given store code (identified by
	 * <code>storeCode</code>).
	 * 
	 * @param storeCode
	 * @return
	 */
	public static int getStoreIdFromStoreCode(String storeCode) {
		Connection conn;
		int storeId = -1;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String query = "SELECT Store.storeId as storeId from Store where storeCode = ?;";
			PreparedStatement psQuery = conn.prepareStatement(query);
			psQuery.setString(1, storeCode);
			ResultSet rsQuery = psQuery.executeQuery();

			if (rsQuery.next()) {
				storeId = rsQuery.getInt("storeId");
			}

			rsQuery.close();
			psQuery.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return storeId;
	}

	/**
	 * Helper method to retrieve a saleId from a given sale code (identified by
	 * <code>saleCode</code>).
	 * 
	 * @param saleCode
	 * @return
	 */
	public static int getSaleIdFromSaleCode(String saleCode) {
		Connection conn;
		int saleId = -1;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String query = "SELECT Sale.saleId as saleId from Sale where saleCode = ?;";
			PreparedStatement psQuery = conn.prepareStatement(query);
			psQuery.setString(1, saleCode);
			ResultSet rsQuery = psQuery.executeQuery();

			if (rsQuery.next()) {
				saleId = rsQuery.getInt("saleId");
			}

			rsQuery.close();
			psQuery.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return saleId;
	}

	/**
	 * Helper method to retrieve an itemId from a given itemCode (identified by
	 * <code>itemCode</code>).
	 * 
	 * @param itemCode
	 * @return
	 */
	public static int getItemIdFromItemCode(String itemCode) {
		Connection conn;
		int itemId = -1;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String query = "SELECT Item.itemId as itemId from Item where itemCode = ?;";
			PreparedStatement psQuery = conn.prepareStatement(query);
			psQuery.setString(1, itemCode);
			ResultSet rsQuery = psQuery.executeQuery();

			if (rsQuery.next()) {
				itemId = rsQuery.getInt("itemId");
			}

			rsQuery.close();
			psQuery.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return itemId;
	}

	/**
	 * Removes all records from all tables in the database.
	 */
	public static void clearDatabase() {
		String[] tables = { "SaleItem", "Sale", "Item", "Email", "Store", "Person", "Address" };
		String deleteSQL = "DELETE FROM %s";

		try (Connection connection = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME,
				DatabaseInfo.PASSWORD)) {
			Statement statement = connection.createStatement();

			for (String table : tables) {
				String sql = String.format(deleteSQL, table);
				statement.executeUpdate(sql);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to add a person record to the database with the provided data.
	 *
	 * @param personUuid
	 * @param firstName
	 * @param lastName
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 */
	public static void addPerson(String personUuid, String firstName, String lastName, String street, String city,
			String state, String zip) {
		if (getPersonIdFromUuid(personUuid) != -1) {
			return;
		}

		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
			String insertAddress = "INSERT INTO Address(street, city, state, zipCode) values (?, ?, ?, ?);";
			PreparedStatement psInsertAddress = conn.prepareStatement(insertAddress, Statement.RETURN_GENERATED_KEYS);
			psInsertAddress.setString(1, street);
			psInsertAddress.setString(2, city);
			psInsertAddress.setString(3, state);
			psInsertAddress.setInt(4, Integer.parseInt(zip));

			psInsertAddress.executeUpdate();

			ResultSet keys = psInsertAddress.getGeneratedKeys();
			keys.next();
			int addressId = keys.getInt(1);

			String insertPerson = "INSERT INTO Person(personUuid, firstName, lastName, addressId) VALUES (?, ?, ?, ?);";
			PreparedStatement psInsertPerson = conn.prepareStatement(insertPerson);
			psInsertPerson.setString(1, personUuid);
			psInsertPerson.setString(2, firstName);
			psInsertPerson.setString(3, lastName);
			psInsertPerson.setInt(4, addressId);

			psInsertPerson.executeUpdate();

			psInsertPerson.close();
			psInsertAddress.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personUuid</code>
	 *
	 * @param personUuid
	 * @param email
	 */
	public static void addEmail(String personUuid, String email) {
		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			int personId = getPersonIdFromUuid(personUuid);

			String insertEmail = "INSERT INTO Email(personId, email) values (?, ?);";
			PreparedStatement psInsertEmail = conn.prepareStatement(insertEmail);
			psInsertEmail.setInt(1, personId);
			psInsertEmail.setString(2, email);

			psInsertEmail.executeUpdate();

			psInsertEmail.close();

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Adds a store record to the database managed by the person identified by the
	 * given code.
	 *
	 * @param storeCode
	 * @param managerCode
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 */
	public static void addStore(String storeCode, String managerCode, String street, String city, String state,
			String zip) {
		if (getStoreIdFromStoreCode(storeCode) != -1) {
			return;
		}

		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
			String insertAddress = "INSERT INTO Address(street, city, state, zipCode) values (?, ?, ?, ?);";
			PreparedStatement psInsertAddress = conn.prepareStatement(insertAddress, Statement.RETURN_GENERATED_KEYS);
			psInsertAddress.setString(1, street);
			psInsertAddress.setString(2, city);
			psInsertAddress.setString(3, state);
			psInsertAddress.setInt(4, Integer.parseInt(zip));

			psInsertAddress.executeUpdate();

			ResultSet keys = psInsertAddress.getGeneratedKeys();
			keys.next();
			int addressId = keys.getInt(1);

			int managerId = getPersonIdFromUuid(managerCode);

			String insertStore = "INSERT INTO Store(storeCode, managerId, addressId) VALUES (?, ?, ?);";
			PreparedStatement psInsertStore = conn.prepareStatement(insertStore);
			psInsertStore.setString(1, storeCode);
			psInsertStore.setInt(2, managerId);
			psInsertStore.setInt(3, addressId);

			psInsertStore.executeUpdate();

			psInsertStore.close();
			psInsertAddress.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Adds an item record to the database of the given <code>type</code> with the
	 * given <code>code</code>, <code>name</code> and <code>basePrice</code>.
	 *
	 * Valid values for the <code>type</code> will be <code>"Product"</code>,
	 * <code>"Service"</code>, <code>"Data"</code>, or <code>"Voice"</code>.
	 *
	 * @param itemCode
	 * @param name
	 * @param type
	 * @param basePrice
	 */
	public static void addItem(String code, String name, String type, double basePrice) {
		if (getItemIdFromItemCode(code) != -1) {
			return;
		}

		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);
			String insertItem = "INSERT INTO Item(itemCode, itemType, itemName, baseCost) values (?, ?, ?, ?);";
			PreparedStatement psInsertItem = conn.prepareStatement(insertItem);
			psInsertItem.setString(1, code);
			psInsertItem.setString(2, String.valueOf(type.charAt(0)));
			psInsertItem.setString(3, name);
			psInsertItem.setDouble(4, basePrice);

			psInsertItem.executeUpdate();

			psInsertItem.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Adds an Sale record to the database with the given data.
	 *
	 * @param saleCode
	 * @param storeCode
	 * @param customerPersonUuid
	 * @param salesPersonUuid
	 * @param saleDate
	 */
	public static void addSale(String saleCode, String storeCode, String customerPersonUuid, String salesPersonUuid,
			String saleDate) {
		if (getSaleIdFromSaleCode(saleCode) != -1) {
			return;
		}

		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String insertSale = "INSERT INTO Sale(saleCode, storeId, customerId, salesPersonId, saleDate) values (?, ?, ?, ?, ?);";
			PreparedStatement psInsertSale = conn.prepareStatement(insertSale);

			int storeId = getStoreIdFromStoreCode(storeCode);
			int customerId = getPersonIdFromUuid(customerPersonUuid);
			int salesPersonId = getPersonIdFromUuid(salesPersonUuid);

			psInsertSale.setString(1, saleCode);
			psInsertSale.setInt(2, storeId);
			psInsertSale.setInt(3, customerId);
			psInsertSale.setInt(4, salesPersonId);
			psInsertSale.setString(5, saleDate);

			psInsertSale.executeUpdate();

			psInsertSale.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a particular product (identified by <code>itemCode</code>) to a
	 * particular sale (identified by <code>saleCode</code>).
	 *
	 * @param saleCode
	 * @param itemCode
	 */
	public static void addProductToSale(String saleCode, String itemCode) {
		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String insertSaleItem = "INSERT INTO SaleItem(saleId, itemId) values (?, ?);";
			PreparedStatement psInsertSaleItem = conn.prepareStatement(insertSaleItem);

			int saleId = getSaleIdFromSaleCode(saleCode);
			int itemId = getItemIdFromItemCode(itemCode);

			psInsertSaleItem.setInt(1, saleId);
			psInsertSaleItem.setInt(2, itemId);

			psInsertSaleItem.executeUpdate();

			psInsertSaleItem.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Adds a particular leased (identified by <code>itemCode</code>) to a
	 * particular sale (identified by <code>saleCode</code>) with the start/end date
	 * specified.
	 *
	 * @param saleCode
	 * @param startDate
	 * @param endDate
	 */
	public static void addLeaseToSale(String saleCode, String itemCode, String startDate, String endDate) {
		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String insertSaleItem = "INSERT INTO SaleItem(saleId, itemId, startDate, endDate) values (?, ?, ?, ?);";
			PreparedStatement psInsertSaleItem = conn.prepareStatement(insertSaleItem);

			int saleId = getSaleIdFromSaleCode(saleCode);
			int itemId = getItemIdFromItemCode(itemCode);

			psInsertSaleItem.setInt(1, saleId);
			psInsertSaleItem.setInt(2, itemId);
			psInsertSaleItem.setString(3, startDate);
			psInsertSaleItem.setString(4, endDate);

			psInsertSaleItem.executeUpdate();

			psInsertSaleItem.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a particular service (identified by <code>itemCode</code>) to a
	 * particular sale (identified by <code>saleCode</code>) with the specified
	 * number of hours. The service is done by the employee with the specified
	 * <code>servicePersonUuid</code>
	 *
	 * @param saleCode
	 * @param itemCode
	 * @param billedHours
	 * @param servicePersonUuid
	 */
	public static void addServiceToSale(String saleCode, String itemCode, double billedHours,
			String servicePersonUuid) {
		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String insertSaleItem = "INSERT INTO SaleItem(saleId, itemId, numHours, employeeId) values (?, ?, ?, ?);";
			PreparedStatement psInsertSaleItem = conn.prepareStatement(insertSaleItem);

			int saleId = getSaleIdFromSaleCode(saleCode);
			int itemId = getItemIdFromItemCode(itemCode);
			int servicePersonId = getPersonIdFromUuid(servicePersonUuid);

			psInsertSaleItem.setInt(1, saleId);
			psInsertSaleItem.setInt(2, itemId);
			psInsertSaleItem.setDouble(3, billedHours);
			psInsertSaleItem.setInt(4, servicePersonId);

			psInsertSaleItem.executeUpdate();

			psInsertSaleItem.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a particular data plan (identified by <code>itemCode</code>) to a
	 * particular sale (identified by <code>saleCode</code>) with the specified
	 * number of gigabytes.
	 *
	 * @param saleCode
	 * @param itemCode
	 * @param gbs
	 */
	public static void addDataPlanToSale(String saleCode, String itemCode, double gbs) {
		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String insertSaleItem = "INSERT INTO SaleItem(saleId, itemId, numGB) values (?, ?, ?);";
			PreparedStatement psInsertSaleItem = conn.prepareStatement(insertSaleItem);

			int saleId = getSaleIdFromSaleCode(saleCode);
			int itemId = getItemIdFromItemCode(itemCode);

			psInsertSaleItem.setInt(1, saleId);
			psInsertSaleItem.setInt(2, itemId);
			psInsertSaleItem.setDouble(3, gbs);

			psInsertSaleItem.executeUpdate();

			psInsertSaleItem.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Adds a particular voice plan (identified by <code>itemCode</code>) to a
	 * particular sale (identified by <code>saleCode</code>) with the specified
	 * <code>phoneNumber</code> for the given number of <code>days</code>.
	 *
	 * @param saleCode
	 * @param itemCode
	 * @param phoneNumber
	 * @param days
	 */
	public static void addVoicePlanToSale(String saleCode, String itemCode, String phoneNumber, int days) {
		Connection conn;
		try {
			conn = DriverManager.getConnection(DatabaseInfo.URL, DatabaseInfo.USERNAME, DatabaseInfo.PASSWORD);

			String insertSaleItem = "INSERT INTO SaleItem(saleId, itemId, phoneNumber, numDays) values (?, ?, ?, ?);";
			PreparedStatement psInsertSaleItem = conn.prepareStatement(insertSaleItem);

			int saleId = getSaleIdFromSaleCode(saleCode);
			int itemId = getItemIdFromItemCode(itemCode);

			psInsertSaleItem.setInt(1, saleId);
			psInsertSaleItem.setInt(2, itemId);
			psInsertSaleItem.setString(3, phoneNumber);
			psInsertSaleItem.setInt(4, days);

			psInsertSaleItem.executeUpdate();

			psInsertSaleItem.close();
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

}
