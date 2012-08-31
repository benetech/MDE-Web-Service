package org.benetech.mde.servlet;

import gov.nasa.ial.mde.util.ResourceUtil;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.benetech.mde.bean.GraphDescriptionBean;
import org.benetech.mde.compute.GraphDescriber;
import org.json.JSONObject;

//@WebServlet("/MdeDescribeEquation")
public class GetEquationDescription extends HttpServlet {
	private static final long serialVersionUID = 1L;

//	private static enum MDE_DESCRIPTION_MODE {visual,math,standards};
//	private static enum MDE_OUTPUT_FORMAT {"text","html","xml"};
//	private static String RESPONSE_FORMATS = {"text","textbean","json","svg","svgfile","mp3file"};
	private String optionalMode = "standards";
	private String optionalFormat = "text";

	public GetEquationDescription() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");

		//Get input parameters
		String equation = request.getParameter("equation");
		String mdeFormatOut = request.getParameter("mdeFormatOut");
		String descriptionMode = request.getParameter("descriptionMode");
		String responseFormat = request.getParameter("responseFormat");
		
		//TODO:  Convert inputs to all lower case.

//		System.out.println("responseFormat: " + responseFormat);
//		System.out.println("responseFormat null?  " + (responseFormat == null));
//		System.out.println("responseFormat blank? " + (responseFormat == ""));
		
		GraphDescriber describer;
		//If mde options are specified, use them
		if (mdeFormatOut.equals("text") || mdeFormatOut.equals("html") || mdeFormatOut.equals("xml"))
			optionalFormat = mdeFormatOut;
		if (descriptionMode.equals("visual") || descriptionMode.equals("math") || descriptionMode.equals("standards"))
			optionalMode = descriptionMode;

		describer = new GraphDescriber(equation, optionalFormat, optionalMode);
		
		//TODO: check input equation for malicious stuff
		
		if (responseFormat.equals("text") || responseFormat.equals("textbean")
				|| responseFormat.equals("json")
				|| responseFormat.equals("svg")) {
			
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");

			switch (responseFormat) {
			case "text":
				String text = describer.getTextDescription();
				out.print(text);
				break;
			case "textbean":
				GraphDescriptionBean bean = describer.getTextDescriptionBean();
				out.print(bean);
				break;
			case "json":
				JSONObject respJson = describer.getJSONDescription();
				out.print(respJson);
				break;
			case "svg":
				String SVGout = describer.getGraphSVG();
				out.print(SVGout);
				break;
			default:
				out.print("Error: Invalid response type requested");
				break;
			}
			
			 //HTML out for testing only. Comment out for production. ====================
//			 out.println("<html>");
//			 out.println("<head></head><body>");
//			 out.println("<form method='get' action='/Mde-Web-Service/GetEquationDescription'>");
//			 out.println("Enter an equation: ");
//			 out.println("<input type='text' name='equation'>");
//			 out.println("<p>Enter mde output format: text, html, xml:");
//			 out.println("<input type='text' name='mdeFormatOut'>");
//			 out.println("<p>Enter mde description mode: visual,math,standards");
//			 out.println("<input type='text' name='descriptionMode'>");
//			 out.println("<p>Enter response format : text,json,textbean,svg,svgfile");
//			 out.println("<input type='text' name='responseFormat'>");
//			 out.println("<input type='submit'>");
//			 out.println("</form>");
//			 out.println("</head></body>");
//			 out.println("</html>");
			 //end test convenience code ======================================================================
			 
			out.close();

		} else if (responseFormat.equals("svgfile")
				|| responseFormat.equals("mp3")) {

			ServletOutputStream servOut = response.getOutputStream();

			switch (responseFormat) {
			case "svgfile":
				response.setHeader("Content-Disposition",
						"attachment; filename=graph.svg");
				String SVGFileContents = describer.getGraphSVG();
				String graphFilePath = getServletContext().getRealPath("/")
						+ "graph.svg";

				ResourceUtil
						.saveFile(graphFilePath, SVGFileContents.getBytes());
				servOut.write(ResourceUtil.readFile(graphFilePath));
				break;
			case "mp3file":
				// TODO:
				break;
			default:
				break;
			}
			servOut.close();
		}
		else {
			//TODO:  Put a proper error code in
			response.setContentType("text/html");
			response.sendError(666, "Error:  Doom and Gloom");
		}

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
