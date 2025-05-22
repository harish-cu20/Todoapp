package com.todo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AddTaskServlet")
public class AddTaskServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String targetDate = request.getParameter("target_date");

        Connection con = null;
        PreparedStatement ps = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "user");

            String query = "INSERT INTO todos (title, description, target_date, username, is_done) VALUES (?, ?, ?, ?, 0)";
            ps = con.prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, targetDate);
            ps.setString(4, username);

            int result = ps.executeUpdate();
            if (result > 0) {
                response.sendRedirect("tasks.jsp");
            } else {
                request.setAttribute("errorMessage", "Failed to add task.");
                request.getRequestDispatcher("tasks.jsp").forward(request, response);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
