package packag;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/updatePoints")
public class PointsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        int pointsToUpdate = Integer.parseInt(request.getParameter("points"));
        
        Connection connection = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Database connection setup
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/LoyaltyCardApp?serverTimezone=UTC", "root", "password");

            // Retrieve current points from the database
            PreparedStatement getPointsStmt = connection.prepareStatement(
                    "SELECT points FROM LoyaltyCardHolders WHERE username = ?");
            getPointsStmt.setString(1, username);
            ResultSet rs = getPointsStmt.executeQuery();

            if (rs.next()) {
                int currentPoints = rs.getInt("points");
                int newPoints = currentPoints + pointsToUpdate;

                // Check if the new balance would be negative
                if (newPoints < 0) {
                    out.println("<html><body>");
                    out.println("<h3>Insufficient points. You cannot have a negative balance.</h3>");
                    out.println("<a href='updatePoints.html'>Go back</a>");
                    out.println("</body></html>");
                } else {
                    // Update the points in the database
                    PreparedStatement updatePointsStmt = connection.prepareStatement(
                            "UPDATE LoyaltyCardHolders SET points = ? WHERE username = ?");
                    updatePointsStmt.setInt(1, newPoints);
                    updatePointsStmt.setString(2, username);

                    int rowsUpdated = updatePointsStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        out.println("<html><body>");
                        out.println("<h3>Points updated successfully!</h3>");
                        out.println("<p>New balance for " + username + ": " + newPoints + " points</p>");
                        out.println("<a href='userDashboard.jsp'>Back to Dashboard</a>");
                        out.println("</body></html>");
                    } else {
                        out.println("<html><body>");
                        out.println("<h3>Failed to update points. Please try again.</h3>");
                        out.println("<a href='updatePoints.html'>Go back</a>");
                        out.println("</body></html>");
                    }

                    updatePointsStmt.close();
                }
            } else {
                out.println("<html><body>");
                out.println("<h3>User not found.</h3>");
                out.println("<a href='updatePoints.html'>Go back</a>");
                out.println("</body></html>");
            }

            rs.close();
            getPointsStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<html><body>");
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
            out.println("</body></html>");
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
