package servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import dao.InformeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Informe;

@WebServlet("/verDocumento")
public class VerDocumentoServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idInformeStr = request.getParameter("idInforme");
        if (idInformeStr == null || idInformeStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de informe no proporcionado");
            return;
        }

        int idInforme = Integer.parseInt(idInformeStr);

        // Simulando la obtención de usuario y rol (puedes adaptarlo a tu sistema de sesiones)
        HttpSession session = request.getSession();
        String nombreUsuario = (String) session.getAttribute("nombreUsuario");  // "practicante", "jefeUnidad", etc.

        // Simulación de búsqueda de informe en base de datos
        Informe informe = InformeDAO.obtenerInformePorId(idInforme);  // Reemplaza con tu método real

        if (informe == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Informe no encontrado");
            return;
        }

        // Determinar la ruta del archivo
        String rutaBase = "C:/SubvencionPracticante/ProyectoSubvencionPDF/";
        String subcarpeta = "";
        String nombreArchivo = "";

        String estado = informe.getEstado();

        if (estado.equalsIgnoreCase("Pendiente") || estado.equalsIgnoreCase("Rechazado")) {
            // Documento sin firmar
            subcarpeta = "practicante/";
            nombreArchivo = informe.getNombreDocumento();  // ej: "informe123.pdf"
        } else if (estado.equalsIgnoreCase("En revision") || estado.equalsIgnoreCase("Aprobado")) {
            // Documento firmado
            subcarpeta = "jefeUnidad/";
            // agregamos el sufijo "_firmado" antes de la extensión
            String original = informe.getNombreDocumento(); // ej: "informe123.pdf"
            int dotIndex = original.lastIndexOf('.');
            if (dotIndex > 0) {
                nombreArchivo = original.substring(0, dotIndex) + "_firmado" + original.substring(dotIndex);
            } else {
                nombreArchivo = original + "_firmado"; // si no tiene extensión
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Estado del informe desconocido");
            return;
        }

        String rutaCompleta = rutaBase + subcarpeta + nombreArchivo;
       
        File file = new File(rutaCompleta);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado");
            return;
        }

        // Preparar respuesta como PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        response.setContentLength((int) file.length());

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}

