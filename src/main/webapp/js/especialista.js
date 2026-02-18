    function prepararEnvio() {
        const checkboxes = document.querySelectorAll('.informe-check:checked');
        const ids = Array.from(checkboxes).map(cb => cb.value);
        
        if (ids.length === 0) {
            alert("Por favor selecciona al menos un informe para generar la planilla.");
            return false; // no envía el formulario
        }

        document.getElementById("idsSeleccionados").value = ids.join(",");
        return true; // envía el formulario
    }

    function toggleAll(source) {
        const checkboxes = document.querySelectorAll('.informe-check');
        checkboxes.forEach(cb => cb.checked = source.checked);
    }
