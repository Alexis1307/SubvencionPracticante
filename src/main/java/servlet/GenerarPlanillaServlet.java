package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Informe;
import model.Planilla;
import model.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.InformeDAO;
import dao.PlanillaDAO;


@WebServlet("/generarPlanilla")
public class GenerarPlanillaServlet extends HttpServlet {
    private InformeDAO informeDAO = new InformeDAO();
    private PlanillaDAO planillaDAO = new PlanillaDAO();

    private static final String CARPETA_DESTINO = "C:\\SubvencionPracticante\\ProyectoSubvencionPDF\\especialista";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	System.out.println("Entró a doPost");
        
    	Usuario especialista = (Usuario) request.getSession().getAttribute("usuarioLogueado");
        if (especialista == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idsParam = request.getParameter("ids");
        if (idsParam == null || idsParam.trim().isEmpty()) {
            request.setAttribute("mensaje", "No se seleccionaron informes.");
            request.setAttribute("tipoMensaje", "warning");
            request.getRequestDispatcher("/especialista").forward(request, response);
            return;
        }

        String[] idArray = idsParam.split(",");
        List<Informe> lista = new ArrayList<>();
        for (String s : idArray) {
            try {
                int id = Integer.parseInt(s);
                Informe inf = informeDAO.obtenerInformePorId(id);
                if (inf != null && "Aprobado".equalsIgnoreCase(inf.getEstado())) {
                    lista.add(inf);
                }
            } catch (NumberFormatException e) {
                // ignorar ID mal formateado
            }
        }

        if (lista.isEmpty()) {
            request.setAttribute("mensaje", "Los informes seleccionados no son válidos.");
            request.setAttribute("tipoMensaje", "error");
            request.getRequestDispatcher("/especialista").forward(request, response);
            return;
        }

        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "planilla_" + timestamp + ".xlsx";
        String rutaArchivoCompleta = CARPETA_DESTINO + File.separator + nombreArchivo;

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Informes Aprobados");
            int rowNum = 0;
            var header = sheet.createRow(rowNum++);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Practicante");
            header.createCell(2).setCellValue("Asunto");
            header.createCell(3).setCellValue("Área");
            header.createCell(4).setCellValue("Periodo (meses)");
            header.createCell(5).setCellValue("Fecha de Envío");
            header.createCell(6).setCellValue("Estado");

            for (Informe inf : lista) {
                var row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(inf.getInformeID());
                row.createCell(1).setCellValue(inf.getPracticante().getNombreUsuario());
                row.createCell(2).setCellValue(inf.getAsunto());
                row.createCell(3).setCellValue(inf.getRol().getNombreRol());
                row.createCell(4).setCellValue(inf.getPeriodoPracticas());
                row.createCell(5).setCellValue(inf.getFechaEnvio().toString());
                row.createCell(6).setCellValue(inf.getEstado());
            }

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivoCompleta)) {
                workbook.write(fileOut);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error generando o guardando planilla", e);
        }

        // Registrar en base de datos
        Planilla planilla = new Planilla();
        planilla.setEspecialista(especialista);
        planilla.setRutaDocumento(rutaArchivoCompleta);
        planilla.setFechaCreacion(LocalDate.now());
        planilla.setEstadoPlanilla("Generada");
        planilla.setNombrePlanilla(nombreArchivo);

        planillaDAO.guardar(planilla);

        request.getSession().setAttribute("mensaje", "Planilla generada y guardada correctamente.");
        request.getSession().setAttribute("tipoMensaje", "success");
        response.sendRedirect(request.getContextPath() + "/especialista");

    }

    // Si no quieres permitir acceso por GET, puedes dejarlo vacío o con error:
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método GET no permitido");
    }
}



