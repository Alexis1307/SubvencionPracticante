package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/verDocumento")
public class VerInformeServlet extends HttpServlet {

    private static final String RUTA_DOCS_JEFE = "C:\\ProyectoSubvencionPDF\\jefeUnidad\\";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreArchivo = request.getParameter("nombre");

        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo faltante.");
            return;
        }

        File archivo = new File(RUTA_DOCS_JEFE, nombreArchivo);

        if (!archivo.exists() || archivo.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado.");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + archivo.getName() + "\"");

        try (FileInputStream fis = new FileInputStream(archivo);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesLeidos;
            while ((bytesLeidos = fis.read(buffer)) != -1) {
            	os.write(buffer, 0, bytesLeidos);
            }
        }
    }
}
