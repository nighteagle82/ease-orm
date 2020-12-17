package cn.ne.pojo;

import java.sql.*;
import java.util.*;

public class Emp {

	private String empname;
	private java.sql.Date birthday;
	private Double bonus;
	private Long deptid;
	private Long id;
	private Double salary;
	private Integer age;


	public String getEmpname(){
		return empname;
	}
	public java.sql.Date getBirthday(){
		return birthday;
	}
	public Double getBonus(){
		return bonus;
	}
	public Long getDeptid(){
		return deptid;
	}
	public Long getId(){
		return id;
	}
	public Double getSalary(){
		return salary;
	}
	public Integer getAge(){
		return age;
	}
	public void setEmpname(String empname){
		this.empname=empname;
	}
	public void setBirthday(java.sql.Date birthday){
		this.birthday=birthday;
	}
	public void setBonus(Double bonus){
		this.bonus=bonus;
	}
	public void setDeptid(Long deptid){
		this.deptid=deptid;
	}
	public void setId(Long id){
		this.id=id;
	}
	public void setSalary(Double salary){
		this.salary=salary;
	}
	public void setAge(Integer age){
		this.age=age;
	}
}
