package ru.deskofnotes.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.deskofnotes.model.Tag;
import ru.deskofnotes.model.User;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/track/tag/add")
public class TagServlet extends HttpServlet {

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
                resp.getWriter().write("{\"success\":false,\"error\":\"Unauthorized\"}");
                return;
            }

            String tagName = req.getParameter("tagName");
            String tagColor = req.getParameter("tagColor");
            String trackIdStr = req.getParameter("trackId");

            if (tagName == null || tagName.isBlank() || trackIdStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false,\"error\":\"Missing tagName or trackId\"}");
                return;
            }

            long trackId = Long.parseLong(trackIdStr);
            Tag tag = trackService.addTagToTrackReturnTag(trackId, tagName.trim(), tagColor, user.getId());

            String json = String.format("{\"success\":true,\"id\":%d,\"name\":\"%s\",\"color\":\"%s\"}",
                    tag.getId(),
                    escapeJson(tag.getName()),
                    escapeJson(tag.getColor()));
            resp.getWriter().write(json);

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

