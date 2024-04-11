package com.yrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
	public static void persistJson(HashMap<?, ?> map, String root, String outputFileName) {
		File f = new File(outputFileName);
		PrintWriter pw;
		try {
			pw = new PrintWriter(f);
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			
			try {
				String json = mapper.writeValueAsString(map.values());
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
	public static void persistXml(HashMap<?, ?> map, String root, String outputFileName) {
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
			
			try {
				mapper.writeValue(f, map.values());
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
	
	public static void main(String args[]) throws JsonProcessingException, FileNotFoundException {
		String personsFile = "data/Persons.csv";
		String storesFile  = "data/Stores.csv";
		String itemFile   = "data/Items.csv";
		
		String personsJson = "output/Persons.json";
		String storesJson  = "output/Stores.json";
		String itemJson   = "output/Items.json";
		
		String personsXml = "output/Persons.xml";
		String storesXml  = "output/Stores.xml";
		String itemXml   = "output/Items.xml";
		
		HashMap<String, Person> people = DataLoader.loadPersonData(personsFile);
		HashMap<String, Store> stores  = DataLoader.loadStoreData(storesFile, people);
		HashMap<String, Item> items   = DataLoader.loadItemData(itemFile);
		
		HashMap<String, Item> itemsByType = new HashMap<String, Item>();
		
		for (Item i : items.values()) {
			if (i instanceof Product) {
				itemsByType.put("product", i);
			} else if (i instanceof Service) {
				itemsByType.put("service", i);
			} else if (i instanceof VoicePlan) {
				itemsByType.put("voicePlan", i);
			} else if (i instanceof DataPlan) {
				itemsByType.put("dataPlan", i);
			}
		}
		
		File f = new File(itemXml);
		PrintWriter pw = new PrintWriter(f);
		ObjectMapper mapper = new ObjectMapper();
		XmlMapper xmlMapper = new XmlMapper();
		
		String xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(itemsByType);
		pw.println(xmlString);
		
//		pw.println(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items));
		pw.close();
		
		DataLoader.getPersonData(1);
//		persistJson(people, "persons", personsJson);
//		persistJson(stores, "stores", storesJson);
//		persistJson(items, "items", itemJson);
//		
//		persistXml(people, "person", personsXml);
//		persistXml(stores, "store", storesXml);
//		persistXml(items, "item", itemXml);
//		
		
	}
}
