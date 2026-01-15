package ru.deskofnotes.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.deskofnotes.repository.TagRepository;
import ru.deskofnotes.repository.TrackRepository;
import ru.deskofnotes.repository.UserRepository;
import ru.deskofnotes.service.DBConnection;
import ru.deskofnotes.service.TagService;
import ru.deskofnotes.service.TrackService;
import ru.deskofnotes.service.UserService;

@WebListener
public class InitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            UserRepository userRepository = new UserRepository();
            TagRepository tagRepository = new TagRepository();
            TrackRepository trackRepository = new TrackRepository(tagRepository);

            UserService userService = new UserService(userRepository);
            TagService tagService = new TagService(tagRepository);
            TrackService trackService = new TrackService(trackRepository,tagService);


            ServletContext servletContext = sce.getServletContext();
            servletContext.setAttribute("trackService", trackService);
            servletContext.setAttribute("tagService", tagService);
            servletContext.setAttribute("userService", userService);
            DBConnection.init();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.destroy();
    }
}
