document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    const requiresAuth = ['/form', '/map'].some(path => window.location.pathname.includes(path));

    if (requiresAuth) {
        const username = localStorage.getItem('username');
        const token = localStorage.getItem('authToken');

        if (!username || !token) {
            if (!window.location.pathname.includes('/login')) {
                alert('No autenticado. Redirigiendo al inicio de sesión.');
                window.location.href = '/login';
            }
        }
    }
});

// Handle login form submission
function handleLogin(event) {
    event.preventDefault(); // Prevent page reload

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Nombre de usuario o contraseña incorrectos.');
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                localStorage.setItem('username', username);
                localStorage.setItem('authToken', data.token); // Store a placeholder token (or user ID)
                window.location.href = '/'; // Redirect to the main page
            } else {
                alert(data.error || 'Nombre de usuario o contraseña incorrectos.');
            }
        })
        .catch(error => {
            console.error('Error al iniciar sesión:', error);
            alert('Error al conectar con el servidor.');
        });
}


// Sync offline forms when back online
window.addEventListener('online', function () {
    const offlineForms = JSON.parse(localStorage.getItem('offlineForms') || '[]');

    if (offlineForms.length > 0) {
        alert('Conexión restablecida. Sincronizando datos...');

        offlineForms.forEach(form => {
            fetch('/submitForm', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(form)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        console.log('Formulario sincronizado correctamente:', form);
                    } else {
                        console.error('Error al sincronizar formulario:', form);
                    }
                })
                .catch(error => console.error('Error durante la sincronización:', error));
        });

        localStorage.removeItem('offlineForms'); // Clear offline forms after syncing
    }
});
