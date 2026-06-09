/**
 * AuthContext — gestiona las credenciales Basic Auth en sesión.
 *
 * El backend usa Spring Security HTTP Basic: cada request necesita
 * el header  Authorization: Basic base64(usuario:contraseña)
 *
 * Las credenciales se guardan en sessionStorage (se borran al cerrar
 * la pestaña) y se inyectan automáticamente via el interceptor de Axios.
 */
import { createContext, useContext, useState, useCallback, useEffect } from 'react';

const STORAGE_KEY = 'taller_auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const raw = sessionStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  });

  const login = useCallback((usuario, nombre) => {
    const userData = { usuario, nombre };
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(userData));
    setUser(userData);
  }, []);

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

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
