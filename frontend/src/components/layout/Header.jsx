import { Bell, RefreshCw, User } from 'lucide-react';
import { useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const routeTitles = {
  '/': { title: 'Dashboard', subtitle: 'Resumen del sistema' },
  '/clientes': { title: 'Clientes', subtitle: 'Gestión de clientes registrados' },
  '/vehiculos': { title: 'Vehículos', subtitle: 'Flota y gestión de vehículos' },
  '/mantenimientos': { title: 'Mantenimientos', subtitle: 'Historial y registro de servicios' },
  '/empleados': { title: 'Empleados', subtitle: 'Gestión del equipo de trabajo' },
};

/**
 * Componente React: Header.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de Header
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente Header.
 */
export default function Header({ onRefresh }) {
  const { pathname } = useLocation();
  const { user } = useAuth();

  // Match route even if nested (e.g. /vehiculos/1)
  const matchedKey = Object.keys(routeTitles)
    .filter((k) => k !== '/')
    .find((k) => pathname.startsWith(k)) ?? (pathname === '/' ? '/' : '/');

  const { title, subtitle } = routeTitles[matchedKey] ?? { title: 'TallerApp', subtitle: '' };

  const now = new Date().toLocaleDateString('es-AR', {
    weekday: 'short',
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });

  return (
    <header className="header" role="banner">
      <div className="header-left">
        <h2>{title}</h2>
        <p>{subtitle} · {now}</p>
      </div>

      <div className="header-right">
        <div className="header-status" aria-label="Estado del sistema: en línea">
          <span className="status-dot" aria-hidden="true" />
          API conectada
        </div>

        {onRefresh && (
          <button
            className="header-btn"
            onClick={onRefresh}
            aria-label="Actualizar datos"
            title="Actualizar"
          >
            <RefreshCw size={16} />
          </button>
        )}

        {user && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: 7,
              padding: '5px 12px',
              background: 'var(--bg-elevated)',
              border: '1px solid var(--border)',
              borderRadius: 'var(--radius-md)',
              fontSize: 13,
              fontWeight: 500,
              color: 'var(--text-secondary)',
            }}
            aria-label={`Usuario activo: ${user.nombre}`}
          >
            <User size={13} color="var(--accent)" />
            <span>{user.nombre ?? user.usuario}</span>
          </div>
        )}

        <button className="header-btn" aria-label="Notificaciones" title="Notificaciones">
          <Bell size={16} />
        </button>
      </div>
    </header>
  );
}
