package packag;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        
        try {
            // Database connection setup
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/LoyaltyCardApp?serverTimezone=UTC", "root", "password");
            
            // Check if passwords match
            if (!request.getParameter("password").equals(request.getParameter("confirmPassword"))) {
                response.sendRedirect("register.html");
                return;
            }
            
            // Prepare SQL to insert new user
            PreparedStatement createUser = connection.prepareStatement(
                    "INSERT INTO LoyaltyCardHolders (username, password, points) VALUES (?, ?, 100)");
            createUser.setString(1, request.getParameter("username"));
            createUser.setString(2, request.getParameter("password"));
            
            int rowsInserted = createUser.executeUpdate();
            createUser.close();
            
            // Redirect to login page if registration is successful
            if (rowsInserted > 0) {
                response.sendRedirect("login.html");
            } else {
                response.sendRedirect("register.html");
            }
            
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

