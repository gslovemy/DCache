package com.dance.cache.demo;

import java.io.Serializable;

public class Stu implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1795909556508872340L;
	private String name;
	private int age;
	public Stu(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	@Override
	public String toString() {
		return "Stu [name=" + name + ", age=" + age + "]";
	}
	
}