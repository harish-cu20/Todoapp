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


@WebServlet("/regform")
public class Register extends HttpServlet{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/demo", "root", "user"
            );

            // Check if username already exists
            PreparedStatement checkStmt = con.prepareStatement(
                "SELECT username FROM users WHERE username = ?"
            );
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Username exists
                request.setAttribute("message", "Username already exists. Please choose a different one.");
                RequestDispatcher rd = request.getRequestDispatcher("Register.jsp");
                rd.forward(request, response);
            } else {
                // Insert new user
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (first_name, last_name, username, password) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, fname);
                ps.setString(2, lname);
                ps.setString(3, username);
                ps.setString(4, password);

                int count = ps.executeUpdate();
                if (count > 0) {
                    response.sendRedirect("Login.jsp?msg=registered");
                } else {
                    request.setAttribute("message", "User registration failed. Please try again.");
                    RequestDispatcher rd = request.getRequestDispatcher("Register.jsp");
                    rd.forward(request, response);
                }
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Server error. Please try again later.");
            RequestDispatcher rd = request.getRequestDispatcher("Register.jsp");
            rd.forward(request, response);
        }
    }
}