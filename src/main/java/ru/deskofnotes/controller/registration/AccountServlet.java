package ru.deskofnotes.controller.registration;

import jakarta.servlet.ServletContext;
import ru.deskofnotes.model.User;
import ru.deskofnotes.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        userService = (UserService) servletContext.getAttribute("userService");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        req.setAttribute("username", user.getUsername());
        req.getRequestDispatcher("/account.ftlh").forward(req, resp);
    }
}
