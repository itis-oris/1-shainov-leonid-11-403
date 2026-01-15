package ru.deskofnotes.controller.registration;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String errorMsg = null;

        if (session != null) {
            errorMsg = (String) session.getAttribute("errormessage");
            session.removeAttribute("errormessage");
        }

        req.setAttribute("errormessage", errorMsg);
        req.getRequestDispatcher("/login.ftlh").forward(req, resp);
    }
}