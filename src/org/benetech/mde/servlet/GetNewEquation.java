package org.benetech.mde.servlet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.benetech.mde.bean.EquationBean;
import org.benetech.mde.compute.GraphDescriber;

//TODO: Document this class
//TODO: Error handling ?

//@WebServlet("/GetNewEquation")
public class GetNewEquation extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetNewEquation() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("text/html");
		String equation = request.getParameter("equation");
		String[] pnames = request.getParameterValues("pname");
		String[] pvalues = request.getParameterValues("pvalue");

		String errMessage;

		int l1 = pnames.length;
		int l2 = pvalues.length;

		boolean paramsOk = true;
		if (l1 != l2) {
			paramsOk = false;
		} else {
			for (int i = 0; i < pnames.length; i++) {
				if ((pnames[i] == null) || (pnames[i] == "")
						|| (pvalues[i] == null) || (pvalues[i] == "")) {
					paramsOk = false;
					break;
				}
			}
		}
		// System.out.println("pname: " + pnames[i] + " pvalue: " + pvalues[i]);

		if (!paramsOk) {
			response.setStatus(400); // is this needed?
			response.sendError(400,
					"Bad Request. Invalid parameter name/value pairs.");
			// errMessage = new
			// String("Bad Request. Invalid parameter name/value pairs.");
		} else {
			if ((equation != null) && (equation != "")) {

				equation = equation.toLowerCase();

				PrintWriter out = response.getWriter();

				GraphDescriber describer = new GraphDescriber(equation);

				String newEquation = describer.getNewEquation(pnames, pvalues);
				
				EquationBean eqbean = new EquationBean(newEquation);
				
				
				// System.out.println("NEW EQUATION IS: "+newEquation);
				if (newEquation != null) {
					response.setStatus(200);
//					out.print(newEquation);
					
					response.setContentType("application/json");

					JSONObject respJson = new JSONObject(eqbean);
					out.print("newEquation(" + respJson + ")");
				}
				else {
					response.setStatus(400);
					response.sendError(400, "Bad Request. Invalid equation or parameters not in equation.");
				}
			} else {
				response.setStatus(400);
				response.sendError(400,
						"Bad Request.  Input equation required.");
				// errMessage = new
				// String("Bad Request.  Input equation required.");
			}
		}
	}

	// HTML out for testing only. Comment out for production.
	// out.println("<html>");
	// out.println("<head></head><body>");
	// out.println("<form method='get' action='/Mde-Web-Service/GetEquationDescription'>");
	// out.println("Enter an equation: ");
	// out.println("<input type='text' name='equation'>");
	// out.println("<input type='submit'>");
	// out.println("</form>");
	// out.println("</head></body>");
	// out.println("</html>");

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
