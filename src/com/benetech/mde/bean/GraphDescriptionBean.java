package com.benetech.mde.bean;

import java.util.HashMap;

public class GraphDescriptionBean {
	private String equation;
	private String description;
	HashMap<String, String> params;

	public GraphDescriptionBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GraphDescriptionBean(String equation) {
		super();
		this.equation = equation;
	}

	public GraphDescriptionBean(String equation, String description) {
		super();
		this.equation = equation;
		this.description = description;
	}

	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}
	
	public String toString(){
		String beanval;
		
		beanval = new String("GraphDescriptionBean: \nequation: "+equation+
				"\ndescription: "+description);
		if (params != null){
			beanval.concat("\nparams are not null");
		}
		return beanval;
	}

}
