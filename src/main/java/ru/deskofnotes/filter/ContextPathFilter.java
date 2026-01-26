package ru.deskofnotes.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class ContextPathFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest req) {
            if (((HttpServletRequest) request).getRequestURI().startsWith(req.getContextPath() + "/static/")) {
                chain.doFilter(request, response);
                return;
            }
            request.setAttribute("contextPath", req.getContextPath());
        }
        chain.doFilter(request, response);
    }
}
