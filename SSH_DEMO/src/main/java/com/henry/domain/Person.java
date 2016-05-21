package com.henry.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
 
 
@Entity
public class Person {
	/*
	 * @Id marks the "id" field as the primary key for this class
     * 
     * @GeneratedValue will cause the id field to be generated by 
     * the provider (Hibernate). Classes
	 */
    @Id
    @GeneratedValue(generator="increment")
    private Integer id;
    private String lastName;
    private String firstName;
 
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
 
    public Integer getId() {
        return id;
    }
 
    public void setId(Integer id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "Person [id=" + id + ", lastName=" + lastName + ", firstName="
				+ firstName + "]";
	}
}