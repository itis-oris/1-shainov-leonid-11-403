package ru.deskofnotes.controller.registration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.deskofnotes.model.User;
import ru.deskofnotes.service.UserService;

import java.io.IOException;

@WebServlet("/usercheck")
public class UserCheckServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        userService = (UserService) servletContext.getAttribute("userService");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        User user = userService.checkUser(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/home");
        } else {
            session.setAttribute("errormessage","Неверное имя пользователя или пароль");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
