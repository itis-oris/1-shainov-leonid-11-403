package ru.deskofnotes.filter;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpSession session = ((HttpServletRequest)req).getSession(false);

        if (!checkExluded(((HttpServletRequest)req).getRequestURI()) && (session == null || session.getAttribute("user") == null)) {
            ((HttpServletResponse)resp).sendRedirect(((HttpServletRequest) req).getContextPath()+"/login");
        } else {
            chain.doFilter(req, resp);
        }
    }

    private boolean checkExluded(String resource) {
        return resource.contains("/login") || resource.contains("/usercheck") || resource.contains("/logout") || resource.contains("/registration") || resource.contains(".css") || resource.contains("auth") || resource.contains("/error");
    }
}
