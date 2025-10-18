const PanelPracticante = (() => {
  const state = {
    darkMode: localStorage.getItem('darkMode') === 'true',
    searchQuery: ''
  };

  const init = () => {
    initDarkMode();
    setupLogoutConfirmation();
    setupTableSearch();
    setupDynamicGreeting();
    setupTableInteractions();
    setupTooltips();
    animateProgressBars();
    setupBadgeColors();
    handleResponsiveMenu();
  };

  const initDarkMode = () => {
    const toggleBtn = document.getElementById('darkModeToggle');
    if (!toggleBtn) return;

    if (state.darkMode) {
      document.body.classList.add('dark-mode');
      updateDarkModeIcon(toggleBtn, true);
    }

    toggleBtn.addEventListener('click', () => {
      state.darkMode = !state.darkMode;
      document.body.classList.toggle('dark-mode');
      localStorage.setItem('darkMode', state.darkMode);
      updateDarkModeIcon(toggleBtn, state.darkMode);
      
      const icon = toggleBtn.querySelector('i');
      icon.style.transform = 'rotate(360deg)';
      setTimeout(() => {
        icon.style.transform = 'rotate(0deg)';
      }, 300);
    });
  };

  const updateDarkModeIcon = (button, isDark) => {
    const icon = button.querySelector('i');
    if (isDark) {
      icon.classList.remove('fa-moon');
      icon.classList.add('fa-sun');
    } else {
      icon.classList.remove('fa-sun');
      icon.classList.add('fa-moon');
    }
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

  const setupTableSearch = () => {
    const searchInput = document.getElementById('tableSearch');
    const table = document.getElementById('informesTable');
    
    if (!searchInput || !table) return;

    searchInput.addEventListener('input', (e) => {
      const query = e.target.value.toLowerCase();
      const rows = table.querySelectorAll('tbody tr');

      rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        const shouldShow = text.includes(query);
        
        row.style.display = shouldShow ? '' : 'none';
        
        if (shouldShow && query.length > 0) {
          row.style.animation = 'fadeInUp 0.3s ease-out';
        }
      });

      const visibleRows = Array.from(rows).filter(row => row.style.display !== 'none');
      if (visibleRows.length === 0 && query.length > 0) {
        showNoResultsMessage(table);
      } else {
        removeNoResultsMessage(table);
      }
    });
  };

  const showNoResultsMessage = (table) => {
    removeNoResultsMessage(table);
    
    const tbody = table.querySelector('tbody');
    const row = document.createElement('tr');
    row.id = 'noResultsRow';
    row.innerHTML = `
      <td colspan="6" class="text-center py-5">
        <i class="fas fa-search fa-3x text-muted mb-3" style="opacity: 0.3;"></i>
        <p class="text-muted mb-0">No se encontraron resultados</p>
      </td>
    `;
    tbody.appendChild(row);
  };

  const removeNoResultsMessage = (table) => {
    const existingRow = table.querySelector('#noResultsRow');
    if (existingRow) {
      existingRow.remove();
    }
  };

  const setupDynamicGreeting = () => {
    const greetingElement = document.getElementById('greeting');
    if (!greetingElement) return;

    const hour = new Date().getHours();
    let greeting = 'Hola';

    if (hour >= 5 && hour < 12) {
      greeting = 'Buenos días';
    } else if (hour >= 12 && hour < 19) {
      greeting = 'Buenas tardes';
    } else {
      greeting = 'Buenas noches';
    }

    const currentText = greetingElement.textContent;
    const userName = currentText.split(', ')[1];
    greetingElement.textContent = `${greeting}, ${userName}`;
  };

  const setupTableInteractions = () => {
    const rows = document.querySelectorAll('#informesTable tbody tr');
    
    rows.forEach(row => {
      row.addEventListener('mouseenter', () => {
        row.style.transition = 'all 0.3s ease';
      });

      row.addEventListener('click', (e) => {
        if (e.target.closest('.btn-group')) return;
        
        const rowIndex = Array.from(rows).indexOf(row) + 1;
        console.log(`Fila ${rowIndex} seleccionada`);
      });
    });
  };

  const setupTooltips = () => {
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    if (tooltipTriggerList.length === 0) return;

    tooltipTriggerList.forEach(element => {
      new bootstrap.Tooltip(element, {
        trigger: 'hover',
        placement: 'top'
      });
    });
  };

  const animateProgressBars = () => {
    const progressBars = document.querySelectorAll('.progress-bar');
    
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const bar = entry.target;
          const width = bar.style.width;
          bar.style.width = '0%';
          
          setTimeout(() => {
            bar.style.width = width;
          }, 100);
          
          observer.unobserve(bar);
        }
      });
    }, { threshold: 0.5 });

    progressBars.forEach(bar => observer.observe(bar));
  };

  const setupBadgeColors = () => {
    const badges = document.querySelectorAll('.status-badge');
    
    badges.forEach(badge => {
      const status = badge.textContent.toLowerCase().trim();
      
      badge.classList.remove('bg-success', 'bg-warning', 'bg-danger', 'bg-info', 'bg-secondary');
      
      if (status.includes('enviado') || status.includes('aprobado')) {
        badge.classList.add('bg-success');
      } else if (status.includes('pendiente') || status.includes('revisión')) {
        badge.classList.add('bg-warning');
      } else if (status.includes('rechazado') || status.includes('cancelado')) {
        badge.classList.add('bg-danger');
      } else if (status.includes('proceso')) {
        badge.classList.add('bg-info');
      } else {
        badge.classList.add('bg-secondary');
      }
    });
  };

  const handleResponsiveMenu = () => {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    let lastScroll = 0;
    window.addEventListener('scroll', () => {
      const currentScroll = window.pageYOffset;

      if (currentScroll <= 0) {
        navbar.classList.remove('shadow-lg');
      } else {
        navbar.classList.add('shadow-lg');
      }

      lastScroll = currentScroll;
    });
  };

  const updateStatsNumbers = () => {
    const numbers = document.querySelectorAll('.stats-number');
    
    numbers.forEach(number => {
      const target = parseInt(number.textContent);
      let current = 0;
      const increment = target / 50;
      const duration = 1000;
      const stepTime = duration / 50;

      const counter = setInterval(() => {
        current += increment;
        if (current >= target) {
          number.textContent = target;
          clearInterval(counter);
        } else {
          number.textContent = Math.floor(current);
        }
      }, stepTime);
    });
  };

  const addTableRowActions = () => {
    const actionButtons = document.querySelectorAll('.btn-group .btn');
    
    actionButtons.forEach(button => {
      button.addEventListener('click', (e) => {
        e.stopPropagation();
        
        const icon = button.querySelector('i');
        if (icon) {
          if (icon.classList.contains('fa-eye')) {
            console.log('Ver detalles del informe');
          } else if (icon.classList.contains('fa-download')) {
            console.log('Descargar informe');
          } else if (icon.classList.contains('fa-edit')) {
            console.log('Editar informe');
          }
        }

        button.style.transform = 'scale(0.95)';
        setTimeout(() => {
          button.style.transform = '';
        }, 150);
      });
    });
  };

  const initializeEventListeners = () => {
    document.querySelectorAll('.stats-card').forEach(card => {
      card.addEventListener('mouseenter', () => {
        card.style.transform = 'translateY(-5px)';
      });
      
      card.addEventListener('mouseleave', () => {
        card.style.transform = 'translateY(0)';
      });
    });

    const elaborarBtn = document.querySelector('a[href*="elaborar_informe"]');
    if (elaborarBtn) {
      elaborarBtn.addEventListener('click', (e) => {
        elaborarBtn.style.transform = 'scale(0.98)';
        setTimeout(() => {
          elaborarBtn.style.transform = '';
        }, 150);
      });
    }
  };

  return {
    init: () => {
      init();
      addTableRowActions();
      initializeEventListeners();
      
      setTimeout(() => {
        updateStatsNumbers();
      }, 500);
    }
  };
})();

document.addEventListener('DOMContentLoaded', () => {
  PanelPracticante.init();
});

function verDocumento(basePath, archivo, tipo) {
    const url = `${basePath}/verInforme?archivo=${encodeURIComponent(archivo)}&tipo=${tipo}`;
    const visor = document.getElementById('visorDocumento');
    visor.innerHTML = `<iframe src="${url}" width="100%" height="100%"></iframe>`;
    document.getElementById('modalDocumento').style.display = 'block';
	console.log()
}



function cerrarModal(idModal) {
    const modal = document.getElementById(idModal);
    if (modal) modal.style.display = 'none';
}