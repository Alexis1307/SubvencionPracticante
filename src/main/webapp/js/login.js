class LoginValidator {
  constructor() {
    this.form = document.getElementById('loginForm');
    this.emailInput = document.getElementById('correo');
    this.passwordInput = document.getElementById('clave');
    this.errorBox = document.getElementById('errorBox');
    this.submitButton = this.form.querySelector('button[type="submit"]');
    this.togglePasswordButton = document.getElementById('togglePassword');

    this.init();
  }

  init() {
    this.form.setAttribute('novalidate', '');
    this.attachEvents();
    this.createParticles();
  }

  attachEvents() {
    this.form.addEventListener('submit', (e) => this.handleSubmit(e));
    
    this.emailInput.addEventListener('input', () => this.clearFieldError(this.emailInput));
    this.passwordInput.addEventListener('input', () => this.clearFieldError(this.passwordInput));

    if (this.togglePasswordButton) {
      this.togglePasswordButton.addEventListener('click', () => this.togglePasswordVisibility());
    }

    this.emailInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        this.passwordInput.focus();
      }
    });

    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
      setTimeout(() => {
        alert.style.animation = 'fadeOut 0.3s ease-out forwards';
        setTimeout(() => alert.remove(), 300);
      }, 5000);
    });
  }

  handleSubmit(e) {
    e.preventDefault();

    const errors = this.validateForm();

    if (errors.length > 0) {
      this.displayErrors(errors);
      this.shakeForm();
    } else {
      this.hideErrors();
      this.submitForm();
    }
  }

  validateForm() {
    const errors = [];
    this.clearAllErrors();

    const email = this.emailInput.value.trim();
    const password = this.passwordInput.value.trim();

    if (!email) {
      errors.push({ field: this.emailInput, message: 'El correo electrónico es obligatorio' });
    } else if (!this.isValidEmail(email)) {
      errors.push({ field: this.emailInput, message: 'El formato del correo es inválido' });
    }

    if (!password) {
      errors.push({ field: this.passwordInput, message: 'La contraseña es obligatoria' });
    } else if (password.length < 6) {
      errors.push({ field: this.passwordInput, message: 'La contraseña debe tener al menos 6 caracteres' });
    }

    errors.forEach(error => this.markFieldInvalid(error.field));

    return errors;
  }

  isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email.toLowerCase());
  }

  displayErrors(errors) {
    const errorMessages = errors.map(error => `<li>${error.message}</li>`).join('');
    this.errorBox.innerHTML = `<ul>${errorMessages}</ul>`;
    this.errorBox.style.display = 'block';
    this.errorBox.setAttribute('role', 'alert');
    this.errorBox.setAttribute('aria-live', 'assertive');
  }

  hideErrors() {
    this.errorBox.innerHTML = '';
    this.errorBox.style.display = 'none';
    this.errorBox.removeAttribute('role');
    this.errorBox.removeAttribute('aria-live');
  }

  markFieldInvalid(field) {
    field.classList.add('is-invalid');
    field.setAttribute('aria-invalid', 'true');
  }

  clearFieldError(field) {
    field.classList.remove('is-invalid');
    field.removeAttribute('aria-invalid');
    
    if (this.errorBox.style.display === 'block') {
      this.hideErrors();
    }
  }

  clearAllErrors() {
    this.emailInput.classList.remove('is-invalid');
    this.passwordInput.classList.remove('is-invalid');
    this.emailInput.removeAttribute('aria-invalid');
    this.passwordInput.removeAttribute('aria-invalid');
  }

  shakeForm() {
    this.form.style.animation = 'shake 0.5s ease-in-out';
    setTimeout(() => {
      this.form.style.animation = '';
    }, 500);
  }

  submitForm() {
    this.submitButton.classList.add('loading');
    this.submitButton.disabled = true;

    setTimeout(() => {
      this.form.submit();
    }, 500);
  }

  togglePasswordVisibility() {
    const type = this.passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    this.passwordInput.setAttribute('type', type);

    const icon = this.togglePasswordButton.querySelector('i');
    if (type === 'text') {
      icon.classList.remove('fa-eye');
      icon.classList.add('fa-eye-slash');
      this.togglePasswordButton.setAttribute('aria-label', 'Ocultar contraseña');
    } else {
      icon.classList.remove('fa-eye-slash');
      icon.classList.add('fa-eye');
      this.togglePasswordButton.setAttribute('aria-label', 'Mostrar contraseña');
    }
  }

  createParticles() {
    const particlesContainer = document.querySelector('.particles');
    if (!particlesContainer) return;

    const particleCount = 30;

    for (let i = 0; i < particleCount; i++) {
      const particle = document.createElement('div');
      particle.style.position = 'absolute';
      particle.style.width = `${Math.random() * 6 + 2}px`;
      particle.style.height = particle.style.width;
      particle.style.background = `rgba(255, 255, 255, ${Math.random() * 0.5 + 0.3})`;
      particle.style.borderRadius = '50%';
      particle.style.left = `${Math.random() * 100}%`;
      particle.style.top = `${Math.random() * 100}%`;
      particle.style.animation = `floatParticle ${Math.random() * 10 + 10}s linear infinite`;
      particle.style.animationDelay = `${Math.random() * 5}s`;

      particlesContainer.appendChild(particle);
    }

    const style = document.createElement('style');
    style.textContent = `
      @keyframes floatParticle {
        0% {
          opacity: 0;
          transform: translateY(0) translateX(0);
        }
        10% {
          opacity: 1;
        }
        90% {
          opacity: 1;
        }
        100% {
          opacity: 0;
          transform: translateY(-1000px) translateX(${Math.random() * 200 - 100}px);
        }
      }
      @keyframes shake {
        0%, 100% { transform: translateX(0); }
        10%, 30%, 50%, 70%, 90% { transform: translateX(-8px); }
        20%, 40%, 60%, 80% { transform: translateX(8px); }
      }
      @keyframes fadeOut {
        from { opacity: 1; transform: translateY(0); }
        to { opacity: 0; transform: translateY(-10px); }
      }
    `;
    document.head.appendChild(style);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  new LoginValidator();
});