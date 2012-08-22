package org.benetech.mde.servlet;


import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.benetech.mde.compute.GraphDescription;

//@WebServlet("/MdeDescribeEquation")
public class MdeDescribeEquation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public MdeDescribeEquation() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    response.setHeader("Cache-Control", "no-cache");
	    response.setHeader("Pragma", "no-cache");
	    response.setContentType("text/html");
	    String equation = request.getParameter("equation");
	    GraphDescription description = new GraphDescription(equation);
	    PrintWriter out = response.getWriter();
	    
	    JSONObject respJson = description.getJSONResponseBean();
	    out.println("<html>");
	    out.println("<head><title>MdeDescribeEquation JSON </title></head>");
	    out.println("<body>");
//	    out.println("<h1>IS THIS WORKING?</h1>");
//	    out.println("doGet(): description: "+description.getEquationDescriptionBean().getDescription());
//	    out.println("doGet(): respJson: "+respJson);
	    
	    out.println(respJson);
	    
	    out.println("<form method='get' action='/Mde-Web-Service/MdeDescribeEquation'>");
	    out.println("Enter an equation: ");
		out.println("<input type='text' name='equation'>");
		out.println("<input type='submit'>");
		out.println("</form>");
	    out.println("</body>");
	    out.println("</html>");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	

}
