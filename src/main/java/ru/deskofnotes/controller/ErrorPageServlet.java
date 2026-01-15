package ru.deskofnotes.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/error")
public class ErrorPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer status = (Integer) req.getAttribute("jakarta.servlet.error.status_code");
        String message;
        if (status != null && status == 404) {
            message = "Страница не найдена";
        } else if (status != null && status == 403) {
            message = "Доступ запрещён";
        } else {
            message = "Произошла внутренняя ошибка сервера";
        }

        req.setAttribute("status", status != null ? status : 500);
        req.setAttribute("message", message != null ? message : "Произошла неизвестная ошибка");
        resp.setStatus((Integer) req.getAttribute("status"));
        req.getRequestDispatcher("/error.ftlh").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
