package org.benetech.mde.compute;



import gov.nasa.ial.mde.describer.Describer;
import gov.nasa.ial.mde.properties.MdeSettings;
import gov.nasa.ial.mde.solver.Solver;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedData;
import gov.nasa.ial.mde.solver.symbolic.AnalyzedItem;
import gov.nasa.ial.mde.ui.graph.CartesianGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

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
	
	
	public GraphDescriber(String equation){
		this.equation = equation;
		currentSettings = new MdeSettings("mySettings");
	    solver = new Solver();

	    mdeFindSolution(equation);
	}

	public JSONObject getJSONDescription() {
	    describer = new Describer(solver, currentSettings);
	    describer.setOutputFormat("text");
		if (solver.anyDescribable())
        	//TODO:  make description mode an input
            description = describer.getDescriptions("standards");
        else 
        	description = "Equation `" + equation + " ` is not supported by MDE.";
		eqbean = new GraphDescriptionBean();
		eqbean.setEquation(equation);
		eqbean.setDescription(description);
		
//		System.out.println("In GraphDescription(String equation): bean= "+eqbean);
//		eqbean.setParams(getEquationParameters());
		
		jsonObject = new JSONObject(eqbean);
//		System.out.println("jsonObject = "+jsonObject);
		
		return jsonObject;
	}
	
	public String getGraphSVG(){
		String svg;
		CartesianGraph grapher = new CartesianGraph(solver, currentSettings);
		JFrame window = new JFrame("Tutorial_CartesianGraph");
        window.getContentPane().add(grapher);
        window.pack();
		if (solver.anyGraphable()) {
			svg = grapher.getSVG();
//			System.out.println("SVG: "+svg);
		}else{
			svg = null;
		}
		window.removeAll();
		window.dispose();
		return svg;
	}
	
//	public void getGraphSVGFile(){
//		String svg = getGraphSVG();
//		String graphFilePath = getServletContext().getRealPath("/") + "tmp/graph.svg";
//	    ResourceUtil.saveFile(graphFilePath, data.getSvg().getBytes());
//	}
	
	private void mdeFindSolution(Object data){
		
        if(data instanceof String)
        	solver.add((String)data);
        else if(data instanceof AnalyzedData)
        	solver.add((AnalyzedItem)data);
        solver.solve();
        //solver.get(0).getAnalyzedItem().getFeatures();
	}
	

	
	public GraphDescriptionBean getEquationDescriptionBean(){
		return eqbean;
	}
	
	public JSONObject getJSONResponseBean(){
		return jsonObject;
	}
	
	
//========================================================================================
//========================================================================================

//	public List<AnalyzedData> getDataFromFile(String fileName){
//		File file = new File(fileName);        
//        TextDataFileParser  fileParser = new TextDataFileParser(file);
//        AnalyzedData data = null;
//        List<AnalyzedData> list = null;
//		try {
//			list = fileParser.parse();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return list;
//	}
	
	
//	public HashMap getEquationParameters(){
//	AnalyzedItem item = solver.get(0).getAnalyzedItem();
//	HashMap<String, String> params = new HashMap<String, String>();
//    if(item instanceof AnalyzedEquation){
////    	System.out.println("item is instanceof AnalyzedEquation");
//    	
//    	AnalyzedEquation ae = (AnalyzedEquation)item;
//    	String[] keys = ae.getParameters();
////    	System.out.println("keys.length: "+keys.length);
//    	
//    	Double d;
//    	for(int i=0; i < keys.length; i++){
////    		System.out.println("getEquationParameters: keys[i] = "+keys[i]);
////    		System.out.println("Double.valueOf: "+Double.valueOf(ae.getParameterValue(keys[i])));
//    		d = Double.valueOf(ae.getParameterValue(keys[i]));
//    		params.put(keys[i], d.toString());
//    	}
////    	System.out.println("GraphDescription.getEquationParameters(): "+params);
//    }
//    return params;
//}
	
//	public EquationDescriptionParamsBean getEquationDescriptionParamsBean(String equation){
//		EquationDescriptionParamsBean bean = new EquationDescriptionParamsBean();
//    	MdeSettings currentSettings = new MdeSettings("myAppsMdeProperties");
//        Solver solver = new Solver();
//        Describer describer = new Describer(solver, currentSettings);
//        describer.setOutputFormat(Describer.TEXT_OUTPUT);
//
//        solver.add((String)equation);
//        solver.solve();
//        
//        bean.setEquation(equation);
//        bean.setSvg(getGraphSVG(solver));
//        
//        if (solver.anyDescribable())
//            bean.setDescription(describer.getDescriptions("standards"));
//        else 
//        	bean.setDescription("Equation `" + equation + " ` is not supported by MDE.");
//        AnalyzedItem item = solver.get(0).getAnalyzedItem();
//        if(item instanceof AnalyzedEquation){
//        	AnalyzedEquation ae = (AnalyzedEquation)item;
//        	HashMap<String, String> params = new HashMap<String, String>();
//        	for(String key : ae.getParameters())
//        		params.put(key, String.valueOf(ae.getParameterValue(key)));
//        	bean.setParams(params);	
//        }
//        solver.removeAll(); 
//        return bean;        
//	}
	
//	public EquationDescriptionFileDataBean getEquationDescriptionFileDataBean(List<AnalyzedData>data){
//		EquationDescriptionFileDataBean bean = new EquationDescriptionFileDataBean();
//		int columns = data.size();
//		int rows = data.get(0).getDataSize();
//		double[][] table = new double[rows][columns + 1];
//		String [] column = new String[columns + 1];
//		int i = 1;
//		double [] x = data.get(0).getXValues();
//		for(int k = 0; k < x.length; k++)
//			table[k][0] = x[k];
//		column[0] = data.get(0).getXName();
//		for(AnalyzedData element : data){
//			double [] y  = element.getYValues();
//			for(int j = 0; j < y.length; j ++)
//				table[j][i] = y[j];
//			column[i] = element.getYName();
//			i++;
//		}
//		bean.setTable(table);
//		bean.setColumn(column);
//		bean.setDescription(getMathDescription(data.get(0)));
//		return bean;
//	}
	

	

}
