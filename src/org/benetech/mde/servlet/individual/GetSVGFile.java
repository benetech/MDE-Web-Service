package org.benetech.mde.servlet;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.stream.FileCacheImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.tools.FileObject;

import gov.nasa.ial.mde.util.ResourceUtil;

import org.benetech.mde.compute.GraphDescriber;

//@WebServlet("/MdeGraphSVG")
public class GetSVGFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public GetSVGFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    response.setHeader("Cache-Control", "no-cache");
	    response.setHeader("Pragma", "no-cache");
	    response.setHeader("Content-Disposition", "attachment; filename=graph.svg");
//	    response.setContentType("image/svg+xml");
//	    response.setContentType("image/jpg");

	    String equation = request.getParameter("equation");
	    GraphDescriber describer = new GraphDescriber(equation);
	    String SVGout = describer.getGraphSVG();
	    	    
	    String graphFilePath = getServletContext().getRealPath("/") + "graph.svg";
		ResourceUtil.saveFile(graphFilePath, SVGout.getBytes());
		
		
	    ServletOutputStream servOut = response.getOutputStream();
//	    servOut.write(bais.read(fdata, 0, fdata.length));
//	    servOut.print(SVGout);
	    servOut.write(ResourceUtil.readFile(graphFilePath));

	    

	    
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	

}
