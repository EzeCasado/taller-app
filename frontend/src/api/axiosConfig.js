import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor — inyecta Basic Auth desde sessionStorage
api.interceptors.request.use(
  (config) => {
    const encoded = sessionStorage.getItem('taller_credentials');
    if (encoded) {
      config.headers['Authorization'] = `Basic ${encoded}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor — normalizar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 401 = credenciales incorrectas o sesión expirada
    if (error.response?.status === 401) {
      sessionStorage.removeItem('taller_credentials');
      sessionStorage.removeItem('taller_auth');
      // Forzar reload para que el router redirija al login
      window.location.href = '/';
    }

    const message =
      error.response?.data?.message ||
      error.response?.data ||
      error.message ||
      'Error de conexión con el servidor';
    return Promise.reject(new Error(message));
  }
);

/**
 * Guarda las credenciales codificadas en Base64 para Basic Auth.
 * Debe llamarse justo después de un login exitoso.
 */
export function saveCredentials(usuario, contrasenia) {
  const encoded = btoa(`${usuario}:${contrasenia}`);
  sessionStorage.setItem('taller_credentials', encoded);
}

export default api;
