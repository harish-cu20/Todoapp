package com.todo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet("/EditTaskServlet")
public class EditTaskServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String taskId = request.getParameter("id");
        
        if (taskId == null || taskId.isEmpty()) {
            response.sendRedirect("tasks.jsp");
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "user");

            String query = "SELECT * FROM todos WHERE id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, taskId);
            rs = ps.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                String targetDate = rs.getString("target_date");

                // Set task details as request attributes
                request.setAttribute("taskId", taskId);
                request.setAttribute("title", title);
                request.setAttribute("description", description);
                request.setAttribute("targetDate", targetDate);

                // Forward to editTask.jsp
                request.getRequestDispatcher("editTask.jsp").forward(request, response);
            } else {
                // Task not found, redirect back to task list
                response.sendRedirect("tasks.jsp");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching the task.");
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
            if (ps != null) try { ps.close(); } catch (SQLException ignore) {}
            if (con != null) try { con.close(); } catch (SQLException ignore) {}
        }
    }
	

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String taskId = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String targetDate = request.getParameter("target_date");

        Connection con = null;
        PreparedStatement ps = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "user");

            String query = "UPDATE todos SET title = ?, description = ?, target_date = ? WHERE id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, targetDate);
            ps.setString(4, taskId);

            int result = ps.executeUpdate();
            if (result > 0) {
                response.sendRedirect("tasks.jsp");
            } else {
                request.setAttribute("errorMessage", "Failed to update task.");
                request.getRequestDispatcher("editTask.jsp").forward(request, response);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
        } finally {
            if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
    }
}

