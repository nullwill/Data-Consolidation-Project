package com.yrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

/**
 * Author(s): Will Aldag & Oliver Triana
 * 
 * This is the main driver that is used to parse the CSV files and
 * maps them into their corresponding objects. The file contains methods
 * that are used to convert the objects into a formatted XML and JSON output.
 */

public class DataConverter {
	
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
			XmlMapper mapper = XmlMapper
					.builder()
					.defaultUseWrapper(true)
					.build();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);			
			
			Class<?> itemClass = Item.class;
			JacksonXmlRootElement annotation = itemClass.getAnnotation(JacksonXmlRootElement.class);
			System.out.println("JacksonXmlRootElement annotation on Item class: " + annotation);
			try {
				mapper.writeValue(f, list);
			} catch (StreamWriteException e) {
				e.printStackTrace();
			} catch (DatabindException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
		String itemFile   = "data/Items.csv";
		
		String personsJson = "output/Persons.json";
		String storesJson  = "output/Stores.json";
		String itemJson   = "output/Items.json";
		
		String personsXml = "output/Persons.xml";
		String storesXml  = "output/Stores.xml";
		String itemXml   = "output/Items.xml";
		
		List<Person> people = DataLoader.loadPersonData(personsFile);
		List<Store> stores  = DataLoader.loadStoreData(storesFile, people);
		HashMap<String, Item> items   = DataLoader.loadItemData(itemFile);
		
		List<Item> itemsList = new ArrayList<Item>(items.values());
		
		persistJson(people, "persons", personsJson);
		persistJson(stores, "stores", storesJson);
		persistJson(itemsList, "items", itemJson);
		
		persistXml(people, "person", personsXml);
		persistXml(stores, "store", storesXml);
		persistXml(itemsList, "item", itemXml);
		
		
	}
}
