package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import jakarta.servlet.ServletOutputStream;

@WebServlet("/verInforme")
public class VerInformeServlet extends HttpServlet {

    private static final String BASE_PATH = "C:\\ProyectoSubvencionPDF";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener el nombre del archivo y el tipo
        String archivo = request.getParameter("archivo");
        String tipo = request.getParameter("tipo"); // "firmado" o "practicante"

        if (archivo == null || archivo.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo no proporcionado");
            return;
        }

        // Determinar subcarpeta
        String carpeta = "practicante";
        if ("firmado".equalsIgnoreCase(tipo)) {
            carpeta = "jefeUnidad"; 
        }

        // Construir la ruta completa del archivo
        File pdf = new File(BASE_PATH + File.separator + carpeta, archivo);

        System.out.println("Buscando archivo en: " + pdf.getAbsolutePath());

        // Validar si el archivo existe
        if (!pdf.exists() || !pdf.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado");
            return;
        }

        // Configurar la respuesta HTTP para servir el PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + archivo + "\"");

        // Copiar el archivo al response
        try (ServletOutputStream out = response.getOutputStream()) {
            Files.copy(pdf.toPath(), out);
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al leer el archivo PDF");
            e.printStackTrace();
        }
    }
}
