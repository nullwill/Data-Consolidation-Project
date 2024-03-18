package com.yrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

public class SalesReport {
	
	public static void printSummaryReport(String filename, List<Sale> sales, List<Person> people, List<?> items) {
		File f = new File(filename);
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println("+----------------------------------------------------------------------------------------+\n"
					+ "| Summary Report - By Total                                                              |\n"
					+ "+----------------------------------------------------------------------------------------+");
			pw.println("Invoice #  Store      Customer                       Num Items          Tax       Total");
			
			for (Sale s : sales) {
				Collections.sort(people, Person.cmpByUuid);
				Person key = new Person(s.getCustomerUuid(), "", "", null, null);
				int index = Collections.binarySearch(people, key, Person.cmpByUuid);
				
				
//				int numItems = 0;
				pw.printf("%-11s%-11s%-10s\n", s.getSaleCode(), s.getStoreCode(), people.get(index).getName());
				
			}
			
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
	}

	public static void printStoresReport(String filename) {
		// TODO: print stores reports.
	}
	
	public static void printIndividualSalesData(String filename) {
		//TODO: print individual sales reports
	}
	
	public static void main(String args[]) {
		String personsFile = "data/Persons.csv";
		String storesFile  = "data/Stores.csv";
		String itemFile   = "data/Items.csv";
		String salesFile  = "data/Sales.csv";
		String soldItemsFile = "data/SaleItems.csv";
		
		List<Person> people = DataLoader.loadPersonData(personsFile);
		@SuppressWarnings("unused")
		List<Store> stores  = DataLoader.loadStoreData(storesFile, people);
		List<List<?>> items   = DataLoader.loadItemData(itemFile);
		List<Sale> sales = DataLoader.loadSalesData(salesFile);
		@SuppressWarnings("unused")
		List<?> soldItems = DataLoader.loadSaleItemsData(soldItemsFile, items, sales);
		
		printSummaryReport("output.txt", sales, people, items);
	}
}
