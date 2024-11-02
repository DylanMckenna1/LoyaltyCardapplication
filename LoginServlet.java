package packag;
	
	import java.io.IOException;
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import javax.servlet.ServletException;
	import javax.servlet.annotation.WebServlet;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;

	@WebServlet("/login")
	public class LoginServlet extends HttpServlet {
	    
	    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        Connection connection = null;
	        
	        try {
	            // Database connection setup
	            connection = DriverManager.getConnection(
	                    "jdbc:mysql://localhost:3306/LoyaltyCardApp?serverTimezone=UTC", "root", "password");
	            
	            // Prepare SQL to check if user exists and retrieve points
	            PreparedStatement checkUser = connection.prepareStatement(
	                    "SELECT points FROM LoyaltyCardHolders WHERE username = ? AND password = ?");
	            checkUser.setString(1, request.getParameter("username"));
	            checkUser.setString(2, request.getParameter("password"));
	            
	            ResultSet rs = checkUser.executeQuery();
	            
	            // Check if user is found and display points
	            if (rs.next()) {
	                int points = rs.getInt("points");
	                request.setAttribute("username", request.getParameter("username"));
	                request.setAttribute("points", points);
	                request.getRequestDispatcher("userDashboard.jsp").forward(request, response);
	            } else {
	                response.sendRedirect("login.html");
	            }
	            
	            rs.close();
	            checkUser.close();
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (connection != null) connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

	


