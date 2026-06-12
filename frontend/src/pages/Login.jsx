import { useState } from 'react';
import { Gauge, User, Lock, Eye, EyeOff, AlertCircle, ShieldCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { saveCredentials } from '../api/axiosConfig';
import api from '../api/axiosConfig';
import '../styles/login.css';

/**
 * Componente React: Login.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de Login
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente Login.
 */
export default function Login() {
  const { login } = useAuth();
  const [form, setForm] = useState({ usuario: '', contrasenia: '' });
  const [showPass, setShowPass] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
    if (error) setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.usuario.trim() || !form.contrasenia.trim()) {
      setError('Completá usuario y contraseña para continuar.');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      saveCredentials(form.usuario, form.contrasenia);
      // login() ahora hace el fetch a /me para validar y obtener nombre y rol
      await login(form.usuario, form.usuario);
    } catch (err) {
      sessionStorage.removeItem('taller_credentials');
      if (err.message?.includes('401') || err.message?.toLowerCase().includes('unauthorized')) {
        setError('Usuario o contraseña incorrectos o cuenta inactiva.');
      } else if (err.message?.toLowerCase().includes('network') || err.message?.toLowerCase().includes('conexión')) {
        setError('No se pudo conectar con el servidor. ¿Está corriendo Spring Boot?');
      } else {
        setError('Credenciales incorrectas o servidor no disponible.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page" role="main">
      <div className="login-card">

        {/* Logo */}
        <div className="login-logo">
          <div className="login-logo-icon" aria-hidden="true">
            <Gauge size={26} color="white" />
          </div>
          <div className="login-logo-text">
            <h1>TallerApp</h1>
            <p>Panel de Gestión de Taller</p>
          </div>
        </div>

        <div className="login-divider" />

        <div className="login-heading">
          <h2>Bienvenido de nuevo</h2>
          <p>Ingresá con tu usuario y contraseña para acceder al sistema.</p>
        </div>

        {/* Error banner */}
        {error && (
          <div className="login-error" role="alert" aria-live="assertive">
            <AlertCircle size={18} />
            <p>{error}</p>
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} noValidate id="login-form">
          <div className="form-group">
            <label className="form-label" htmlFor="login-usuario">Usuario</label>
            <div className="input-icon-wrapper">
              <User size={16} />
              <input
                id="login-usuario"
                name="usuario"
                className="form-input"
                placeholder="Tu nombre de usuario"
                value={form.usuario}
                onChange={handleChange}
                autoComplete="username"
                autoFocus
                disabled={loading}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="login-contrasenia">Contraseña</label>
            <div className="input-icon-wrapper">
              <Lock size={16} />
              <input
                id="login-contrasenia"
                name="contrasenia"
                type={showPass ? 'text' : 'password'}
                className="form-input"
                placeholder="••••••••"
                value={form.contrasenia}
                onChange={handleChange}
                autoComplete="current-password"
                disabled={loading}
              />
              <button
                type="button"
                onClick={() => setShowPass((v) => !v)}
                aria-label={showPass ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                tabIndex={-1}
                style={{
                  position: 'absolute', right: 12, background: 'none', border: 'none',
                  cursor: 'pointer', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center',
                }}
              >
                {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>

          <button id="btn-ingresar" className="login-btn" type="submit" disabled={loading}>
            {loading ? (
              <>
                <span className="spinner" style={{ width: 18, height: 18, borderColor: 'rgba(255,255,255,0.3)', borderTopColor: 'white' }} />
                Verificando...
              </>
            ) : (
              <>
                <ShieldCheck size={18} />
                Ingresar al sistema
              </>
            )}
          </button>
        </form>

        {/* Security note */}
        <div className="login-footer-note" style={{ marginTop: 24 }}>
          <Lock size={12} />
          Acceso restringido — solo personal autorizado
        </div>
      </div>
    </div>
  );
}
