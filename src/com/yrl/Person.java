package com.yrl;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class contains the methods and attributes pertaining to
 * a "Person". 
 */

public class Person implements Comparable<Person>{
	private String uuid;
	private String firstName;
	private String lastName;
	@JacksonXmlElementWrapper(localName="emails")
	@JacksonXmlProperty(localName="email")
	private List<String> emails;
	private Address address;
	
	public Person() {
		super();
	}
	
	public Person(String uuid, String firstName, String lastName, Address address, List<String> emails) {
		this.address = address;
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emails = emails;
	}
	
	public List<String> getEmails() {
		return emails;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public String getName() {
		return lastName + ", " + firstName;
	}
	
	public String getFormattedInfo() {
		return this.getName() + " (" + this.getUuid() + ")\n" + 
				"\t" + this.getEmails() + "\n" +
				"\t" + this.getAddress().getStreet() + "\n" +
				"\t" + this.getAddress().getCityStateZip() + "\n";
	}
	
	static Comparator<Person> cmpByUuid = new Comparator<Person>() {
		public int compare(Person a, Person b) {
			return a.getUuid().compareTo(b.getUuid());
		}
	};

	@Override
	public int compareTo(Person o) {
		return this.getUuid().compareTo(o.getUuid());
	}
	
}
