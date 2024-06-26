package com.yrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the file that contains the methods to collect, organize, and output
 * the sales report data.
 */

public class SalesReport {

	/**
	 * This method prints the summary report to the system. It takes the sales,
	 * people, items, and soldItems maps as input, formats them according to the
	 * invoice #, Store Code, customer, number of items, tax and total price.
	 * 
	 * @param filename
	 * @param sales
	 * @param people
	 * @param items
	 * @param soldItems
	 */
	public static void printSummaryReport(String filename, HashMap<String, Sale> sales, HashMap<String, Person> people,
			HashMap<String, Item> items) {
		Integer allItems = 0;
		Double allTotals = 0.0;
		Double allTaxes = 0.0;
		System.out
				.println("+----------------------------------------------------------------------------------------+\n"
						+ "| Summary Report - By Total                                                              |\n"
						+ "+----------------------------------------------------------------------------------------+");
		System.out.println("Invoice #  Store      Customer             Num Items          Tax            Total");

		List<Sale> salesList = new ArrayList<>(sales.values());

		Collections.sort(salesList, new Comparator<Sale>() {
			@Override
			public int compare(Sale s1, Sale s2) {
				return s2.getSaleGrandTotal().compareTo(s1.getSaleGrandTotal());
			}
		});

		for (Sale s : salesList) {
			Double totalTaxes = 0.0;
			Double totalPrice = 0.0;
			Integer numItems = 0;
			numItems = s.getItems().size();
			totalTaxes = s.getSaleTaxTotal();
			totalPrice = s.getSaleGrandTotal();

			System.out.printf("%-11s%-11s%-21s%-19d$%-15.2f$%.2f\n", s.getSaleCode(), s.getStore().getStoreCode(),
					s.getCustomer().getName(), numItems, totalTaxes, totalPrice);
			allTotals += totalPrice;
			allItems += numItems;
			allTaxes += totalTaxes;

		}

		System.out
				.println("+----------------------------------------------------------------------------------------+");
		System.out.printf("%44d%-18c$%-15.2f$%.2f\n\n", allItems, ' ', allTaxes, allTotals);

	}

	public static void printStoresReport(String filename, HashMap<String, Store> stores, HashMap<String, Person> people,
			HashMap<String, Sale> sales) {
		Double allTotal = 0.0;
		Integer allItems = 0;
		System.out.println("+----------------------------------------------------------------+\n"
				+ "| Store Sales Summary Report                                     |\n"
				+ "+----------------------------------------------------------------+\n"
				+ "Store      Manager                        # Sales    Grand Total  ");

		List<Store> sortedStores = new ArrayList<>(stores.values());

		Collections.sort(sortedStores, new Comparator<Store>() {
			@Override
			public int compare(Store store1, Store store2) {
				int comp = store1.getManager().getLastName().compareTo(store2.getManager().getLastName());
				if (comp == 0) {
					return store2.getNetTotalOfStore().compareTo(store1.getNetTotalOfStore());
				} else {
					return comp;
				}
			}
		});

		for (Store s : sortedStores) {
			Double total = 0.0;
			List<Sale> storeSales = s.getSales();

			Collections.sort(storeSales, new Comparator<Sale>() {
				@Override
				public int compare(Sale s1, Sale s2) {
					return s2.getSaleGrandTotal().compareTo(s1.getSaleGrandTotal());
				}
			});

			for (Sale sale : storeSales) {
				List<Item> items = sale.getItems();
				if (items != null) {
					for (Item i : items) {
						Item item = (Item) i;
						total += item.getNetTotal();
					}
				}

			}
			System.out.printf("%-11s%-31s%-11d$%10.2f\n", s.getStoreCode(), s.getManager().getName(),
					s.getSales().size(), total);
			allTotal += total;
			allItems += s.getSales().size();

		}
		System.out.println("+----------------------------------------------------------------+");
		System.out.printf("%43d\t     $%10.2f\n\n", allItems, allTotal);
	}

	public static void printIndividualSalesData(String filename, HashMap<String, Sale> sales,
			HashMap<String, Person> people) {
		for (Sale s : sales.values()) {
			Double totalTaxes = 0.0;
			Double totalPreTaxes = 0.0;
			System.out.println("Sale     #" + s.getSaleCode());
			System.out.println("Store    #" + s.getStore().getStoreCode());
			System.out.println("Date      " + s.getDate());

			Person customer = s.getCustomer();
			System.out.println("Customer:");
			System.out.println(customer.getFormattedInfo());

			Person salesPerson = s.getSalesPerson();
			System.out.println("Sales Person:");
			System.out.println(salesPerson.getFormattedInfo());
			List<Item> items = sales.get(s.getSaleCode()).getItems();
			Integer numItems = items != null ? items.size() : 0;

			System.out.println("Items (" + numItems + ")\t\t\t\t\t\t\t     Tax       Total");
			System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-                          -=-=-=-=-=- -=-=-=-=-=-");
			if (numItems != 0) {
				for (Item i : items) {
					System.out.print(i.getName() + " (" + i.getItemCode() + ")");
					if (i instanceof Leased) {
						Leased l = (Leased) i;
						System.out.print(" - Lease for " + l.getLeaseLength() + " months\n");
					} else if (i instanceof Service) {
						Service se = (Service) i;
						System.out.print(" - Served by " + se.getEmployee().getName() + "\n");
						System.out.printf("  	%.2f hours @ $%.2f/hour\n", se.getHoursBilled(), se.getCostPerHour());
					} else if (i instanceof DataPlan) {
						DataPlan d = (DataPlan) i;
						System.out.print(" - Data\n");
						System.out.printf("  	%.2f GB @ $%.2f/GB\n", d.getGbPurchased(), d.getCostPerGB());
					} else if (i instanceof VoicePlan) {
						VoicePlan v = (VoicePlan) i;
						System.out.printf(" - Voice %s\n", v.getPhoneNumber());
						System.out.printf("  	%.0f days @ $%.2f / 30 days\n", v.getDays(), v.getPeriodCost());
					} else if (i instanceof Purchased) {
						System.out.print("\n");
					}

					System.out.printf("%61s$%10.2f $%10.2f\n", " ", i.getTaxes(), i.getPreTaxTotal());
					totalTaxes += i.getTaxes();
					totalPreTaxes += i.getPreTaxTotal();
				}
			}
			System.out.println("                                                             -=-=-=-=-=- -=-=-=-=-=-");
			System.out.print("                                                   Subtotals $");
			System.out.printf("%10.2f $%10.2f\n", totalTaxes, totalPreTaxes);
			System.out.printf("                                                 Grand Total             $%10.2f\n",
					totalTaxes + totalPreTaxes);
		}
	}

	public static void printSalesByCustomer(HashMap<String, Sale> sales) {
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println("| Sales by Customer                                                       |");
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println("Sale       Store      Customer             Salesperson          Total     ");

		SortedArrayList<Sale> sortedSales = new SortedArrayList<>(
				Comparator.comparing(s -> s.getCustomer().getLastName()));
		for (Sale s : sales.values()) {
			sortedSales.add(s);
		}
		for (Sale sale : sortedSales) {
			String saleCode = sale.getSaleCode();
			String storeCode = sale.getStore().getStoreCode();
			String customerName = sale.getCustomer().getName();
			String salespersonName = sale.getSalesPerson().getName();
			double total = sale.getSaleGrandTotal();
			System.out.printf("%-10s%-11s%-21s%-20s$%10.2f%n", saleCode, storeCode, customerName, salespersonName,
					total);
		}
		System.out.println("+-------------------------------------------------------------------------+");
	}

	public static void printSalesByTotal(HashMap<String, Sale> sales) {
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println("| Sales by Total                                                          |");
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println("Sale       Store      Customer             Salesperson          Total     ");

		SortedArrayList<Sale> sortedSales = new SortedArrayList<>(
				Comparator.comparing(Sale::getSaleGrandTotal).reversed());
		for (Sale s : sales.values()) {
			sortedSales.add(s);
		}

		for (Sale sale : sortedSales) {
			String saleCode = sale.getSaleCode();
			String storeCode = sale.getStore().getStoreCode();
			String customerName = sale.getCustomer().getName();
			String salespersonName = sale.getSalesPerson().getName();
			double total = sale.getSaleGrandTotal();
			System.out.printf("%-10s%-11s%-21s%-20s$%10.2f%n", saleCode, storeCode, customerName, salespersonName,
					total);
		}
		System.out.println("+-------------------------------------------------------------------------+");
	}

	public static void printSalesByStore(HashMap<String, Sale> sales) {
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println("| Sales by Store                                                          |");
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println("Sale       Store      Customer             Salesperson          Total     ");

		SortedArrayList<Sale> sortedSales = new SortedArrayList<>(
				Comparator.comparing(s -> s.getStore().getStoreCode()));
		for (Sale s : sales.values()) {
			sortedSales.add(s);
		}

		for (Sale sale : sortedSales) {
			String saleCode = sale.getSaleCode();
			String storeCode = sale.getStore().getStoreCode();
			String customerName = sale.getCustomer().getName();
			String salespersonName = sale.getSalesPerson().getName();
			double total = sale.getSaleGrandTotal();
			System.out.printf("%-10s%-11s%-21s%-20s$%10.2f%n", saleCode, storeCode, customerName, salespersonName,
					total);
		}
		System.out.println("+-------------------------------------------------------------------------+");
	}

	public static void main(String args[]) {

		HashMap<String, Person> people = DataLoader.getAllPeople();
		HashMap<String, Store> stores = DataLoader.getAllStores();
		HashMap<String, Item> items = DataLoader.getAllItems();
		HashMap<String, Sale> sales = DataLoader.getAllSales();

		printSalesByCustomer(sales);
		printSalesByTotal(sales);
		printSalesByStore(sales);

//		printSummaryReport("output.txt", sales, people, items);
//		printStoresReport("output.txt", stores, people, sales);
//		printIndividualSalesData("output.txt", sales, people);

	}
}
