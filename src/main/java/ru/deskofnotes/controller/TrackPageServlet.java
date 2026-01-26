package ru.deskofnotes.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.deskofnotes.model.Track;
import ru.deskofnotes.model.User;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;

import java.io.IOException;
import java.util.List;

@WebServlet("/track")
public class TrackPageServlet extends HttpServlet {
    private TrackService trackService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        trackService = (TrackService) servletContext.getAttribute("trackService");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute("user");
        long id = Long.parseLong(req.getParameter("id"));
        Track track = trackService.findTrackById(id);
        req.setAttribute("track", track);
        List<Track> tracks = trackService.getTracksByUser(user.getId());
        req.setAttribute("userTracks", tracks);
        req.getRequestDispatcher("/track-edit.ftlh").forward(req, resp);
    }
}
