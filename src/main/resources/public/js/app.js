// Verificar si el usuario está autenticado al cargar la página
document.addEventListener('DOMContentLoaded', function () {
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('authToken');

    if (!username || !token) {
        alert('No autenticado. Redirigiendo al inicio de sesión.');
        window.location.href = '/login';
    } else {
        // Verificar con el servidor si el token es válido
        fetch('/verify-token', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ token: token })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Token no válido. Redirigiendo al inicio de sesión.');
                }
            })
            .catch(error => {
                console.error('Error al verificar el token:', error);
                window.location.href = '/login';
            });
    }
});

// Manejar el evento de inicio de sesión
function handleLogin(event) {
    event.preventDefault(); // Evitar la recarga de la página

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Enviar la solicitud de inicio de sesión al servidor
    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `username=${username}&password=${password}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Guardar la información del usuario en Local Storage
                localStorage.setItem('username', username);
                localStorage.setItem('authToken', data.token);
                alert('Usuario autenticado con éxito.');
                window.location.href = '/'; // Redirigir a la página principal
            } else {
                alert('Nombre de usuario o contraseña incorrectos.');
            }
        })
        .catch(error => {
            console.error('Error al iniciar sesión:', error);
            alert('Error al conectar con el servidor.');
        });
}

// Manejar la sincronización de datos cuando la conexión se restablece
window.addEventListener('online', function () {
    alert('Conexión restablecida. Sincronizando datos...');
    const offlineForms = JSON.parse(localStorage.getItem('offlineForms') || '[]');

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
            });
    });

    // Limpiar formularios offline después de la sincronización
    localStorage.removeItem('offlineForms');
});

// Manejar el envío del formulario de captura de información
function handleFormSubmit(event) {
    event.preventDefault(); // Evitar envío de formulario por defecto

    const formData = {
        name: document.getElementById('name').value,
        sector: document.getElementById('sector').value,
        schoolLevel: document.getElementById('schoolLevel').value,
        geoPosition: document.getElementById('geoPosition').value,
        imageBase64: document.getElementById('imageBase64').value
    };

    if (navigator.onLine) {
        // Si estamos en línea, enviar el formulario al servidor
        fetch('/submitForm', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Formulario enviado correctamente.');
                } else {
                    alert('Error al enviar formulario.');
                }
            })
            .catch(error => {
                console.error('Error al enviar formulario:', error);
            });
    } else {
        // Si estamos offline, guardar el formulario en Local Storage
        let offlineForms = JSON.parse(localStorage.getItem('offlineForms') || '[]');
        offlineForms.push(formData);
        localStorage.setItem('offlineForms', JSON.stringify(offlineForms));
        alert('Sin conexión. El formulario se guardará localmente.');
    }
}
