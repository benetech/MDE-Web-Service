package org.benetech.mde.bean;


public class EquationBean {
	private String equation;
//	private String description;
//	private String[] parameters;

	public EquationBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EquationBean(String equation) {
		super();
		this.equation = equation;
	}

	public EquationBean(String equation, String description) {
		super();
		this.equation = equation;
//		this.description = description;
	}

	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}

//	public String[] getParameters(){
//		return parameters;
//	}
//
//	public void setParameters(String[] parameters) {
//		this.parameters = parameters;
//	}

	public String toString(){
		String beanval;
		
		beanval = new String("EquationBean: \nequation: "+equation);
//		if (parameters != null){
//			beanval.concat("\nparams are not null");
//		}
		return beanval;
	}

}
