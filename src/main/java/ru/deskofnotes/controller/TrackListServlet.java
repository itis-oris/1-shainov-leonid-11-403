package ru.deskofnotes.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.deskofnotes.model.Track;
import ru.deskofnotes.model.Tag;
import ru.deskofnotes.model.User;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;

import java.io.IOException;
import java.util.List;

@WebServlet("/tracks/list")
public class TrackListServlet extends HttpServlet {
    private TrackService trackService;

    @Override
    public void init() {
        ServletContext servletContext = getServletContext();
        trackService = (TrackService) servletContext.getAttribute("trackService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String sort = req.getParameter("sort");
        if (sort == null || sort.isBlank()) sort = "updated";

        String search = req.getParameter("search");

        String tagsParam = req.getParameter("tags");
        List<String> tags = null;
        if (tagsParam != null && !tagsParam.isBlank()) {
            tags = List.of(tagsParam.split(","));
        }

        List<Track> tracks = trackService.getTracksByUserFiltered(user.getId(), sort, search, tags);

        StringBuilder sb = new StringBuilder();
        sb.append("{\"tracks\":[");
        for (int i = 0; i < tracks.size(); i++) {
            Track t = tracks.get(i);
            sb.append("{")
                    .append("\"id\":").append(t.getId()).append(",")
                    .append("\"title\":\"").append(t.getTitle().replace("\"", "\\\"")).append("\",")
                    .append("\"imagePath\":").append(t.getImagePath() == null ? "null" : "\"" + t.getImagePath() + "\"").append(",")
                    .append("\"audioPath\":").append(t.getAudioPath() == null ? "null" : "\"" + t.getAudioPath() + "\"").append(",")
                    .append("\"tags\":[");
            List<Tag> tTags = t.getTags();
            if (tTags != null) {
                for (int j = 0; j < tTags.size(); j++) {
                    Tag tag = tTags.get(j);
                    sb.append("{")
                            .append("\"id\":").append(tag.getId()).append(",")
                            .append("\"name\":\"").append(tag.getName().replace("\"", "\\\"")).append("\",")
                            .append("\"color\":\"").append(tag.getColor()).append("\"")
                            .append("}");
                    if (j < tTags.size() - 1) sb.append(",");
                }
            }
            sb.append("]}");
            if (i < tracks.size() - 1) sb.append(",");
        }
        sb.append("]}");

        resp.getWriter().write(sb.toString());
    }
}
