package ru.deskofnotes.controller;


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.deskofnotes.model.Tag;
import ru.deskofnotes.model.Track;
import ru.deskofnotes.model.User;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomePageServlet extends HttpServlet {

    private TrackService trackService;
    private TagService tagService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        trackService = (TrackService) servletContext.getAttribute("trackService");
        tagService = (TagService) servletContext.getAttribute("tagService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<Track> tracks = trackService.getTracksByUser(user.getId());
        req.setAttribute("userTracks", tracks);
        List<Tag> allTags = tagService.getAll();
        req.setAttribute("allTags", allTags);

        req.getRequestDispatcher("/home-page.ftlh").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
