package com.yrl;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person extends Address{
	private String uuid;
	private String firstName;
	private String lastName;
	@JacksonXmlElementWrapper(localName="emails")
	@JacksonXmlProperty(localName="email")
	private List<String> emails;
	
	public Person() {
		super();
	}
	
	public Person(String uuid, String firstName, String lastName, String street, String state, String city, Integer zip, List<String> emails) {
		super(street, state, city, zip);
		this.uuid = uuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emails = emails;
	}
	
	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
