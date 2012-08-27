package org.benetech.mde.servlet;


import org.json.JSONObject;

import gov.nasa.ial.mde.util.ResourceUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.benetech.mde.compute.GraphDescriber;

//@WebServlet("/MdeGraphSVG")
public class GetSVG extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public GetSVG() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    response.setHeader("Cache-Control", "no-cache");
	    response.setHeader("Pragma", "no-cache");
	    response.setContentType("image/svg+xml");

	    String equation = request.getParameter("equation");
	    GraphDescriber describer = new GraphDescriber(equation);
	    String SVGout = describer.getGraphSVG();
	    	    
	    PrintWriter out = response.getWriter();

	    out.print(SVGout);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	

}
