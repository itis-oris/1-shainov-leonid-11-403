package ru.deskofnotes.controller.registration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;
import ru.deskofnotes.service.UserService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/account/delete")
public class AccountDeleteServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        userService = (UserService) servletContext.getAttribute("userService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (session == null || session.getAttribute("userId") == null) {
            out.write("{\"success\":false,\"error\":\"Не авторизован\"}");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        try {
            userService.deleteUser(userId);
            session.invalidate();
            out.write("{\"success\":true}");
        } catch (Exception e) {
            out.write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
