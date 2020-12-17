package cn.ne.pojo;

import java.sql.*;
import java.util.*;

public class Dept {

	private String address;
	private Long id;
	private String dname;


	public String getAddress(){
		return address;
	}
	public Long getId(){
		return id;
	}
	public String getDname(){
		return dname;
	}
	public void setAddress(String address){
		this.address=address;
	}
	public void setId(Long id){
		this.id=id;
	}
	public void setDname(String dname){
		this.dname=dname;
	}
}
