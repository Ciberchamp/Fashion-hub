package com.shashi.srv;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shashi.beans.UserBean;
import com.shashi.service.impl.UserServiceImpl;

@WebServlet("/LoginSrv")
public class LoginSrv extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public LoginSrv() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            System.out.println("LoginSrv doPost method called");
            
            String userName = request.getParameter("username");
            String password = request.getParameter("password");
            String userType = request.getParameter("usertype");
            
            System.out.println("Username: " + userName);
            System.out.println("UserType: " + userType);
            
            response.setContentType("text/html;charset=UTF-8");

            if (userName == null || password == null || userType == null) {
                request.setAttribute("message", "Please fill all fields");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            if (userType.equals("admin")) {
                // Admin login
                if (password.equals("admin") && userName.equals("admin@gmail.com")) {
                	System.out.println("Password entered: " + password);
                    System.out.println("Admin login successful");
                    
                    HttpSession session = request.getSession();
                    session.setAttribute("username", userName);
                    session.setAttribute("password", password);
                    session.setAttribute("usertype", userType);
                    
                    response.sendRedirect("adminViewProduct.jsp");
                } else {
                    System.out.println("Admin login failed");
                    request.setAttribute("message", "Invalid Admin Credentials");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } else {
                // Customer login
                UserServiceImpl userService = new UserServiceImpl();
                String status = userService.isValidCredential(userName, password);
                
                System.out.println("Customer login status: " + status);

                if (status.equalsIgnoreCase("valid")) {
                    UserBean user = userService.getUserDetails(userName, password);
                    HttpSession session = request.getSession();
                    session.setAttribute("userdata", user);
                    session.setAttribute("username", userName);
                 // In LoginSrv.java, add this line after validating credentials
                    session.setAttribute("password", password);
                    session.setAttribute("usertype", userType);
                   
                    response.sendRedirect("userHome.jsp");
                } else {
                    request.setAttribute("message", status);
                    request.getRequestDispatcher("userHome.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in LoginSrv: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("message", "An error occurred during login");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("LoginSrv doGet method called - redirecting to doPost");
        doPost(request, response);
    }
}