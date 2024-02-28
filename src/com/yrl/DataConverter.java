package com.yrl;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.*;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
//import javax.xml.bind.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;


/**
 * Author(s): Will Aldag & Oliver Triana
 * 
 * This is the main driver that is used to parse the CSV files and
 * maps them into their corresponding objects. The file contains methods
 * that are used to convert the objects into a formatted XML and JSON output.
 */

public class DataConverter {
		
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
			// Skip over first line
			s.nextLine();
			while (s.hasNextLine()) {
				List<String> email = new ArrayList<>();
				String line = s.nextLine();
				String tokens[] = line.split(",");
				if (tokens.length >= 8) {
					for (int i = 7; i < tokens.length; i++) {
						email.add(tokens[i]);
					}
					Person p = new Person(tokens[0], tokens[1], tokens[2], tokens[3],
							tokens[4], tokens[5], Integer.parseInt(tokens[6]), email);
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
			// Skip over first line
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
			// Skip over first line
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
	
	/**
	 * This method is a general method to persist the object data and output
	 * it into an JSON format. The method accepts a list of unspecified objects,
	 * a root name, and an output filename. The method then outputs the objects,
	 * formatted as JSON under the name provided by the user.
	 * 
	 * @param list
	 * @param head
	 * @param outputFileName
	 */
	public static void persistJson(List<?> list, String root, String outputFileName) {
		File f = new File(outputFileName);
		PrintWriter pw;
		try {
			pw = new PrintWriter(f);
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			Map<String, List<?>> map = new HashMap<>();
			map.put(root, list);
			
			try {
				String json = mapper.writeValueAsString(map);
				pw.println(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is a general method to persist the object data and output
	 * it into an XML format. The method accepts a list of unspecified objects,
	 * a root name, and an output filename. The method then outputs the objects,
	 * formatted as XML under the name provided by the user.
	 * @param list
	 * @param root
	 * @param outputFileName
	 */
	public static void persistXml(List<?> list, String root, String outputFileName) {
		File f = new File(outputFileName);
		PrintWriter pw;
		try {
			pw = new PrintWriter(f);
			XmlMapper mapper = XmlMapper.builder()
					.defaultUseWrapper(false)
					.build();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
			Map<String, List<?>> map = new HashMap<>();
			map.put(root, list);
			
			
			try {
				pw.println(mapper
						.writer()
						.withRootName(root + "s")
						.writeValueAsString(map));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		String personsFile = "data/Persons.csv";
		String storesFile  = "data/Stores.csv";
		String itemsFile   = "data/Items.csv";
		
		String personsJson = "data/Persons.json";
		String storesJson  = "data/Stores.json";
		String itemsJson   = "data/Items.json";
		
		String personsXml = "data/Persons.xml";
		String storesXml  = "data/Stores.xml";
		String itemsXml   = "data/Items.xml";
		
		List<Person> people = loadPersonData(personsFile);
		List<Store> stores  = loadStoreData(storesFile);
		List<Items> items   = loadItemsData(itemsFile);
		
		persistJson(people, "persons", personsJson);
		persistJson(stores, "stores", storesJson);
		persistJson(items, "items", itemsJson);
		
		persistXml(people, "person", personsXml);
		persistXml(stores, "store", storesXml);
		persistXml(items, "item", itemsXml);
	}
}
