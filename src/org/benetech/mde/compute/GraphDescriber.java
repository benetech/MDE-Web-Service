package org.benetech.mde.compute;

import gov.nasa.ial.mde.describer.Describer;
import gov.nasa.ial.mde.properties.MdeSettings;
import gov.nasa.ial.mde.solver.Solver;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedData;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedEquation;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedItem;
import gov.nasa.ial.mde.ui.graph.CartesianGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.tools.FileObject;

import org.benetech.mde.bean.GraphDescriptionBean;
import org.json.JSONObject;

public class GraphDescriber {

	String equation;
	String description = null;
	MdeSettings currentSettings;
	Solver solver;
	Describer describer;
	String outputFormat;
	String descriptionMode;

	GraphDescriptionBean eqbean;
	JSONObject jsonObject;

	public GraphDescriber(String equation) {
		this.equation = equation;
		currentSettings = new MdeSettings("mySettings");
		solver = new Solver();

		mdeFindSolution(equation);
	}

	public JSONObject getJSONDescription() {
		describer = new Describer(solver, currentSettings);
		describer.setOutputFormat("text");
		if (solver.anyDescribable())
			// TODO: make description mode an input
			description = describer.getDescriptions("standards");
		else
			description = "Equation `" + equation
					+ " ` is not supported by MDE.";
		eqbean = new GraphDescriptionBean();
		eqbean.setEquation(equation);
		eqbean.setDescription(description);
	
		// System.out.println("In GraphDescription(String equation): bean= "+eqbean);

		jsonObject = new JSONObject(eqbean);
		
		// We have to build the parameters array separately, because auto-bean instantiation isn't
		// working for arrays, nor HashMaps, nor ArrayLists.
		
		// We also only include the parameter names in the JSONObject because MDE will always 
		// default the values to 1.0.  Any manipulation of parameters has to be done by the 
		// client application, i.e., when a parameter value changes, rebuild the equation with those
		// values and then call the MDE service.
		
		String keys[] = getEquationParameters();
		try{
		for (int i=0; i < keys.length; i++)
			jsonObject.accumulate("parameters", keys[i]);
		// System.out.println("jsonObject = "+jsonObject);
		}
		catch (Exception e){
			System.err.println(e);
		}
		return jsonObject;
	}

	public String getGraphSVG() {
		String svg;
		CartesianGraph grapher = new CartesianGraph(solver, currentSettings);
		JFrame window = new JFrame("Tutorial_CartesianGraph");
		window.getContentPane().add(grapher);
		window.pack();
		if (solver.anyGraphable()) {
			svg = grapher.getSVG();
			// System.out.println("SVG: "+svg);
		} else {
			svg = null;
		}
		window.removeAll();
		window.dispose();
		return svg;
	}

	private void mdeFindSolution(Object data) {

		if (data instanceof String)
			solver.add((String) data);
		else if (data instanceof AnalyzedData)
			solver.add((AnalyzedItem) data);
		solver.solve();
		// solver.get(0).getAnalyzedItem().getFeatures();
	}

	public GraphDescriptionBean getEquationDescriptionBean() {
		return eqbean;
	}

	public JSONObject getJSONResponseBean() {
		return jsonObject;
	}

	// We'll only return the parameter names since MDE will always set the default value to 1.0
	public String[] getEquationParameters() {
		AnalyzedItem item = solver.get(0).getAnalyzedItem();
		String[] keys = null;  //test
		
		if (item instanceof AnalyzedEquation) {
			// System.out.println("item is instanceof AnalyzedEquation");

			AnalyzedEquation ae = (AnalyzedEquation) item;
			Hashtable<String,Object> ht = ae.getParameterHash();
			keys = ae.getParameters();	
		}
		return keys;
	}
	
	
	// ========================================================================================
	// ========================================================================================

	// public List<AnalyzedData> getDataFromFile(String fileName){
	// File file = new File(fileName);
	// TextDataFileParser fileParser = new TextDataFileParser(file);
	// AnalyzedData data = null;
	// List<AnalyzedData> list = null;
	// try {
	// list = fileParser.parse();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return list;
	// }

	// public EquationDescriptionParamsBean
	// getEquationDescriptionParamsBean(String equation){
	// EquationDescriptionParamsBean bean = new EquationDescriptionParamsBean();
	// MdeSettings currentSettings = new MdeSettings("myAppsMdeProperties");
	// Solver solver = new Solver();
	// Describer describer = new Describer(solver, currentSettings);
	// describer.setOutputFormat(Describer.TEXT_OUTPUT);
	//
	// solver.add((String)equation);
	// solver.solve();
	//
	// bean.setEquation(equation);
	// bean.setSvg(getGraphSVG(solver));
	//
	// if (solver.anyDescribable())
	// bean.setDescription(describer.getDescriptions("standards"));
	// else
	// bean.setDescription("Equation `" + equation +
	// " ` is not supported by MDE.");
	// AnalyzedItem item = solver.get(0).getAnalyzedItem();
	// if(item instanceof AnalyzedEquation){
	// AnalyzedEquation ae = (AnalyzedEquation)item;
	// HashMap<String, String> params = new HashMap<String, String>();
	// for(String key : ae.getParameters())
	// params.put(key, String.valueOf(ae.getParameterValue(key)));
	// bean.setParams(params);
	// }
	// solver.removeAll();
	// return bean;
	// }

	// public EquationDescriptionFileDataBean
	// getEquationDescriptionFileDataBean(List<AnalyzedData>data){
	// EquationDescriptionFileDataBean bean = new
	// EquationDescriptionFileDataBean();
	// int columns = data.size();
	// int rows = data.get(0).getDataSize();
	// double[][] table = new double[rows][columns + 1];
	// String [] column = new String[columns + 1];
	// int i = 1;
	// double [] x = data.get(0).getXValues();
	// for(int k = 0; k < x.length; k++)
	// table[k][0] = x[k];
	// column[0] = data.get(0).getXName();
	// for(AnalyzedData element : data){
	// double [] y = element.getYValues();
	// for(int j = 0; j < y.length; j ++)
	// table[j][i] = y[j];
	// column[i] = element.getYName();
	// i++;
	// }
	// bean.setTable(table);
	// bean.setColumn(column);
	// bean.setDescription(getMathDescription(data.get(0)));
	// return bean;
	// }

}
