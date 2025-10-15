<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Jefe de Recursos Humanos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/jefeRrhh.css" />
</head>
<body>
<nav class="navbar">
  <div class="navbar-links">
    <a href="#inicio">Inicio</a>

    <a id="revisar-planilla" class="btn-principal" href="${pageContext.request.contextPath}/views/revisar_planilla.html">
        Revisar Planilla
    </a>

    <!-- Notificaciones -->
    <div class="notificaciones">
      <button id="btnNotificaciones">
        Notificaciones
        <span class="badge" id="badgeNotificaciones">2</span>
      </button>
      <div class="notificaciones-lista" id="listaNotificaciones">
        <div class="notificacion">
          <div class="notificacion-titulo">Nuevo informe recibido</div>
          <div class="notificacion-contenido">
            Informe enviado del Área de Sistemas
          </div>
        </div>
        <div class="notificacion">
          <div class="notificacion-titulo">Nuevo informe recibido</div>
          <div class="notificacion-contenido">
            Informe enviado del Área de Marketing
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- este botón queda al extremo derecho -->
  <button onclick="cerrarSesion()" class="btn-cerrar">Cerrar sesión</button>
</nav>

<section id="inicio">
  <h1>Bienvenido, Jefe de Recursos Humanos</h1>

  <!-- Tabla de informes -->
  <div id="tabla_informes">
    <table>
      <thead>
        <tr>
          <th>Nombre del informe</th>
          <th>Fecha</th>
          <th>Área</th>
          <th>Estado</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="informe" items="${listaInformes}">
          <tr onclick="verDocumento('${informe.rutaDocumento}')" style="cursor:pointer">
            <td>${informe.asunto}</td>
            <td><fmt:formatDate value="${informe.fechaEnvio}" pattern="yyyy-MM-dd"/></td>
            <td>${informe.area}</td> <!-- o el campo correcto para área -->
            <td id="estado-informe-${informe.informeID}">${informe.estado}</td>
            <td>
              <button
                onclick="event.stopPropagation(); window.estadoActualId='estado-informe-${informe.informeID}'; window.informeId=${informe.informeID}; mostrarModal('modalConfirmacion')"
                class="btn-aprobar"
              >
                Aprobar
              </button>
              <button
                onclick="event.stopPropagation(); window.informeId=${informe.informeID}; mostrarModal('modalObservacion')"
                class="btn-rechazar"
              >
                Rechazar
              </button>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>

  <!-- Notificación general -->
  <div id="notificacionRRHH" class="notificacion-general"></div>

  <!-- Modal Documento -->
  <div id="modalDocumento" class="modal">
    <div class="modal-contenido modal-documento">
      <h3>Visualización del informe</h3>
      <div id="visorDocumento"></div>
      <div id="botonesModalDocumento">
        <button onclick="cerrarModal('modalDocumento')" class="btn-cerrar">
          Cerrar
        </button>
      </div>
    </div>
  </div>

  <!-- Modal Observaciones -->
  <div id="modalObservacion" class="modal">
    <div class="modal-contenido modal-observacion">
      <h3>Agregar observación</h3>
      <textarea id="observacionTexto" required></textarea>
      <div class="modal-botones">
        <button onclick="enviarObservacion()" class="btn-enviar">Enviar observaciones</button>
        <button onclick="cerrarModal('modalObservacion')" class="btn-cerrar">Cerrar</button>
      </div>
      <div id="notificacionObservacion" class="notificacion-general"></div>
    </div>
  </div>

  <!-- Modal Confirmación -->
  <div id="modalConfirmacion" class="modal">
    <div class="modal-contenido modal-confirmacion">
      <h3>¿Seguro de enviar el documento?</h3>
      <div id="botonesModalDocumento">
        <button onclick="aceptarInforme(this, window.estadoActualId)" class="btn-confirmar">Aceptar</button>
        <button onclick="cerrarModal('modalConfirmacion')" class="btn-cerrar">Cerrar</button>
      </div>
    </div>
  </div>
</section>

<script src="${pageContext.request.contextPath}/js/jefeRrhh.js"></script>
</body>
</html>
