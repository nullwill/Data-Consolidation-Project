package com.yrl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This is the file that contains the methods to collect, organize, and output the
 * sales report data. 
 */

public class SalesReport {

	public static void printSummaryReport(String filename, List<Sale> sales, List<Person> people,
			HashMap<String, Item> items, HashMap<String, List<Item>> soldItems) {
		Integer allItems = 0;
		Double allTotals = 0.0;
		Double allTaxes = 0.0;
		System.out
				.println("+----------------------------------------------------------------------------------------+\n"
						+ "| Summary Report - By Total                                                              |\n"
						+ "+----------------------------------------------------------------------------------------+");
		System.out.println("Invoice #  Store      Customer             Num Items          Tax            Total");

		for (Sale s : sales) {
			Collections.sort(people, Person.cmpByUuid);
			Person key = new Person(s.getCustomerUuid(), "", "", null, null);
			int index = Collections.binarySearch(people, key, Person.cmpByUuid);
			List<Item> saleItems = soldItems.get(s.getSaleCode());
			Double totalTaxes = 0.0;
			Double totalPrice = 0.0;
			Integer numItems = 0;
			if (saleItems != null) {
				numItems = soldItems.get(s.getSaleCode()).size();
				for (Item i : saleItems) {
					totalTaxes += i.getTaxes();
					totalPrice += i.getNetTotal();
				}

			}

			System.out.printf("%-11s%-11s%-21s%-19d$%-15.2f$%.2f\n", s.getSaleCode(), s.getStoreCode(),
					people.get(index).getName(), numItems, totalTaxes, totalPrice);
			allTotals += totalPrice;
			allItems += numItems;
			allTaxes += totalTaxes;

		}

		System.out
				.println("+----------------------------------------------------------------------------------------+");
		System.out.printf("%44d%-18c$%-15.2f$%.2f\n\n", allItems, ' ', allTaxes, allTotals);

	}

	public static void printStoresReport(String filename, List<Store> stores, HashMap<String, List<Item>> saleItems,
			List<Person> people, List<Sale> sales) {
		Double allTotal = 0.0;
		Integer allItems = 0;
		Collections.sort(stores);
		System.out.println("+----------------------------------------------------------------+\n"
				+ "| Store Sales Summary Report                                     |\n"
				+ "+----------------------------------------------------------------+\n"
				+ "Store      Manager                        # Sales    Grand Total  ");
		for (Store s : stores) {
			Double total = 0.0;
			List<Sale> storeSales = s.getSales();
			for (Sale sale : storeSales) {
				List<Item> items = saleItems.get(sale.getSaleCode());
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

	public static void printIndividualSalesData(String filename, List<Sale> sales, HashMap<String, Person> people,
			HashMap<String, List<Item>> saleItems) {
		for (Sale s : sales) {
			Double totalTaxes = 0.0;
			Double totalPreTaxes = 0.0;
			System.out.println("Sale     #" + s.getSaleCode());
			System.out.println("Store    #" + s.getStoreCode());
			System.out.println("Date      " + s.getDate());

			Person customer = people.get(s.getCustomerUuid());
			System.out.println("Customer:");
			System.out.println(customer.getFormattedInfo());

			Person salesPerson = people.get(s.getSalesPersonUuid());
			System.out.println("Sales Person:");
			System.out.println(salesPerson.getFormattedInfo());
			List<Item> items = saleItems.get(s.getSaleCode());
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
						System.out.print(" - Served by " + people.get(se.getEmployeeUuid()).getName() + "\n");
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

	public static void main(String args[]) {
		String personsFile = "data/Persons.csv";
		String storesFile = "data/Stores.csv";
		String itemFile = "data/Items.csv";
		String salesFile = "data/Sales.csv";
		String soldItemsFile = "data/SaleItems.csv";

		List<Person> people = DataLoader.loadPersonData(personsFile);
		List<Store> stores = DataLoader.loadStoreData(storesFile, people);
		HashMap<String, Item> items = DataLoader.loadItemData(itemFile);
		List<Sale> sales = DataLoader.loadSalesData(salesFile);
		HashMap<String, List<Item>> soldItems = DataLoader.loadSaleItemsData(soldItemsFile, items, sales);

		for (Sale sale : sales) {
			for (Store store : stores) {
				if (sale.getStoreCode().compareTo(store.getStoreCode()) == 0) {
					store.getSales().add(sale);
				}
			}
		}

		HashMap<String, Person> peopleMap = new HashMap<String, Person>();
		for (Person p : people) {
			peopleMap.put(p.getUuid(), p);
		}

		System.out.println("Phase 1 output...");
		printSummaryReport("output.txt", sales, people, items, soldItems);
		printStoresReport("output.txt", stores, soldItems, people, sales);
		printIndividualSalesData("output.txt", sales, peopleMap, soldItems);

	}
}
