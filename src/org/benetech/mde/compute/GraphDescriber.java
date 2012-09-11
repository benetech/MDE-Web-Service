package org.benetech.mde.compute;

import gov.nasa.ial.mde.describer.Describer;
import gov.nasa.ial.mde.properties.MdeSettings;
import gov.nasa.ial.mde.solver.Solver;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedData;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedEquation;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedItem;
import gov.nasa.ial.mde.ui.graph.CartesianGraph;

import java.util.Hashtable;

import javax.swing.JFrame;

import org.benetech.mde.bean.GraphDescriptionBean;
import org.json.JSONObject;

/**
 * GraphDescriber is a helper class for MDE web services. On construction, It
 * initializes the MDE library and calls Solver to generate a "solved graph"
 * from which various descriptions can be generated. After construction, clients call
 * GraphDescriber public methods to generate the description of choice.
 * <p>
 * Current description and data return methods are: <blockquote>
 * <ul>
 * <li>
 * getTextDescription - returns the text description as a String</li>
 * <li>
 * getTextDescriptionBean - returns the bean representation of the text
 * description.</li>
 * <li>
 * getJSONDescription - returns a text description as a JSON Object,</li> 
 * <li>
 * getGraphSVG - returns a svg description of the visual graph.
 * </li>
 * <li>
 * getNewEquation - returns the equation with new parameter values substituted in.
 * </ul>
 * 
 * @see GraphDescriptionBean for the text description contents. </li><li>
 *      getGraphSVG - returns the SVG description of the graph as a String. 
 *      </li><li>
 *      </ul>
 * 
 * @author Terry Hodgson, based on original code by Alex Yang August 2012
 * 
 */
public class GraphDescriber {

	
	private String equation;
	private String description = null;
	private MdeSettings currentSettings;
	private Solver solver;
	private String mdeOutputFormat;
	private String mdeDescriptionMode;

	private GraphDescriptionBean eqbean;
	private JSONObject jsonObject;

	public GraphDescriber(String equation) {
		this.equation = equation;
		currentSettings = new MdeSettings("mySettings");
		solver = new Solver();

		mdeFindSolution(equation);
	}
	
	public GraphDescriber(String equation, String mdeOutputFormat, String mdeDescriptionMode) {

		this.equation = equation;
		currentSettings = new MdeSettings("mySettings");
		solver = new Solver();

		mdeFindSolution(equation);
		this.mdeOutputFormat = mdeOutputFormat;
		this.mdeDescriptionMode = mdeDescriptionMode;
	}

	private void mdeFindSolution(Object data) {

		if (data instanceof String)
			solver.add((String) data);
		else if (data instanceof AnalyzedData)
			solver.add((AnalyzedItem) data);
		solver.solve();
		// solver.get(0).getAnalyzedItem().getFeatures();
	}
	
	public String getNewEquation(String[] parameterNames, String[] parameterValues){
		String newEquation;
		
		AnalyzedEquation ae = setEquationParameters(parameterNames, parameterValues);
		
		if (ae != null){
			newEquation = ae.printEquation();

			return newEquation;
		}
		
		return null;
	}

	/**
	 * 
	 */
	public String getTextDescription() {
		Describer describer;

		if (mdeOutputFormat == null)
			mdeOutputFormat = new String("text");
		if (mdeDescriptionMode == null)
			mdeDescriptionMode = new String("standards");
		
//		System.out.println("mode: " + mdeDescriptionMode);
		
		describer = new Describer(solver, currentSettings);
		describer.setOutputFormat(mdeOutputFormat);
		describer.setCurrentDescriptionMode(mdeDescriptionMode);
//		eqbean = null;
		
		if (solver.anyDescribable()) {
			// TODO: make description mode an input
			description = describer.getDescriptions(mdeDescriptionMode);
		} else
			description = "Equation `" + equation
					+ " ` is not supported by MDE.";
		return description;
	}

	/**
	 * 
	 */
	public GraphDescriptionBean getTextDescriptionBean() {
		// solver is already initialized with the equation and solution

		if (description == null)
			description = getTextDescription();

		eqbean = new GraphDescriptionBean();
		eqbean.setEquation(equation);
		eqbean.setDescription(description);
		return eqbean;

	}

	public JSONObject getJSONDescription() {
		getTextDescriptionBean();

		// System.out.println("In GraphDescription(String equation): bean= "+eqbean);

		jsonObject = new JSONObject(eqbean);

		// We have to build the parameters array separately, because auto-bean
		// instantiation isn't
		// working for arrays, nor HashMaps, nor ArrayLists.

		// We also only include the parameter names in the JSONObject because
		// MDE will always
		// default the values to 1.0. Any manipulation of parameters has to be
		// done by the
		// client application, i.e., when a parameter value changes, rebuild the
		// equation with those
		// values and then call the MDE service.

		String keys[] = getEquationParameters();

		// System.out.println("keys = null? "+(keys == null));
		// System.out.println("keys.length: "+keys.length);

		try {
			for (int i = 0; i < keys.length; i++)
				jsonObject.accumulate("parameters", keys[i]);
			// System.out.println("jsonObject = "+jsonObject);
		} catch (Exception e) {
			System.err.println(e);
		}
		return jsonObject;
	}

	// public JSONObject getJSONResponseBean() {
	// return jsonObject;
	// }

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
		grapher.removeAll();
		window.removeAll();
		window.dispose();
		
		return svg;
	}

	// We'll only return the parameter names since MDE will always set the
	// default value to 1.0
	private String[] getEquationParameters() {
		
		String[] keys = null; 
		
		AnalyzedEquation ae = getAnalyzedEquation();
		
		if (ae != null)
			keys = ae.getParameters();
		return keys;
	}
	
	public AnalyzedEquation setEquationParameters(String[] pnames, String[] pvalues){

		AnalyzedEquation ae = getAnalyzedEquation();
		
		if (ae != null){
			
			for (int i=0; i < pnames.length; i++)
				ae.setParameterValue(pnames[i], Double.valueOf(pvalues[i]).doubleValue());
			ae.updateFeatures();
//			System.out.println("equation: "+ae.printOriginalEquation());
		}
		return ae;
	}
	
	public AnalyzedEquation getAnalyzedEquation(){
		AnalyzedItem item = getAnalyzedItem();
		if (item instanceof AnalyzedEquation) {
			// System.out.println("item is instanceof AnalyzedEquation");
			AnalyzedEquation ae = (AnalyzedEquation) item;
			return ae;
		}
		else
			return null;
	}
	
	/**
	 * @return
	 */
	private AnalyzedItem getAnalyzedItem() {
		AnalyzedItem item = solver.get(0).getAnalyzedItem();
		return item;
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
