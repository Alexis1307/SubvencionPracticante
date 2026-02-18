
function cerrarModal(id) {
    document.getElementById(id).style.display = "none";
}

function mostrarModalFirma(informeId) {
    document.getElementById("aprobar_informeId").value = informeId;
    document.getElementById("modalFirma").style.display = "block";
}

function mostrarModalRechazo(informeId) {
    document.getElementById("rechazo_informeId").value = informeId;
    document.getElementById("modalRechazo").style.display = "block";
}