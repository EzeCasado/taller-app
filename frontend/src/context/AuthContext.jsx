/**
 * AuthContext — gestiona las credenciales Basic Auth en sesión.
 *
 * El backend usa Spring Security HTTP Basic: cada request necesita
 * el header  Authorization: Basic base64(usuario:contraseña)
 *
 * Las credenciales se guardan en sessionStorage (se borran al cerrar
 * la pestaña) y se inyectan automáticamente via el interceptor de Axios.
 */
import { createContext, useContext, useState, useCallback } from 'react';

const STORAGE_KEY = 'taller_auth';

/**
 * Contexto de Autenticación de la aplicación.
 * Proporciona el estado global del usuario logueado y métodos para iniciar/cerrar sesión.
 */
const AuthContext = createContext(null);

/**
 * Proveedor de Autenticación que envuelve la aplicación.
 * Maneja la persistencia de la sesión mediante localStorage y configura 
 * las credenciales globales de Axios.
 *
 * @param {Object} props - Propiedades del componente (children).
 * @returns {JSX.Element} Proveedor del contexto de autenticación.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const raw = sessionStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  });

  /**
   * Función para iniciar sesión en la aplicación.
   * Realiza la llamada a la API, decodifica la respuesta y persiste el usuario en el localStorage.
   *
   * @param {string} usuario - El nombre de usuario.
   * @param {string} nombre - El nombre del usuario.
   * @returns {Promise<void>} 
   */
  const login = useCallback(async (usuario, nombre) => {
    try {
      const response = await import('../api/empleados').then(m => m.obtenerUsuarioActual());
      const userData = { usuario, nombre: response.data.nombre, rol: response.data.rol };
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(userData));
      setUser(userData);
    } catch (error) {
      console.error("Error al obtener datos del usuario actual:", error);
      const fallbackData = { usuario, nombre, rol: 'MECANICO' };
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(fallbackData));
      setUser(fallbackData);
    }
  }, []);

  /**
   * Función para cerrar sesión.
   * Limpia el estado local, remueve los tokens del localStorage y resetea las cabeceras de Axios.
   */
  const logout = useCallback(() => {
    sessionStorage.removeItem(STORAGE_KEY);
    // Limpiamos también la credencial guardada para Axios
    sessionStorage.removeItem('taller_credentials');
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook personalizado para acceder fácilmente al Contexto de Autenticación.
 * 
 * @returns {Object} El estado y las funciones del contexto de autenticación (user, login, logout, etc).
 */
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
