let informeIdActual = null;
    let estadoIdActual = null;

    function aprobarInforme(informeId, estadoId) {
      fetch('/procesarInforme', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
          informeId: informeId,
          accion: 'aprobar'
        })
      })
      .then(res => res.text())
      .then(msg => {
        document.getElementById(estadoId).innerText = "Aprobado";
        alert("Informe aprobado correctamente.");
      })
      .catch(err => {
        console.error(err);
        alert("Error al aprobar el informe.");
      });
    }

    function mostrarModalRechazo(informeId, estadoId) {
      informeIdActual = informeId;
      estadoIdActual = estadoId;
      document.getElementById('modalObservacion').style.display = 'block';
    }

    function enviarRechazo() {
      const comentario = document.getElementById('observacionTexto').value.trim();
      if (comentario === "") {
        alert("Debes ingresar una observación.");
        return;
      }

      fetch('/procesarInforme', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
          informeId: informeIdActual,
          accion: 'rechazar',
          comentario: comentario
        })
      })
      .then(res => res.text())
      .then(msg => {
        document.getElementById(estadoIdActual).innerText = "Rechazado";
        cerrarModal('modalObservacion');
        alert("Informe rechazado con observación.");
      })
      .catch(err => {
        console.error(err);
        alert("Error al rechazar el informe.");
      });
    }

    function cerrarModal(idModal) {
      document.getElementById(idModal).style.display = 'none';
    }

    function cerrarSesion() {
      // Puedes hacer logout por fetch o redirección
      window.location.href = "/logout";
    }