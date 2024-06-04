package it.unisa.control;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.unisa.model.UserBean;
import it.unisa.model.UserDao;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao userDao = new UserDao();
        
        try {
            String username = request.getParameter("un");
            String password = request.getParameter("pw");

            // Verifica che siano stati forniti username e password
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/Login.jsp?action=error");
                return;
            }
            
            // Esegui l'hashing della password con SHA-512
            String hashedPassword = hashPassword(password);

            // Recupera l'utente dal database
            UserBean user = userDao.doRetrieve(username, hashedPassword);

            if (user != null && user.isValid()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("currentSessionUser", user);

                String checkout = request.getParameter("checkout");
                if (checkout != null) {
                    response.sendRedirect(request.getContextPath() + "/account?page=Checkout.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/Home.jsp");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/Login.jsp?action=error"); // Pagina di errore
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = md.digest(password.getBytes());
            return bytesToHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 non è supportato", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
