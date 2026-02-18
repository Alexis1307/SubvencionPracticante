const JefeUnidadModule = (() => {
    const SELECTORS = {
        TABLE_BODY: '#tabla-informes tbody',
        ACTION_BUTTON: '[data-action]',
        ROW: 'tr'
    };

    const ACTIONS = {
        APROBAR: 'aprobar',
        RECHAZAR: 'rechazar',
        VER: 'ver'
    };

    const ROW_STATES = {
        IDLE: 'idle',
        PROCESSING: 'processing',
        SUCCESS: 'success',
        REJECTED: 'rejected'
    };

    const nodes = {};

    function showNotification(message, type = 'info') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show notification-alert`;
        alertDiv.setAttribute('role', 'alert');
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        `;

        const container = document.querySelector('.container');
        if (container) {
            container.insertBefore(alertDiv, container.firstChild);

            setTimeout(() => {
                alertDiv.classList.remove('show');
                setTimeout(() => alertDiv.remove(), 300);
            }, 5000);
        }
    }

    function setRowState(row, state) {
        if (!row) return;

        row.classList.remove('processing-row', 'success-row', 'rejected-row', 'idle-row');

        const buttons = row.querySelectorAll(SELECTORS.ACTION_BUTTON);
        const badge = row.querySelector('.badge');

        const stateConfig = {
            [ROW_STATES.PROCESSING]: {
                className: 'processing-row',
                badgeText: 'Procesando...',
                badgeClass: 'bg-secondary'
            },
            [ROW_STATES.SUCCESS]: {
                className: 'success-row',
                badgeText: 'Aprobado',
                badgeClass: 'bg-success'
            },
            [ROW_STATES.REJECTED]: {
                className: 'rejected-row',
                badgeText: 'Rechazado',
                badgeClass: 'bg-danger'
            },
            [ROW_STATES.IDLE]: {
                className: 'idle-row',
                badgeText: null,
                badgeClass: null
            }
        };

        const config = stateConfig[state] || stateConfig[ROW_STATES.IDLE];
        row.classList.add(config.className);

        buttons.forEach(btn => {
            btn.disabled = state === ROW_STATES.PROCESSING || 
                          state === ROW_STATES.SUCCESS || 
                          state === ROW_STATES.REJECTED;
            btn.classList.toggle('btn-disabled', btn.disabled);
        });

        if (badge && config.badgeText !== null) {
            badge.innerHTML = `<i class="fas fa-sync fa-spin me-1"></i>${config.badgeText}`;
            if (state !== ROW_STATES.PROCESSING) {
                badge.innerHTML = config.badgeText;
            }
            badge.className = `badge ${config.badgeClass}`;
        }
    }

    function handleAction(button) {
        if (!button) return;

        const action = button.dataset.action;
        const id = button.dataset.id;
        const row = button.closest(SELECTORS.ROW);

        if (!action || !id || !row) {
            console.error('Datos incompletos para la acción');
            return;
        }

        if (action === ACTIONS.VER) {
            window.location.href = `${contextPath}/verInforme?id=${id}`;
            return;
        }

        const confirmMessage = action === ACTIONS.APROBAR 
            ? '¿Está seguro de aprobar este informe?' 
            : '¿Está seguro de rechazar este informe?';

        if (!confirm(confirmMessage)) return;

        const originalBtnText = button.innerHTML;
        setRowState(row, ROW_STATES.PROCESSING);
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>';

        fetch(`${contextPath}/jefeUnidad`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                action: action,
                id: id
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error del servidor: ${response.status} ${response.statusText}`);
            }
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return response.json();
            } else {
                return response.text().then(() => ({
                    success: false,
                    message: 'Respuesta inesperada del servidor'
                }));
            }
        })
        .then(result => {
            if (result.success) {
                const finalState = action === ACTIONS.APROBAR 
                    ? ROW_STATES.SUCCESS 
                    : ROW_STATES.REJECTED;
                
                setRowState(row, finalState);

                const msg = action === ACTIONS.APROBAR 
                    ? '<i class="fas fa-check-circle me-2"></i>Informe aprobado exitosamente' 
                    : '<i class="fas fa-times-circle me-2"></i>Informe rechazado exitosamente';

                showNotification(msg, 'success');

                setTimeout(() => {
                    row.style.transition = 'opacity 0.5s ease';
                    row.style.opacity = '0';
                    setTimeout(() => row.remove(), 500);
                }, 2000);
            } else {
                throw new Error(result.message || 'La operación no se completó correctamente');
            }
        })
        .catch(error => {
            console.error(`Error en la acción ${action}:`, error);
            showNotification(
                `<i class="fas fa-exclamation-triangle me-2"></i>Error: ${error.message}`, 
                'danger'
            );
            setRowState(row, ROW_STATES.IDLE);
            button.innerHTML = originalBtnText;
        });
    }

    function setupEventListeners() {
        const tableBody = document.querySelector(SELECTORS.TABLE_BODY);
        if (!tableBody) {
            console.warn('Tabla de informes no encontrada');
            return;
        }

        tableBody.addEventListener('click', e => {
            const button = e.target.closest(SELECTORS.ACTION_BUTTON);
            if (button && !button.disabled) {
                e.preventDefault();
                handleAction(button);
            }
        });
    }

    function init() {
        const tableBody = document.querySelector(SELECTORS.TABLE_BODY);
        if (!tableBody) {
            console.warn('Módulo JefeUnidad: tabla no encontrada en esta página');
            return;
        }
        nodes.tableBody = tableBody;
        setupEventListeners();
        console.log('JefeUnidadModule inicializado correctamente');
    }

    return {
        init,
        handleAction,
        setRowState
    };
})();

document.addEventListener('DOMContentLoaded', () => JefeUnidadModule.init());

document.addEventListener('DOMContentLoaded', () => {
    // Botones aprobar
    document.querySelectorAll('.btn-action-aprobar').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            document.getElementById('modalAprobarInformeId').value = id;
            const modal = new bootstrap.Modal(document.getElementById('modalAprobar'));
            modal.show();
        });
    });
	
	
    // Botones rechazar
    document.querySelectorAll('.btn-action-rechazar').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            const comentario = prompt("Ingrese observaciones para el rechazo:");
            if (!comentario) {
                return;
            }
            // Hacer submit mediante formulario oculto
            const form = document.createElement('form');
            form.method = 'post';
            form.action = contextPath + '/procesarInforme';
            // campos
            const f1 = document.createElement('input');
            f1.type = 'hidden'; f1.name = 'informeId'; f1.value = id;
            form.appendChild(f1);
            const f2 = document.createElement('input');
            f2.type = 'hidden'; f2.name = 'accion'; f2.value = 'rechazar';
            form.appendChild(f2);
            const f3 = document.createElement('input');
            f3.type = 'hidden'; f3.name = 'comentario'; f3.value = comentario;
            form.appendChild(f3);
            document.body.appendChild(form);
            form.submit();
        });
    });
});

window.addEventListener('DOMContentLoaded', (event) => {
	    if (abrirModalFirma === 'true') {
	        document.getElementById('modalFirma').style.display = 'block';
	    }
	});
	
	if (abrirModalFirma === 'true') {
	    mostrarModalFirma(informeIdModal);
	}


    function mostrarModalFirma(informeId) {
        document.getElementById('firma_informeId').value = informeId;
        document.getElementById('modalFirma').style.display = 'flex';
    }

    function mostrarModalRechazo(informeId) {
        document.getElementById('rechazo_informeId').value = informeId;
        document.getElementById('modalRechazo').style.display = 'flex';
    }

    function cerrarModal(id) {
        document.getElementById(id).style.display = 'none';

        if (id === 'modalDocumento') {
            document.getElementById('visorDocumento').src = '';
        }
    }
	
	const init = () => {
	    setupLogoutConfirmation();
	  };
	  
	const setupLogoutConfirmation = () => {
	    const logoutBtn = document.getElementById('logoutBtn');
	    if (!logoutBtn) return;

	    const form = logoutBtn.closest('form');
	    form.addEventListener('submit', (e) => {
	      const confirmed = confirm('¿Estás seguro de que deseas cerrar sesión?');
	      if (!confirmed) {
	        e.preventDefault();
	      }
	    });
	  };
