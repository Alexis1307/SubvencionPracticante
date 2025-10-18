package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import jakarta.servlet.ServletOutputStream;

@WebServlet("/verInforme")
public class VerInformeServlet extends HttpServlet {

    private static final String RUTA_DOCS_JEFE = "C:\\ProyectoSubvencionPDF\\jefeUnidad\\";
    private static final String RUTA_DOCS_PRACTICANTE = "C:\\ProyectoSubvencionPDF\\practicante\\";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String archivo = request.getParameter("archivo");
        String tipo = request.getParameter("tipo");
        System.out.println("ruta mas tipo: " + archivo + tipo);
        if (archivo == null || tipo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetros inválidos");
            return;
        }

        String rutaBase;
        if ("firmado".equalsIgnoreCase(tipo)) {
            rutaBase = RUTA_DOCS_JEFE;
        } else if ("practicante".equalsIgnoreCase(tipo)) {
            rutaBase = RUTA_DOCS_PRACTICANTE;
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de informe no válido");
            return;
        }

        File pdf = new File(rutaBase, archivo);
        if (!pdf.exists() || pdf.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + archivo + "\"");

        try (FileInputStream fis = new FileInputStream(pdf);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.flush();
        }
    }
}
