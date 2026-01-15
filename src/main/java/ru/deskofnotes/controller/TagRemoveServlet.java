package ru.deskofnotes.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.deskofnotes.model.User;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/track/tag/remove")
public class TagRemoveServlet extends HttpServlet {

    private TrackService trackService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        trackService = (TrackService) servletContext.getAttribute("trackService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }

            String trackIdStr = req.getParameter("trackId");
            String tagIdStr = req.getParameter("tagId");

            if (trackIdStr == null || tagIdStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing trackId or tagId\"}");
                return;
            }

            long trackId = Long.parseLong(trackIdStr);
            long tagId = Long.parseLong(tagIdStr);

            trackService.removeTagFromTrack(trackId, tagId);

            resp.getWriter().write("{\"success\":true}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "");
    }
}
