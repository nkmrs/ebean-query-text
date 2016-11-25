package models;

import javax.persistence.Entity;

@Entity
public class Example1 {

	public Integer id;

	public String name;

	public Integer age;

	@Override
	public String toString() {
		return String.join(",", String.valueOf(id), name, String.valueOf(age));
	}
}
