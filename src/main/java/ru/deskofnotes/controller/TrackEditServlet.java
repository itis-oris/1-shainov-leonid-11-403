package ru.deskofnotes.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ru.deskofnotes.model.Track;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;

import java.io.IOException;

@WebServlet("/track/edit")
@MultipartConfig
public class TrackEditServlet extends HttpServlet {

    private TrackService trackService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        trackService = (TrackService) servletContext.getAttribute("trackService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");

        try {
            long id = Long.parseLong(req.getParameter("id"));
            Track track = trackService.findTrackById(id);
            if (track == null) throw new IOException("Трек не найден");

            String title = req.getParameter("title");
            if (title != null) track.setTitle(title);

            String description = req.getParameter("description");
            if (description != null) track.setDescription(description);

            String lyrics = req.getParameter("lyrics");
            if (lyrics != null) track.setLyrics(lyrics);

            Part imagePart = req.getPart("image");
            if (imagePart != null && imagePart.getSize() > 0) {
                String imagePath = trackService.saveFileAndGetPath(id, imagePart, "image");
                if (imagePath != null) track.setImagePath(imagePath);
            }

            Part audioPart = req.getPart("audio");
            if (audioPart != null && audioPart.getSize() > 0) {
                String audioPath = trackService.saveFileAndGetPath(id, audioPart, "audio");
                if (audioPath != null) track.setAudioPath(audioPath);
            }

            trackService.updateTrack(track);
            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

