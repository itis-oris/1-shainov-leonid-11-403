package ru.deskofnotes.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

@WebServlet("/files/*")
public class FileServlet extends HttpServlet {

    private static final String UPLOAD_DIR = new StringBuilder(System.getProperty("user.home")).append(File.separator).append("deskofnotes").append(File.separator).append("uploads").toString();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();
        if (path == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = new File(UPLOAD_DIR, path);
        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long fileLength = file.length();
        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) mimeType = "application/octet-stream";

        resp.setHeader("Accept-Ranges", "bytes");
        resp.setContentType(mimeType);

        String range = req.getHeader("Range");

        if (range == null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentLengthLong(fileLength);

            try (InputStream in = new FileInputStream(file);
                 OutputStream out = resp.getOutputStream()) {
                in.transferTo(out);
            }
            return;
        }

        long start, end;
        String[] parts = range.replace("bytes=", "").split("-");
        start = Long.parseLong(parts[0]);
        end = (parts.length > 1 && !parts[1].isEmpty()) ? Long.parseLong(parts[1]) : fileLength - 1;

        if (end >= fileLength) end = fileLength - 1;

        long chunkSize = end - start + 1;

        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        resp.setHeader("Content-Length", String.valueOf(chunkSize));

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             OutputStream out = resp.getOutputStream()) {

            raf.seek(start);
            byte[] buffer = new byte[8192];
            long remaining = chunkSize;

            while (remaining > 0) {
                int read = raf.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                if (read == -1) break;
                out.write(buffer, 0, read);
                remaining -= read;
            }
        }
    }
}
