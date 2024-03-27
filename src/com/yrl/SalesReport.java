package com.yrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SalesReport {

	public static void printSummaryReport(String filename, List<Sale> sales, List<Person> people,
			HashMap<String, Item> items, HashMap<String, List<Item>> soldItems) {
		File f = new File(filename);
		Integer allItems = 0;
		Double allTotals = 0.0;
		Double allTaxes = 0.0;
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println("+----------------------------------------------------------------------------------------+\n"
					+ "| Summary Report - By Total                                                              |\n"
					+ "+----------------------------------------------------------------------------------------+");
			pw.println("Invoice #  Store      Customer             Num Items          Tax            Total");

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
						System.out.println(i.getName() + ": " + i.getNetTotal());
					}
	
				}
				
				pw.printf("%-11s%-11s%-21s%-19d$%-15.2f$%.2f\n", s.getSaleCode(), s.getStoreCode(),
						people.get(index).getName(), numItems, totalTaxes, totalPrice);
				allTotals += totalPrice;
				allItems += numItems;
				allTaxes += totalTaxes;
				
				
			}
			
			pw.println("+----------------------------------------------------------------------------------------+");
			pw.printf("%44d%-18c$%-15.2f$%.2f\n\n", allItems, ' ', allTaxes, allTotals);

			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void printStoresReport(String filename, List<Store> stores, HashMap<String, List<Item>> saleItems, List<Person> people, List<Sale> sales) {
		File f = new File(filename);
		Double allTotal = 0.0;
		Integer allItems = 0;
		try {
			FileWriter fw = new FileWriter(filename, true);
			PrintWriter pw = new PrintWriter(fw);
			Collections.sort(stores);
			pw.println("+----------------------------------------------------------------+\n"
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
					System.out.println(sale.getCustomerUuid());
					
				}
				pw.printf("%-11s%-31s%-11d$%10.2f\n", s.getStoreCode(), s.getManager().getName(), s.getSales().size(), total);
				allTotal += total;
				allItems += s.getSales().size();
				
			}
			pw.println("+----------------------------------------------------------------+");
			pw.printf("%43d\t\t\t $%10.2f\n\n", allItems, allTotal);
			
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printIndividualSalesData(String filename, List<Sale> sales, HashMap<String, Person> people, HashMap<String, List<Item>> saleItems) {
		File f = new File(filename);
		try {
			FileWriter fw = new FileWriter(filename, true);
			PrintWriter pw = new PrintWriter(fw);
			for (Sale s : sales) {
				Double totalTaxes = 0.0;
				Double totalPreTaxes = 0.0;
				pw.println("Sale     #" + s.getSaleCode());
				pw.println("Store    #" + s.getStoreCode());
				pw.println("Date      " + s.getDate());
				
				Person customer = people.get(s.getCustomerUuid());
				pw.println("Customer:");
				pw.println(customer.getFormattedInfo());
				
				Person salesPerson = people.get(s.getSalesPersonUuid());
				pw.println("Sales Person:");
				pw.println(salesPerson.getFormattedInfo());
				List<Item> items = saleItems.get(s.getSaleCode());
				Integer numItems = items != null ? items.size() : 0;
				
				pw.println("Items (" + numItems + ")\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tTax       Total");
				pw.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-                          -=-=-=-=-=- -=-=-=-=-=-");
				if (numItems != 0) {
					for (Item i : items) {
						pw.print(i.getName() + " (" + i.getItemCode() + ")");
						if (i instanceof Leased) {
							Leased l = (Leased) i;
							pw.print(" - Lease for " + l.getLeaseLength() + " months\n");
						} else if (i instanceof Service) {
							Service se = (Service) i;
							pw.print(" - Served by " + people.get(se.getEmployeeUuid()).getName() + "\n");
							pw.printf("  	%.2f hours @ $%.2f/hour\n", se.getHoursBilled(), se.getCostPerHour());
						} else if (i instanceof DataPlan) {
							DataPlan d = (DataPlan) i;
							pw.print(" - Data\n");
							pw.printf("  	%.2f GB @ $%.2f/GB\n", d.getGbPurchased(), d.getCostPerGB());
						} else if (i instanceof VoicePlan) {
							VoicePlan v = (VoicePlan) i;
							pw.printf(" - Voice %s\n", v.getPhoneNumber());
							pw.printf("  	%.0f days @ $%.2f / 30 days\n", v.getDays(), v.getPeriodCost());
						} else if (i instanceof Purchased) {
							pw.print("\n");
						}
						
						pw.printf("%61s$%10.2f $%10.2f\n", " ", i.getTaxes(), i.getPreTaxTotal());
						totalTaxes += i.getTaxes();
						totalPreTaxes += i.getPreTaxTotal();
					}
				}
				pw.println("                                                             -=-=-=-=-=- -=-=-=-=-=-");
				pw.print("                                                   Subtotals $");
				pw.printf("%10.2f $%10.2f\n", totalTaxes, totalPreTaxes);
				pw.printf("                                                 Grand Total             $%10.2f\n", totalTaxes + totalPreTaxes);
			}
			
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		String personsFile = "data/Persons.csv";
		String storesFile = "data/Stores.csv";
		String itemFile = "data/Items.csv";
		String salesFile = "data/Sales.csv";
		String soldItemsFile = "data/SaleItems.csv";

		List<Person> people = DataLoader.loadPersonData(personsFile);
		@SuppressWarnings("unused")
		List<Store> stores = DataLoader.loadStoreData(storesFile, people);
		HashMap<String, Item> items = DataLoader.loadItemData(itemFile);
		List<Sale> sales = DataLoader.loadSalesData(salesFile);
		@SuppressWarnings("unused")
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
		
		printSummaryReport("output.txt", sales, people, items, soldItems);
		printStoresReport("output.txt", stores, soldItems, people, sales);
		printIndividualSalesData("output.txt", sales, peopleMap, soldItems);

	}
}
