package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class UserAge extends Model {

	@Id
	public Integer id;

	public Integer age;
}
