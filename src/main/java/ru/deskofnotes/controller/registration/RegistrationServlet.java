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

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        userService = (UserService) servletContext.getAttribute("userService");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String errorMsg = null;

        if (session != null) {
            errorMsg = (String) session.getAttribute("errormessage");
            session.removeAttribute("errormessage");
        }

        req.setAttribute("errormessage", errorMsg);
        req.getRequestDispatcher("/registration.ftlh").forward(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = new User();
        user.setUsername(req.getParameter("username"));
        user.setHashPassword(req.getParameter("password"));


        try {
            if (userService.getUser(user.getUsername()) != null) {
                userService.addUser(user);
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/login");
            } else {
                HttpSession session = req.getSession();
                session.setAttribute("errormessage", "Пользователь с таким именем уже существует");
                resp.sendRedirect(req.getContextPath() + "/registration");
            }
        } catch (Exception ex) {
            HttpSession session = req.getSession();
            session.setAttribute("errormessage", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/registration");
        }

    }
}

