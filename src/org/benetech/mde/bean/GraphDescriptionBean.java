package org.benetech.mde.bean;

import gov.nasa.ial.mde.properties.MdeSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class GraphDescriptionBean {
	private String equation;
	private String description;
//	private String[] parameters;

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

//	public String[] getParameters(){
//		return parameters;
//	}
//
//	public void setParameters(String[] parameters) {
//		this.parameters = parameters;
//	}

	public String toString(){
		String beanval;
		
		beanval = new String("GraphDescriptionBean: \nequation: "+equation+
				"\ndescription: "+description);
//		if (parameters != null){
//			beanval.concat("\nparams are not null");
//		}
		return beanval;
	}

}
