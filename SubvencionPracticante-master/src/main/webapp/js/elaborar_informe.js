(() => {
    const formInforme = document.getElementById('formInforme');
    const asuntoInput = document.getElementById('asunto');
    const periodoSelect = document.getElementById('periodo');
    const actividadesTextarea = document.getElementById('actividades');
    const submitBtn = document.getElementById('submitBtn');
    const charCount = document.getElementById('charCount');

    const showError = (element, message) => {
        element.classList.add('is-invalid');
        const feedback = element.nextElementSibling;
        if (feedback && feedback.classList.contains('invalid-feedback')) {
            feedback.textContent = message;
        }
    };

    const hideError = (element) => {
        element.classList.remove('is-invalid');
    };

    const validateAsunto = () => {
        const value = asuntoInput.value.trim();
        if (value.length === 0) {
            showError(asuntoInput, 'El asunto es obligatorio');
            return false;
        } else if (value.length < 5) {
            showError(asuntoInput, 'El asunto debe tener al menos 5 caracteres');
            return false;
        } else if (value.length > 200) {
            showError(asuntoInput, 'El asunto no puede exceder 200 caracteres');
            return false;
        }
        hideError(asuntoInput);
        return true;
    };

    const validatePeriodo = () => {
        const value = periodoSelect.value;
        if (!value || value === '') {
            showError(periodoSelect, 'Debe seleccionar un periodo');
            return false;
        }
        hideError(periodoSelect);
        return true;
    };

    const validateActividades = () => {
        const value = actividadesTextarea.value.trim();
        if (value.length === 0) {
            showError(actividadesTextarea, 'Las actividades son obligatorias');
            return false;
        } else if (value.length < 20) {
            showError(actividadesTextarea, 'Debe describir las actividades con al menos 20 caracteres');
            return false;
        } else if (value.length > 2000) {
            showError(actividadesTextarea, 'Las actividades no pueden exceder 2000 caracteres');
            return false;
        }
        hideError(actividadesTextarea);
        return true;
    };

    const validateForm = () => {
        const isAsuntoValid = validateAsunto();
        const isPeriodoValid = validatePeriodo();
        const isActividadesValid = validateActividades();
        return isAsuntoValid && isPeriodoValid && isActividadesValid;
    };

    const updateCharCounter = () => {
        const count = actividadesTextarea.value.length;
        charCount.textContent = count;
        if (count > 2000) {
            charCount.style.color = '#dc3545';
        } else if (count >= 1800) {
            charCount.style.color = '#f59e0b';
        } else {
            charCount.style.color = '#6c757d';
        }
    };

    const handlePeriodoChange = () => {
        const value = periodoSelect.value;
        if (value === '6') {
            const option = periodoSelect.querySelector('option[value="6"]');
            if (option && !option.textContent.includes('Final')) {
                option.textContent = '6 mes (Final)';
            }
        }
    };

    const showSpinner = () => {
        const btnText = submitBtn.querySelector('.btn-text');
        const btnSpinner = submitBtn.querySelector('.btn-spinner');
        if (btnText && btnSpinner) {
            btnText.style.display = 'none';
            btnSpinner.style.display = 'inline-flex';
        }
        submitBtn.disabled = true;
    };

    const hideSpinner = () => {
        const btnText = submitBtn.querySelector('.btn-text');
        const btnSpinner = submitBtn.querySelector('.btn-spinner');
        if (btnText && btnSpinner) {
            btnText.style.display = 'inline-flex';
            btnSpinner.style.display = 'none';
        }
        submitBtn.disabled = false;
    };

    const fadeOutForm = () => {
        formInforme.style.transition = 'opacity 0.5s ease';
        formInforme.style.opacity = '0';
        setTimeout(() => {
            formInforme.style.opacity = '1';
        }, 600);
    };

    const clearForm = () => {
        asuntoInput.value = '';
        periodoSelect.value = '';
        actividadesTextarea.value = '';
        updateCharCounter();
        hideError(asuntoInput);
        hideError(periodoSelect);
        hideError(actividadesTextarea);
    };

    asuntoInput.addEventListener('input', () => {
        if (asuntoInput.value.trim().length > 0) {
            hideError(asuntoInput);
        }
    });

    asuntoInput.addEventListener('blur', validateAsunto);

    periodoSelect.addEventListener('change', () => {
        hideError(periodoSelect);
        handlePeriodoChange();
    });

    periodoSelect.addEventListener('blur', validatePeriodo);

    actividadesTextarea.addEventListener('input', () => {
        updateCharCounter();
        if (actividadesTextarea.value.trim().length >= 20) {
            hideError(actividadesTextarea);
        }
    });

    actividadesTextarea.addEventListener('blur', validateActividades);

    formInforme.addEventListener('submit', (e) => {
        e.preventDefault();
        
        if (validateForm()) {
            showSpinner();
            
            setTimeout(() => {
                formInforme.submit();
            }, 300);
        } else {
            const firstInvalid = formInforme.querySelector('.is-invalid');
            if (firstInvalid) {
                firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstInvalid.focus();
            }
        }
    });

    window.addEventListener('load', () => {
        const formWrapper = document.querySelector('.form-wrapper');
        if (formWrapper) {
            formWrapper.style.opacity = '0';
            formWrapper.style.transform = 'translateY(30px)';
            
            setTimeout(() => {
                formWrapper.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
                formWrapper.style.opacity = '1';
                formWrapper.style.transform = 'translateY(0)';
            }, 100);
        }
    });

    updateCharCounter();
})();