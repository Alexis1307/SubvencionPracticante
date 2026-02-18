function verDocumento(idInforme) {
    const contextPath = document.body.getAttribute('data-context-path') || '';
    const url = `${contextPath}/verDocumento?idInforme=${idInforme}`;
    const iframe = document.getElementById("iframeInforme");
    if (iframe) {
        iframe.src = url;
        const modal = new bootstrap.Modal(document.getElementById('modalVerInforme'));
        modal.show();
    } else {
        window.open(url, "_blank");
    }
}