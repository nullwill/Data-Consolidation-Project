package com.yrl;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * Author(s): Will Aldag & Oliver Triana
 * 
 * This is the main driver that is used to parse the CSV files and
 * maps them into their corresponding objects. The file contains methods
 * that are used to convert the objects into a formatted XML and JSON output.
 */

public class DataLoader {
		
	/**
	 * This method loads specifically the Person CSV formatted data
	 * and returns a list of "Person"s.
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
		} catch (FileNotFoundException e){
			throw new RuntimeException(e);
		}
		
		return people;
	}
	/**
	 * This method loads specifically the Store CSV formatted data
	 * and returns a list of "Store"s.
	 * 
	 * @param filename
	 * @return
	 */
	public static List<Store> loadStoreData(String filename) {
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
					Store st = new Store(tokens[0], tokens[1], tokens[2],
							tokens[3], tokens[4], Integer.parseInt(tokens[5]));
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
	 * This method loads specifically the Item CSV formatted data
	 * and returns a list of "Item"s.
	 * 
	 * @param filename
	 * @return
	 */
	
	public static List<Items> loadItemsData(String filename) {
		List<Items> items = new ArrayList<>();
		
		File f = new File(filename);
		Scanner s;
		try {
			s = new Scanner(f);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length == 4) {
					Items i = new Items(tokens[0], tokens[1].charAt(0), tokens[2], Double.parseDouble(tokens[3]));
					items.add(i);
				}
			} 
			s.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		return items;
	}
}
