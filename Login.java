package com.todo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Login")
public class Login extends HttpServlet{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		PrintWriter out = response.getWriter();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","user");
			System.out.println("Driver");
			
			PreparedStatement ps = con.prepareStatement("select *from users where username=? and password=?");
			ps.setString(1,username);
			ps.setString(2, password);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				System.out.println("Login successful");
				HttpSession session = request.getSession();
                session.setAttribute("username", username);
                response.sendRedirect("tasks.jsp");
			}
			else {
				response.setContentType("text/html");
				out.print("invalid user id");
				
				RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp");
				rd.include(request, response);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
}
}
